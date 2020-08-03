package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Holders.MessagingPassengerHolder;
import com.seatus.Holders.MessagingDriverHolder;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.Help;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by saqib on 11/22/2017.
 */

public class MessagingFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private EfficientRecyclerAdapter<TripItem> adapter;
    private ArrayList<TripItem> tripItems;

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_messaging;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("Messaging").enableMenu().enableInfo(Help.Messaging);
    }

    @Override
    public void inits() {
        StaticMethods.initVerticalRecycler(getContext(), true, recyclerview);

        if (getUserItem().getCurrentInterface())
            getCustomerTripsList();
        else getDriverTripsList();
    }

    @Override
    public void setEvents() {

    }

    private void getCustomerTripsList() {
        getActivityViewModel().getPassengerUpComingTripData().observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    tripItems = webResponseResource.data.body;
                    adapter = new EfficientRecyclerAdapter<>(R.layout.item_messaging_passenger, MessagingPassengerHolder.class, tripItems);
                    recyclerview.setAdapter(adapter);
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    private void getDriverTripsList() {
        getActivityViewModel().getDriverUpComingTripData().observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    tripItems = webResponseResource.data.body;
                    adapter = new EfficientRecyclerAdapter<>(R.layout.item_messaging_driver, MessagingDriverHolder.class, tripItems);
                    recyclerview.setAdapter(adapter);
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).update("unread_chats", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
