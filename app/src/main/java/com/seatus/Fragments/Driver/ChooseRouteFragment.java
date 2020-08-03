package com.seatus.Fragments.Driver;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Dialogs.PaymentTypeDialog;
import com.seatus.Fragments.Passenger.BankInfoFragment;
import com.seatus.Fragments.Passenger.MyTripsFragment;
import com.seatus.Holders.RouteHolder;
import com.seatus.Models.SearchFilterDataItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.DateTimeHelper;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientAdapter;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.akexorcist.googledirection.util.DirectionConverter.*;
import static com.seatus.Utils.AppConstants.LOCAL_TRIP_RANGE;

/**
 * Created by rah on 20-Nov-17.
 */

public class ChooseRouteFragment extends BaseFragment implements OnMapReadyCallback, DirectionCallback, EfficientAdapter.OnItemClickListener {

    public static final String ARGUMENT_DATA_ITEM = "argument_data";

    SearchFilterDataItem dataItem;
    GoogleMap googleMap;

    EfficientRecyclerAdapter mAdp;
    ArrayList<Route> mList = new ArrayList<>();
    Route selectedRoute;

    Date myTime;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    public static ChooseRouteFragment newInstance(SearchFilterDataItem item) {
        Bundle args = new Bundle();
        args.putString(ARGUMENT_DATA_ITEM, new Gson().toJson(item));
        ChooseRouteFragment fragment = new ChooseRouteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_choose_route;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("route selection").enableBack();
    }

    @Override
    public void inits() {

        String arg = getArguments().getString(ARGUMENT_DATA_ITEM);
        if (arg != null)
            dataItem = getGson().fromJson(arg, SearchFilterDataItem.class);
        else {
            makeSnackbar("Unable to find data");
            getFragmentActivity().actionBack();
        }

        StaticMethods.initVerticalRecycler(getContext(), true, recycler);
        mAdp = new EfficientRecyclerAdapter(R.layout.item_route_selection, RouteHolder.class, mList);
        recycler.setAdapter(mAdp);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void setEvents() {
        mAdp.setOnItemClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mList.isEmpty()) {
            showLoader();
            fetchRoutes();
        }
    }

