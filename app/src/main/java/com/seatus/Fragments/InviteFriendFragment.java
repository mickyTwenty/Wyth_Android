package com.seatus.Fragments;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.seatus.Adapters.FriendsAdapter;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Holders.AddFriendsHolder;
import com.seatus.Models.SearchFilterDataItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.database.AppDatabase;
import com.seatus.Views.RippleView;
import com.seatus.Views.TitleBar;
import com.seatus.enums.InviteCategory;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.seatus.Utils.StaticAppMethods.deleteRequestData;

/**
 */

public class InviteFriendFragment extends BaseFragment {

    @BindView(R.id.searchView)
    AppCompatAutoCompleteTextView searchView;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.btn_positive)
    RippleView btnPositive;

    private EfficientRecyclerAdapter mAdp;
    ArrayList<UserItem> mList;

    public SearchFilterDataItem dataItem;
    private ListenerRegistration invitesListener;

    public static InviteFriendFragment newInstance(SearchFilterDataItem item) {
        InviteFriendFragment fragment = new InviteFriendFragment();
        fragment.dataItem = item;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

        showLoader();
        searchView.showDropDown();

        StaticAppMethods.fetchDocument(document -> {
            hideLoader();
            if (document == null) {
                makeConnectionSnackbar();
                return;
            }

            if (!TextUtils.isEmpty(document.invite_type) && !document.invite_type.equals(dataItem.inviteType.name())) {
                new AlertDialog.Builder(getContext()).setTitle("Previous Pending Invites").setMessage("You currently have pending invites of another category, do you want to cancel previous invites and proceed ?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            deleteRequestData(() -> setInvitesListener());
                        }).setNegativeButton("No", (dialogInterface, i) -> getFragmentActivity().actionBack()).show();
            } else {
                if (document.isExpired()) {
                    makeSnackbar("Previous Invites Expired !");
                    deleteRequestData(() -> setInvitesListener());
                } else
                    setInvitesListener();
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_invite_friend;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("add your friends").enableBack();
    }

    @Override
    public void inits() {
        mList = new ArrayList<>();
        mAdp = new EfficientRecyclerAdapter<>(R.layout.item_addfriend, AddFriendsHolder.class, mList);
        StaticMethods.initVerticalRecycler(getContext(), recyclerview);
        recyclerview.setAdapter(mAdp);
        searchView.setAdapter(new FriendsAdapter(getContext(), AppDatabase.getInstance(getContext()).friendsDao().getAll()));
    }

    @Override
    public void setEvents() {
        searchView.setOnClickListener(v -> {
            searchView.showDropDown();
        });

        searchView.setOnItemClickListener((adapterView, view, i, l) -> {
            UserItem item = (UserItem) adapterView.getAdapter().getItem(i);
            searchView.setText("");
            showLoader();
            StaticAppMethods.fetchDocument(document -> {

                if (document == null) {
                    hideLoader();
                    makeConnectionSnackbar();
                    return;
                }

                if (document.isExpired()) {
                    makeSnackbar("Previous Invites Expired !");
                    showLoader();
                    deleteRequestData(() -> {
                        hideLoader();
                        inviteUser(item);
                    });
                } else {
                    hideLoader();
                    inviteUser(item);
                }
            });
        });

        mAdp.setOnItemLongClickListener((adapter, view1, object, position) -> {
            UserItem friendItem = (UserItem) object;
            new AlertDialog.Builder(getContext()).setMessage("Do you want to cancel this invitation?").setPositiveButton("Yes", (dialog, which) -> {
                showLoader();
                FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).collection(AppConstants.STORE_COLLECTION_INVITED_MEMBERS).document(friendItem.user_id).delete().addOnCompleteListener(task -> hideLoader());
            }).setNegativeButton("No", null).show();
        });
    }

    private void inviteUser(UserItem item) {
        SearchFilterDataItem filters = PreferencesManager.getObject(AppConstants.KEY_TEMP_FILTERS, SearchFilterDataItem.class);
        Map<String, Object> map = new HashMap<>();
        map.put("trip_search_data", filters.toMap(getUserItem().getFull_name(), getUserItem().user_id));
        map.put("last_req_edited", FieldValue.serverTimestamp());
        map.put("invite_type", dataItem.inviteType == null ? InviteCategory.user_search : dataItem.inviteType.name());
        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).collection(AppConstants.STORE_COLLECTION_INVITED_MEMBERS).document(item.user_id).set(item.toMap(), SetOptions.merge());
        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).update(map);
    }

    @OnClick({R.id.btn_positive, R.id.btn_invite})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_positive:
                getFragmentActivity().actionBack();
                break;
            case R.id.btn_invite:
                StaticMethods.shareIntent(getContext());
                break;
        }
    }

    private void setInvitesListener() {
        invitesListener = FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).collection(AppConstants.STORE_COLLECTION_INVITED_MEMBERS).addSnapshotListener((documentSnapshots, e) -> {
            if (e == null) {
                mList.clear();
                mList.addAll(documentSnapshots.toObjects(UserItem.class));
                mAdp.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            invitesListener.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
