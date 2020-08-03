package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Fragments.Driver.DriverOfferFragment;
import com.seatus.Holders.OffersDriverListingHolder;
import com.seatus.Holders.OffersPassengerListingHolder;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.Help;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by saqib on 11/21/2017.
 */

public class OffersListingFragment extends BaseFragment {
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private EfficientRecyclerAdapter<TripItem> mAdp;
    public static ArrayList<TripItem> mList = new ArrayList<>();

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        fetchOffers();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_offers;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.enableInfo(Help.MyTrips);
    }

    @Override
    public void inits() {
        StaticMethods.initVerticalRecycler(getContext(), true, recyclerview);
        if (getUserItem().getCurrentInterface())
            mAdp = new EfficientRecyclerAdapter<TripItem>(R.layout.item_offers_passenger_fragment, OffersPassengerListingHolder.class, mList);
        else
            mAdp = new EfficientRecyclerAdapter<TripItem>(R.layout.item_offers_driver_fragment, OffersDriverListingHolder.class, mList);
        recyclerview.setAdapter(mAdp);

        refreshLayout.setOnRefreshListener(() -> {
            fetchOffers();
        });
    }

    @Override
    public void setEvents() {
        mAdp.setOnItemClickListener((adapter, view1, object, position) -> {
            if (getUserItem().getCurrentInterface())
                getFragmentActivity().replaceFragmentWithBackstack(PassengerOfferFragment.newInstance(object.trip_id, object.getOtherUserId()));
            else
                getFragmentActivity().replaceFragmentWithBackstack(DriverOfferFragment.newInstance(object.trip_id, object.getOtherUserId()));
        });

        AppStore.getInstance().getNotificationItem().observe(this, notifObject -> {
            fetchOffers();
        });
    }

    private void fetchOffers() {

        getActivityViewModel().getOffers(getUserItem().getRoleString()).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    if (mList.isEmpty())
                        showLoader();
                    break;
                case success:
                    refreshLayout.setRefreshing(false);
                    hideLoader();
                    mList.clear();
                    mList.addAll(webResponseResource.data.body);
                    mAdp.notifyDataSetChanged();
                    break;
                default:
                    refreshLayout.setRefreshing(false);
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });

    }

}
