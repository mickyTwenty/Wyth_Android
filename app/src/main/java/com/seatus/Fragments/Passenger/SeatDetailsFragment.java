package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Fragments.Driver.OtherDriverProfileFragment;
import com.seatus.Fragments.OtherProfileFragment;
import com.seatus.Holders.RideSeatsDetailHolder;
import com.seatus.Models.RideSeatItem;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.DialogHelper;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientAdapter;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by saqib on 11/21/2017.
 */

public class SeatDetailsFragment extends BaseFragment implements EfficientAdapter.OnItemClickListener<UserItem> {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private EfficientRecyclerAdapter<UserItem> adapter;

    public TripItem tripItem;

    public static SeatDetailsFragment newInstance(TripItem item) {
        SeatDetailsFragment fragment = new SeatDetailsFragment();
        fragment.tripItem = item;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_ride_seats_detail;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("seat details").enableBack();
        if (!getUserItem().getCurrentInterface() && tripItem.driver.user_id.equals(getUserItem().user_id) && isEditableStatus())
            titleBar.enableRightButton(R.drawable.actionbar_edit, v -> {
                DialogHelper.showEditSeatsDialog(getContext(), tripItem.seats_total, (dialog, seats) -> {
                    dialog.dismiss();
                    getActivityViewModel().editSeats(tripItem.trip_id, seats).observe(this, webResponseResource -> {
                        switch (webResponseResource.status) {
                            case loading:
                                showLoader();
                                break;
                            case success:
                                hideLoader();
                                makeSnackbar(webResponseResource.data);
                                getFragmentActivity().actionBack();
                                break;
                            default:
                                hideLoader();
                                makeSnackbar(webResponseResource.data);
                                break;
                        }
                    });
                });
            });
    }

    private boolean isEditableStatus() {
        if (tripItem.getTripStatus() == TripItem.TripStatus.pending || tripItem.getTripStatus() == TripItem.TripStatus.active || tripItem.getTripStatus() == TripItem.TripStatus.filled)
            return true;
        else
            return false;
    }

    @Override
    public void inits() {
        setRideSeatsList();
    }

    @Override
    public void setEvents() {
        adapter.setOnItemClickListener(this);
        adapter.setOnItemClickListener((adapter1, view1, object, position) -> {
            if (!TextUtils.isEmpty(object.user_id))
                if (position == 0)
                    getFragmentActivity().replaceFragmentWithBackstack(OtherDriverProfileFragment.newInstance(object.user_id));
                else
                    getFragmentActivity().replaceFragmentWithBackstack(OtherProfileFragment.newInstance(object.user_id));
        });
    }

    private void setRideSeatsList() {
        recyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));

        for (int a = 0; a < tripItem.seats_available; a++) {
            UserItem userItem = new UserItem();
            userItem.first_name = "XXX";
            tripItem.passengers.add(userItem);
        }

        ArrayList<UserItem> list = new ArrayList<>();
        if (tripItem.driver != null) {
            UserItem driver = tripItem.driver;
            driver.state_text = "Available";
            driver.is_third_party = true;
            list.add(driver);
        } else {
            UserItem driver = new UserItem();
            driver.state_text = "Not Available";
            driver.is_third_party = true;
            list.add(driver);
        }
        list.addAll(tripItem.passengers);

        adapter = new EfficientRecyclerAdapter<UserItem>(R.layout.item_ride_seats_detail, RideSeatsDetailHolder.class, list);
        recyclerview.setAdapter(adapter);
    }

    @Override
    public void onItemClick(EfficientAdapter<UserItem> adapter, View view, UserItem object, int position) {
        if (getUserItem().getCurrentInterface() || TextUtils.isEmpty(object.user_id) || object.is_confirmed)
            return;

        new AlertDialog.Builder(getContext()).setTitle("Cancel Reservation").setMessage("Do you want to cancel reservation for " + object.getFull_name() + " ?").setNegativeButton("No", (dialog, which) -> Log.v("KickDialog", "dismiss"))
                .setPositiveButton("Yes", (dialog, which) -> {

                    if (!TextUtils.isEmpty(object.group_id)) {
                        new AlertDialog.Builder(getContext()).setTitle("Group User").setMessage("The selected user is member of a group, Canceling this reservation will also cancel the bookings/reservations of other users of this group, Are you sure u want to proceed ?").setNegativeButton("No", (dialog2, which2) -> Log.v("KickDialog", "dismiss"))
                                .setPositiveButton("Yes", (dialog1, which1) -> {
                                    getActivityViewModel().kickPassenger(tripItem.trip_id, object.user_id).observe(this, webResponseResource -> {
                                        switch (webResponseResource.status) {
                                            case loading:
                                                showLoader();
                                                break;
                                            case success:
                                                hideLoader();
                                                break;
                                            default:
                                                hideLoader();
                                                break;
                                        }
                                    });
                                }).show();
                    } else
                        getActivityViewModel().kickPassenger(tripItem.trip_id, object.user_id).observe(this, webResponseResource -> {
                            switch (webResponseResource.status) {
                                case loading:
                                    showLoader();
                                    break;
                                case success:
                                    hideLoader();
                                    break;
                                default:
                                    hideLoader();
                                    break;
                            }
                        });
                }).show();
    }
}