    private void fetchRoutes() {
        GoogleDirection.withServerKey(getResources().getString(R.string.google_places_key))
                .from(StaticMethods.parseLatLng(dataItem.origin_geo))
                .to(StaticMethods.parseLatLng(dataItem.destination_geo))
                .alternativeRoute(true)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onItemClick(EfficientAdapter adapter, View view, Object object, int position) {
        Route route = (Route) object;
        setSelectedRoute(position);
        googleMap.clear();
        googleMap.addPolyline(createPolyline(getContext(), route.getAllCoordinates(), 5, ContextCompat.getColor(getContext(), R.color.colorAccent)));
        try {
            googleMap.addMarker(new MarkerOptions().position(StaticMethods.parseLatLng(dataItem.origin_geo)).title("Origin").snippet(dataItem.origin_text));
            googleMap.addMarker(new MarkerOptions().position(StaticMethods.parseLatLng(dataItem.destination_geo)).title("Destination").snippet(dataItem.destination_text));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(route.getLatLngBounds(), 300));
        } catch (Exception e) {
            e.printStackTrace();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(route.getLatLngBounds(), 10));
        }
    }

    private void setSelectedRoute(int position) {
        for (int i = 0; i < mList.size(); i++) {
            if (i == position)
                mList.get(i).isSelected = true;
            else
                mList.get(i).isSelected = false;
        }

        selectedRoute = mList.get(position);
        mAdp.notifyDataSetChanged();
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK() && direction.getRouteList().size() > 0) {
            mList.clear();
            mList.addAll(direction.getRouteList());
            mAdp.notifyDataSetChanged();
            onItemClick(null, null, mList.get(0), 0);
            getRoutesPopulatiry();
        } else {
            hideLoader();
            makeSnackbar("No Routes Found");
        }
    }

    private void getRoutesPopulatiry() {
        getActivityViewModel().getRoutesPopularity(mList).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case success:
                    hideLoader();
                    if (webResponseResource.data != null && webResponseResource.data.body.popularity != null) {
                        for (int i = 0; i < mList.size(); i++) {
                            if (webResponseResource.data.body.popularity.has("index_" + i))
                                mList.get(i).popularity = webResponseResource.data.body.popularity.get("index_" + i).getAsLong();
                        }
                        mAdp.notifyDataSetChanged();
                    }
                    break;
                default:
                    hideLoader();
                    break;
            }
        });
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        hideLoader();
        makeSnackbar("No Routes Found");
    }

    public String selectedPaymentType = "standard";

    @OnClick(R.id.btn_create_trip)
    public void onViewClicked() {

        showLoader();
        StaticAppMethods.fetchValidInvites(getContext(), dataItem, (status, documents) -> {
            switch (status) {
                case connectionError:
//                    makeConnectionSnackbar();
                    if(dataItem.trip_count > 1)
                        createRideStandardPayout(null);
                    else
                        createRide(null);
                    hideLoader();
                    break;
                case noInvites:
                    hideLoader();
                    if(dataItem.trip_count > 1)
                        createRideStandardPayout(null);
                    else
                        createRide(null);
                    break;
                case expiredContinue:
                    hideLoader();
                    if(dataItem.trip_count > 1)
                        createRideStandardPayout(null);
                    else
                        createRide(null);
                    break;
                case expiredAbort:
                    hideLoader();
                    break;
                case invitesPending:
                    hideLoader();
                    DialogHelper.showAlertDialog(getContext(), getString(R.string.error_pending_invites));
                    break;
                case invitesAvailaible:
                    hideLoader();
                    if(dataItem.trip_count > 1)
                        createRideStandardPayout(documents);
                    else
                        createRide(documents);
                    break;
            }
        });

    }

    private void createRide(List<UserItem> documents) {

                new PaymentTypeDialog.Builder(getContext(), R.style.BounceDialog)
                .setNegativeButton(null)
                .setPositiveButton((dialog, paymentType) -> {

                    dialog.dismiss();
                    selectedPaymentType = paymentType;

                    //region Gathering Params
                    Map<String, Object> map = new HashMap<>();
                    LatLng origin, destination;
                    origin = StaticMethods.parseLatLng(dataItem.origin_geo);
                    destination = StaticMethods.parseLatLng(dataItem.destination_geo);
                    Route.TimeDistanceInfo info = selectedRoute.getRouteTimeDistance();

                    map.put("_token", getUserItem().token);
//        map.put("trip_name", dataItem.trip_name);
                    map.put("origin_latitude", origin.latitude);
                    map.put("origin_longitude", origin.longitude);
                    map.put("origin_title", dataItem.origin_text);
                    map.put("destination_latitude", destination.latitude);
                    map.put("destination_longitude", destination.longitude);
                    map.put("destination_title", dataItem.destination_text);

                    map.put("expected_distance_format", info.getDistanceText());
                    map.put("expected_distance", info.getDistanceValue());

//        map.put("estimates", dataItem.estimate);
//        map.put("estimates_format", dataItem.estimate_format);

                    map.put("expected_duration", info.getTimeText());
//        map.put("expected_duration_format", info.getTimeText());

//        map.put("expected_start_date", DateTimeHelper.getDateToShow(dataItem.getDate()));
//        map.put("expected_start_date", StaticMethods.convertTimeToUTC(dataItem.getDate()));
                    map.put("expected_start_date", dataItem.getDate().getTime());


                    map.put("time_range", dataItem.time_range);
                    map.put("seats_total", dataItem.seats);
                    map.put("is_roundtrip", dataItem.isRoundTrip);
//        map.put("is_enabled_booknow", dataItem.book_now);
                    if(info.getDistanceValue() < LOCAL_TRIP_RANGE) {
                        map.put("is_enabled_booknow", 1);
                    } else {
                        map.put("is_enabled_booknow", 0);
                    }
                    map.put("booknow_price", 5.0);

//        map.put("stepped_route", getGson().toJson(selectedRoute.getAllCoordinates(500)));
                    map.put("stepped_route", selectedRoute.getOverviewPolyline().getRawPointList());
                    map.put("preferences", dataItem.preferences);
                    map.put("desired_gender", StaticAppMethods.getGenderInt(dataItem.gender));

                    map.put("payout_type", selectedPaymentType);


                    if (selectedRoute == null) {
                        fetchRoutes();
                        return;
                    }

                    if (documents != null)
                        map.put("invited_members", StaticAppMethods.getAcceptedUserIds(documents));


                    //new fields
                    map.put("min_estimates", String.format("%.2f", dataItem.estimate_low));
                    map.put("max_estimates", String.format("%.2f", dataItem.estimate_high));
                    if (dataItem.isRoundTrip == 1) {
//            map.put("date_returning", StaticMethods.convertTimeToUTC(dataItem.getReturnDate()));
                        map.put("date_returning", dataItem.getReturnDate().getTime());
                        map.put("time_range_returning", dataItem.return_time_range);
                        map.put("seats_total_returning", dataItem.return_seats);
                    }

                    // endregion
                    getActivityViewModel().createRideDriver(map).observe(this, webResponseResource -> {
                        switch (webResponseResource.status) {
                            case loading:
                                showLoader();
                                break;
                            case success:
                                StaticAppMethods.fireBaseAnalytics(getContext(), "create_driver", map);
                                makeSnackbar(webResponseResource.data);
                                StaticAppMethods.deleteRequestData(null);
                                getFragmentActivity().replaceFragment(MyTripsFragment.newInstance(1));
                                hideLoader();

                                break;
                            case add_bank_account:
                                makeSnackbar(webResponseResource.data);
                                getFragmentActivity().replaceFragmentWithBackstack(BankInfoFragment.newInstance(true));
                                break;
                            default:
                                hideLoader();
                                makeSnackbar(webResponseResource.data);
                                break;
                        }
                    });
                }).show();


    }

    private void createRideStandardPayout(List<UserItem> documents) {

        selectedPaymentType = "standard";

        //region Gathering Params
        Map<String, Object> map = new HashMap<>();
        LatLng origin, destination;
        origin = StaticMethods.parseLatLng(dataItem.origin_geo);
        destination = StaticMethods.parseLatLng(dataItem.destination_geo);
        Route.TimeDistanceInfo info = selectedRoute.getRouteTimeDistance();

        map.put("_token", getUserItem().token);
//        map.put("trip_name", dataItem.trip_name);
        map.put("origin_latitude", origin.latitude);
        map.put("origin_longitude", origin.longitude);
        map.put("origin_title", dataItem.origin_text);
        map.put("destination_latitude", destination.latitude);
        map.put("destination_longitude", destination.longitude);
        map.put("destination_title", dataItem.destination_text);

        map.put("expected_distance_format", info.getDistanceText());
        map.put("expected_distance", info.getDistanceValue());

//        map.put("estimates", dataItem.estimate);
//        map.put("estimates_format", dataItem.estimate_format);

        map.put("expected_duration", info.getTimeText());
//        map.put("expected_duration_format", info.getTimeText());

//        map.put("expected_start_date", DateTimeHelper.getDateToShow(dataItem.getDate()));
//        map.put("expected_start_date", StaticMethods.convertTimeToUTC(dataItem.getDate()));
        map.put("expected_start_date", dataItem.getDate().getTime());

        // Local Trip Dialog for < 10 miles
        //if(info.getDistanceValue() < LOCAL_TRIP_RANGE) {
        //    StaticMethods.timePopup(getContext(), dataItem.getDate().getTime(), cal -> tripTimeSelected(cal));
        //    map.put("expected_start_time", myTime);
        //}

        map.put("time_range", dataItem.time_range);
        map.put("seats_total", dataItem.seats);
        map.put("is_roundtrip", dataItem.isRoundTrip);
//        map.put("is_enabled_booknow", dataItem.book_now);
        if(info.getDistanceValue() < LOCAL_TRIP_RANGE) {
            map.put("is_enabled_booknow", 1);
        } else {
            map.put("is_enabled_booknow", 0);
        }
        map.put("booknow_price", 5.0);

//        map.put("stepped_route", getGson().toJson(selectedRoute.getAllCoordinates(500)));
        map.put("stepped_route", selectedRoute.getOverviewPolyline().getRawPointList());
        map.put("preferences", dataItem.preferences);
        map.put("desired_gender", StaticAppMethods.getGenderInt(dataItem.gender));

        map.put("payout_type", selectedPaymentType);


        if (selectedRoute == null) {
            fetchRoutes();
            return;
        }

        if (documents != null)
            map.put("invited_members", StaticAppMethods.getAcceptedUserIds(documents));


        //new fields
        map.put("min_estimates", String.format("%.2f", dataItem.estimate_low));
        map.put("max_estimates", String.format("%.2f", dataItem.estimate_high));
        if (dataItem.isRoundTrip == 1) {
//            map.put("date_returning", StaticMethods.convertTimeToUTC(dataItem.getReturnDate()));
            map.put("date_returning", dataItem.getReturnDate().getTime());
            map.put("time_range_returning", dataItem.return_time_range);
            map.put("seats_total_returning", dataItem.return_seats);
        }

        // endregion
        getActivityViewModel().createRideDriver(map).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    StaticAppMethods.fireBaseAnalytics(getContext(), "create_driver", map);
                    makeSnackbar(webResponseResource.data);
                    StaticAppMethods.deleteRequestData(null);
                    if(dataItem.trip_count <= 1)
                    {
                        getFragmentActivity().replaceFragment(MyTripsFragment.newInstance(1));
                    }
                    else
                    {
                        dataItem.trip_count = dataItem.trip_count - 1;
                        createRideStandardPayout(documents);
                    }
                    hideLoader();

                    break;
                case add_bank_account:
                    makeSnackbar(webResponseResource.data);
                    getFragmentActivity().replaceFragmentWithBackstack(BankInfoFragment.newInstance(true));
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }
}
