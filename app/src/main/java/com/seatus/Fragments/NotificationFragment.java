package com.seatus.Fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Fragments.Driver.DriverOfferFragment;
import com.seatus.Fragments.Passenger.PassengerOfferFragment;
import com.seatus.Holders.NotificationHolder;
import com.seatus.Interfaces.EndlessRecyclerViewScrollListener;
import com.seatus.Models.CardItem;
import com.seatus.Models.NotifObject;
import com.seatus.Models.NotificationListingItem;
import com.seatus.R;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.seatus.Models.NotifObject.ACTION_ACCEPTED_DRIVER;
import static com.seatus.Models.NotifObject.ACTION_NEW_DRIVER_INVITE;
import static com.seatus.Models.NotifObject.ACTION_NEW_INVITE;
import static com.seatus.Models.NotifObject.ACTION_NEW_MESSAGE;
import static com.seatus.Models.NotifObject.ACTION_NEW_OFFER;

/**
 * Created by saqib on 1/29/2018.
 */

public class NotificationFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private EfficientRecyclerAdapter<NotificationListingItem> mAdp;
    public ArrayList<NotificationListingItem> mList = new ArrayList<>();
    public static int MAX_LIMIT = 20;

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        if (!isLoaded)
            getNotifications(1);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_notification;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.resetTitleBar().setTitle("notifications").enableMenu();
    }

    @Override
    public void inits() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        mAdp = new EfficientRecyclerAdapter(R.layout.item_notifications, NotificationHolder.class, mList);
        recyclerView.setAdapter(mAdp);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getNotifications(page);
            }

        });
    }

    @Override
    public void setEvents() {

        mAdp.setOnItemClickListener((adapter, view1, object, position) -> {
            try {
                Long currTime = StaticMethods.getUTCTimestamp();
                switch (object.payload.data.getAction()) {
                    case ACTION_NEW_MESSAGE:
                        getFragmentActivity().replaceFragmentWithBackstack(ChatFragment.newInstance(object.payload.data.trip_id));
                        break;
                    case ACTION_NEW_OFFER:
                        if (object.payload.data.isForPassenger())
                            getFragmentActivity().replaceFragmentWithBackstack(PassengerOfferFragment.newInstance(object.payload.data.trip_id, object.payload.data.getOtherUserId()));
                        else
                            getFragmentActivity().replaceFragmentWithBackstack(DriverOfferFragment.newInstance(object.payload.data.trip_id, object.payload.data.getOtherUserId()));
                        break;

                    case NotifObject.ACTION_NEW_INVITE:
                        if (currTime - Long.valueOf(object.unix_timestamp + "000") < 1 * 60 * 60 * 1000)
                            DialogHelper.showInviteDialog(getContext(), getUserItem(), object.payload.data);
                        break;
                    case NotifObject.ACTION_NEW_DRIVER_INVITE:
                        if (currTime - Long.valueOf(object.unix_timestamp + "000") < 1 * 60 * 60 * 1000)
                            DialogHelper.showInviteForDriverDialog(getContext(), getUserItem(), object.payload.data);
                        break;
                    case ACTION_ACCEPTED_DRIVER:
                        if (currTime - Long.valueOf(object.unix_timestamp + "000") < 24 * 60 * 60 * 1000)
                           // DialogHelper.showPassengerVerifyOffer(getContext(), getUserItem(), object.payload.data);
                            getCreditCards(object.payload.data);
                        break;
                    default:
                        if (object.payload.data.isTripDetailNotif())
                            getFragmentActivity().replaceFragmentWithBackstack(RideDetailFragment.newInstance(object.payload.data.trip_id));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getCreditCards(NotifObject object) {

        getActivityViewModel().getCreditCard().observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    List<CardItem> mList = webResponseResource.data.body;
                    for (int a = 0; a < mList.size(); a++) {
                        CardItem cardItem = mList.get(a);
                        if (cardItem.is_default) {
                            object.card_number = "*** **** " + cardItem.last_digits;
                            DialogHelper.showPassengerVerifyOffer(getContext(), getUserItem(), object);
                        }
                    }
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }


    private void getNotifications(int page) {

        getActivityViewModel().getNotifications(getUserItem().getRoleString(), MAX_LIMIT, page).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    int curSize = mAdp.getItemCount();
                    mList.addAll(webResponseResource.data.body);
                    view.post(() -> mAdp.notifyItemRangeInserted(curSize, mList.size() - 1));
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }
}
