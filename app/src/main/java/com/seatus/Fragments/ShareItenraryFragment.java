package com.seatus.Fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.seatus.Adapters.FriendsAdapter;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Holders.ItineraryHolder;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.database.AppDatabase;
import com.seatus.Views.RippleView;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by saqib on 11/27/2017.
 */

public class ShareItenraryFragment extends BaseFragment {


    @BindView(R.id.searchView)
    AppCompatAutoCompleteTextView searchView;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    ListPopupWindow popupWindow;
    @BindView(R.id.layout_edit)
    LinearLayout layoutEdit;
    @BindView(R.id.btn_sendNow)
    RippleView btnSendNow;
    Unbinder unbinder;
    private EfficientRecyclerAdapter mAdp;
    ArrayList<UserItem> mList;
    private ArrayAdapter popupAdapter;

    public TripItem tripItem;
    private boolean isEdit;


    public static ShareItenraryFragment newInstance(TripItem item, boolean isEdit) {
        ShareItenraryFragment fragment = new ShareItenraryFragment();
        fragment.tripItem = item;
        fragment.isEdit = isEdit;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        searchView.showDropDown();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_itinerary_detail;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle(isEdit ? "itinerary detail" : "shared itinerary details").enableBack();
    }

    @Override
    public void inits() {
        mList = new ArrayList<>();
        mAdp = new EfficientRecyclerAdapter<>(R.layout.item_itinerary_detail, ItineraryHolder.class, mList);
        StaticMethods.initVerticalRecycler(getContext(), true, recyclerview);
        recyclerview.setAdapter(mAdp);


        if (tripItem.itinerary_shared != null && !tripItem.itinerary_shared.isEmpty()) {
            mList.addAll(tripItem.itinerary_shared);
            convertPhoneMobile();
            mAdp.notifyDataSetChanged();
        }

        if (!isEdit) {
            layoutEdit.setVisibility(View.GONE);
            btnSendNow.setVisibility(View.GONE);
        } else
            searchView.setAdapter(new FriendsAdapter(getContext(), AppDatabase.getInstance(getContext()).friendsDao().getAllPlusThirdParty()));
    }

    @Override
    public void setEvents() {

        if (isEdit) {
            searchView.setOnClickListener(v -> {
                searchView.showDropDown();
            });

            searchView.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
                UserItem item = (UserItem) adapterView.getAdapter().getItem(i);
                searchView.setText("");
                addUser(item);
            });
        }
    }


    @OnClick({R.id.btn_add_frnd, R.id.btn_sendNow, R.id.btn_add_custom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_frnd:
                showFriendsPopup(view);
                break;
            case R.id.btn_sendNow:
                actionSend();
                break;
            case R.id.btn_add_custom:
                DialogHelper.showInviteItineraryDialog(getContext(), (dialog, user) -> {
                    addUser(user);
                });
                break;
        }
    }

    private void actionSend() {

        if (!mList.isEmpty()) {
            convertPhoneMobile();
            removeAlreadyInvited();
            getActivityViewModel().shareItinerary(getUserItem().getRoleString(), tripItem.trip_id, getGson().toJson(mList)).observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case success:
                        hideLoader();
                        makeSnackbar(webResponseResource.data.message);
                        getFragmentActivity().actionBack();
                        break;
                    default:
                        hideLoader();
                        makeSnackbar(webResponseResource.data.message);
                        break;
                }
            });
        }
    }

    private void removeAlreadyInvited() {
        if (tripItem.itinerary_shared != null && !tripItem.itinerary_shared.isEmpty())
            for (UserItem user : new ArrayList<>(mList)) {
                if (tripItem.itinerary_shared.contains(user))
                    mList.remove(user);
            }
    }

    private void convertPhoneMobile() {
        for (UserItem user : mList)
            if (TextUtils.isEmpty(user.phone))
                user.phone = user.mobile;
            else
                user.mobile = user.phone;

    }

//    private String getUserJson() {
//        JsonArray arr = new JsonArray();
//        for (UserItem item : mList) {
//            arr.add(item.toTempJson());
//
//        }
//        return arr.toString();
//    }

    private void addUser(UserItem item) {
        if (isUnique(item)) {
            mList.add(item);
            mAdp.notifyDataSetChanged();
            addToRecent(item);
        }
    }

    private void addToRecent(UserItem item) {
        UserItem likeItem = AppDatabase.getInstance(getContext()).friendsDao().hasLikeUser(item.email, item.phone);
        if (likeItem == null)
            AppDatabase.getInstance(getContext()).friendsDao().insert(item);
        AppDatabase.getInstance(getContext()).friendsDao().setRecentSearch(item.user_id);
    }

    private boolean isUnique(UserItem user) {
        for (UserItem item : mList) {
            if (item.phone.equals(user.phone) || item.email.equals(user.email))
                return false;
        }
        return true;
    }

    private void showFriendsPopup(View anchorView) {

        List<UserItem> list = AppDatabase.getInstance(getContext()).friendsDao().getRecentSearch();
        if (list == null || list.isEmpty()) {
            makeSnackbar("No Recent Shares");
            return;
        }
        popupAdapter = new ArrayAdapter(getContext(), R.layout.item_text, list);
        popupWindow = new ListPopupWindow(getContext());
        popupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.opac_black)));
        popupWindow.setWidth(600);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnchorView(anchorView);
        popupWindow.setAdapter(popupAdapter);
        popupWindow.setOnItemClickListener((parent, view1, position, id) -> {
            UserItem item = (UserItem) popupAdapter.getItem(position);
            if (isUnique(item)) {
                mList.add(item);
                mAdp.notifyDataSetChanged();
            }
            popupWindow.dismiss();
        });
        popupWindow.show();
    }

}
