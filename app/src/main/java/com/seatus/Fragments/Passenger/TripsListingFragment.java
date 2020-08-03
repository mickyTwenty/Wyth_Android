package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.seatus.BaseClasses.BaseLocalFiltersFragment;
import com.seatus.BaseClasses.BaseLocationPickerFragment;
import com.seatus.Holders.PassengerTripsHolder;
import com.seatus.Holders.DriverTripsHolder;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.Help;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saqib on 11/21/2017.
 */

public class TripsListingFragment extends BaseLocationPickerFragment {
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.field_destination)
    EditText fieldDestination;
    @BindView(R.id.field_date)
    EditText fieldDate;
    private EfficientRecyclerAdapter<TripItem> adapter;
    private ArrayList<TripItem> tripItems = new ArrayList<>();
    private String path;

    public static TripsListingFragment newInstance(String path) {
        TripsListingFragment fragment = new TripsListingFragment();
        fragment.path = path;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_trips;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.enableInfo(Help.MyTrips);
    }

    @Override
    public void inits() {

        StaticMethods.initVerticalRecycler(getContext(), true, recyclerview);
        getTripsList();
        refreshLayout.setOnRefreshListener(() -> {
                    fieldDate.setText("");
                    fieldDestination.setText("");
                    fieldDestination.setTag(null);
                    getTripsList();
                }
        );

        if (getUserItem().getCurrentInterface())
            adapter = new EfficientRecyclerAdapter<>(R.layout.item_passenger_trip, PassengerTripsHolder.class, tripItems);
        else
            adapter = new EfficientRecyclerAdapter<>(R.layout.item_driver_trip, DriverTripsHolder.class, tripItems);
        recyclerview.setAdapter(adapter);
    }

    @Override
    public void setEvents() {
        AppStore.getInstance().getNotificationItem().observe(this, notifObject -> {
            getTripsList();
        });
    }

    private void getTripsList() {
        String date = fieldDate.getText().toString();
        String latitude = null;
        String longitude = null;

        String destinationTag = (String) fieldDestination.getTag();
        if (!TextUtils.isEmpty(destinationTag)) {
            LatLng latLng = StaticMethods.parseLatLng(destinationTag);
            latitude = String.valueOf(latLng.latitude);
            longitude = String.valueOf(latLng.longitude);
        }

        getActivityViewModel().getMyTrips(path, latitude, longitude, date).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    refreshLayout.setRefreshing(false);
                    if (webResponseResource.data.body != null) {
                        tripItems.clear();
                        tripItems.addAll(webResponseResource.data.body);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    refreshLayout.setRefreshing(false);
                    hideLoader();
                    break;
            }
        });
    }

    @OnClick({R.id.field_destination, R.id.field_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.field_destination:
                pickDestination();
                break;
            case R.id.field_date:
                StaticMethods.datePopup(getContext(), fieldDate, false, () -> getTripsList());
                break;
        }
    }

    @Override
    protected void onLocationSelected(boolean isOrigin, String address, String geo) {
        fieldDestination.setText(address);
        fieldDestination.setTag(geo);
        getTripsList();
    }

    @Override
    protected void estimateFound(double lowerEstimate, double upperEstimate) {

    }

}
