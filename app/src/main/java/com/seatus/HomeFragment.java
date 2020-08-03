package com.seatus;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;
import com.seatus.Activities.MainActivity;
import com.seatus.BaseClasses.BaseLocationPickerFragment;
import com.seatus.Fragments.CreateTripFragment;
import com.seatus.Fragments.Driver.SearchRidersFragment;
import com.seatus.Fragments.EditProfileFragment;
import com.seatus.Fragments.FindRideFragment;
import com.seatus.Models.TripItem;
import com.seatus.Utils.DateTimeHelper;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.Help;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.RippleView;
import com.seatus.Views.TitleBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

import static com.akexorcist.googledirection.GoogleDirection.withServerKey;
import static com.akexorcist.googledirection.util.DirectionConverter.*;

/**
 * Created by rohail on 3/19/2018.
 */

public class HomeFragment extends BaseLocationPickerFragment implements OnMapReadyCallback, DirectionCallback,
        GoogleMap.OnPoiClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener {

    @BindView(R.id.txt_origin)
    TextView txtOrigin;
    @BindView(R.id.txt_origin_title)
    TextView txtOriginTitle;
    @BindView(R.id.txt_destination)
    TextView txtDestination;
    @BindView(R.id.txt_destination_title)
    TextView txtDestinationTitle;
    @BindView(R.id.view_origin_destin)
    LinearLayout layoutOriginDesitn;
    @BindView(R.id.layout_return_date)
    LinearLayout layoutReturnDate;
    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.txt_return_date)
    TextView txtReturnDate;
    @BindView(R.id.view_left)
    LinearLayout viewLeft;
    @BindView(R.id.txt_estimate)
    TextView txtEstimate;
    @BindView(R.id.switch_roundtrip)
    CheckBox switchRoundtrip;
    @BindView(R.id.btn_search)
    RippleView btnSearch;
    @BindView(R.id.btn_create)
    RippleView btnCreate;
    @BindView(R.id.txt_create)
    TextView txtCreate;
    @BindView(R.id.txt_search)
    TextView txtSearch;

    @BindView(R.id.btn_search_local)
    RippleView btnSearchLocal;
    @BindView(R.id.txt_search_local)
    TextView txtSearchLocal;

    @BindView(R.id.btn_location_picker)
    LinearLayout btn_location_picker;
    @BindView(R.id.txt_origin_picker)
    TextView txt_origin_picker;
    @BindView(R.id.txt_destination_picker)
    TextView txt_destination_picker_picker;

    @BindView(R.id.checkbox_showhide)
    CheckBox checkboxShowhide;
    @BindView(R.id.view_info)
    RelativeLayout viewInfo;
    private GoogleMap googleMap;

    private Marker mOrigin;
    private Marker mDestination;

    private Polyline routeLine;
    private Marker tempMarker;

    private ArrayList<TripItem> tripItems = new ArrayList<>();

    private boolean hideLayoutAfterDirection;

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.resetTitleBar().setTitle(getUserItem().getCurrentInterface() ? "find ride" : "post ride").enableMenu().enableInfo(Help.FindRide);
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        txtSearch.setText(getUserItem().getCurrentInterface() ? "Search Drivers" : "Search Riders");
        txtOriginTitle.setText(getUserItem().getCurrentInterface() ? "Pick-Up" : "Origin");
        txtDestinationTitle.setText(getUserItem().getCurrentInterface() ? "Drop-Off" : "Destination");

        showLocationPicker(false);

        syncFromLocalCache();
        if (!isLoaded)
            fetchRoutes();
    }

    @Override
    public void inits() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void syncFromLocalCache() {
        try {

            if (getTempItem().getDate() != null && getTempItem().getDate().after(Calendar.getInstance().getTime())) {
                txtDate.setText(DateTimeHelper.getDateToShow(getTempItem().getDate()));
                txtDate.setTag(getTempItem().getDate());
            } else {
                Calendar currTime = Calendar.getInstance();
                getTempItem().setDate(currTime.getTime());
                updateTempItem();
                txtDate.setText(DateTimeHelper.getDateToShow(getTempItem().getDate()));
                txtDate.setTag(getTempItem().getDate());
            }

            if (getTempItem().getReturnDate() != null && getTempItem().getReturnDate().after(Calendar.getInstance().getTime())) {
                txtReturnDate.setText(DateTimeHelper.getDateToShow(getTempItem().getReturnDate()));
                txtReturnDate.setTag(getTempItem().getReturnDate());
            }

            if (getTempItem().estimate_high == 0.0d)
                txtEstimate.setText("No Estimate Available");
            else {
                showEstimate(getTempItem().isRoundTrip == 1);
            }


            if (!TextUtils.isEmpty(getTempItem().destination_geo)) {
                txtDestination.setText(getTempItem().destination_text);
                txtDestination.setTag(getTempItem().destination_geo);
            }
            switchRoundtrip.setChecked(getTempItem().isRoundTrip == 1);

            if (!TextUtils.isEmpty(getTempItem().origin_geo)) {
                txtOrigin.setText(getTempItem().origin_text);
                txtOrigin.setTag(getTempItem().origin_geo);
               googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(StaticMethods.parseLatLng(getTempItem().origin_geo), 13));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEstimate(boolean isRound) {
        txtEstimate.setText(StaticAppMethods.getEstimate(isRound ? getTempItem().estimate_low * 2 : getTempItem().estimate_low, isRound ? getTempItem().estimate_high * 2 : getTempItem().estimate_high));
    }

    @Override
    public void setEvents() {
        switchRoundtrip.setOnCheckedChangeListener((buttonView, b) -> {
            layoutReturnDate.setVisibility(b ? View.VISIBLE : View.GONE);
            if (getTempItem().isRoundTrip != (b ? 1 : 0)) {
                if (b) {
                    getTempItem().isRoundTrip = 1;
                    updateTempItem();
                } else {
                    getTempItem().isRoundTrip = 0;
                    updateTempItem();
                }
                showEstimate(b);
                updateTempItem();
            }
        });
        checkboxShowhide.setOnCheckedChangeListener((buttonView, isChecked) -> viewInfo.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Initialize custom Listeners
        googleMap.setOnPoiClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMyLocationButtonClickListener(this);

        // Zoom into New York if nowhere else is loaded
        if (this.googleMap == null)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.713014, -74.000482), 13));

        // Set origin to phone's current Location
        ((MainActivity) getContext()).checkLocationEnabled(() -> {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
                googleMap.setMyLocationEnabled(true);
                if (TextUtils.isEmpty(getTempItem().origin_geo)) {

                    FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), location -> {
                                if (location != null) {
                                    String origin = StaticMethods.getLocationString(getActivity(), location);
                                    getTempItem().origin_text = origin;
                                    getTempItem().origin_geo = location.getLatitude() + "," + location.getLongitude();
                                    updateTempItem();
                                    txtOrigin.setText(getTempItem().origin_text);
                                    txtOrigin.setTag(getTempItem().origin_geo);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 100));
                                }
                            });
                }
            }
        }, true);

        this.googleMap = googleMap;

        loadMarkers();
    }

    @OnClick({R.id.txt_origin, R.id.txt_destination, R.id.txt_date, R.id.txt_return_date,
            R.id.btn_search, R.id.btn_create, R.id.btn_search_local, R.id.txt_origin_picker,
            R.id.txt_destination_picker, R.id.view_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_origin:
                pickOrigin();
                break;
            case R.id.txt_destination:
                pickDestination();
                break;
            case R.id.txt_date:
                StaticMethods.datePopup(getContext(), (TextView) view, () -> {
                    Date date = DateTimeHelper.getNonUTCTimeStamp((Date) txtDate.getTag());
                    getTempItem().setDate(date);
                    updateTempItem();
                });
                break;
            case R.id.txt_return_date:
                StaticMethods.datePopup(getContext(), (TextView) view, () -> {
                    Date date = DateTimeHelper.getNonUTCTimeStamp((Date) txtReturnDate.getTag());
                    getTempItem().setReturnDate(date);
                    updateTempItem();
                });
                break;
            case R.id.btn_search_local:
                quickSearch();
                break;
            case R.id.txt_origin_picker:
                quickOriginPicker(tempMarker);
                break;
            case R.id.txt_destination_picker:
                quickDestinationPicker(tempMarker);
                break;
            case R.id.btn_search:
                showAllSearch();
                if (getUserItem().isProfileInComplete()) {
                    DialogHelper.showAlertDialog(getContext(), "Your Profile is incomplete, do you wish to complete your profile ?", (dialog, which) -> {
                        getFragmentActivity().replaceFragmentWithBackstack(new EditProfileFragment());
                    });
                    return;
                }
                if (areInitialFieldsValid(false))
                    getFragmentActivity().replaceFragmentWithBackstack(getUserItem().getCurrentInterface() ? new FindRideFragment() : new SearchRidersFragment());
                break;
            case R.id.btn_create:
                if (getUserItem().isProfileInComplete()) {
                    DialogHelper.showAlertDialog(getContext(), "Your Profile is incomplete, do you wish to complete your profile ?", (dialog, which) -> {
                        getFragmentActivity().replaceFragmentWithBackstack(new EditProfileFragment());
                    });
                    return;
                }
                if (areInitialFieldsValid(true))
                    getFragmentActivity().replaceFragmentWithBackstack(new CreateTripFragment());
                break;
            case R.id.view_left:
                flipOriginDestination();
        }
    }

    private void flipOriginDestination() {
        String tempText = getTempItem().origin_text;
        String tempGeo = getTempItem().origin_geo;

        getTempItem().origin_text = getTempItem().destination_text;
        getTempItem().origin_geo = getTempItem().destination_geo;
        updateTempItem();
        txtOrigin.setText(getTempItem().origin_text);
        txtOrigin.setTag(getTempItem().origin_geo);

        getTempItem().destination_text = tempText;
        getTempItem().destination_geo = tempGeo;
        updateTempItem();
        txtDestination.setText(getTempItem().destination_text);
        txtDestination.setTag(getTempItem().destination_geo);

        fetchRoutes();
    }

    private void quickSearch() {

        if(!getUserItem().getCurrentInterface())
            return;

        String currentDateTime = DateTimeHelper.currentDateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeHelper.parseDate(currentDateTime,"yyyy-MM-dd hh:mm:ss"));
        long currentTime = calendar.getTimeInMillis();

        getActivityViewModel().getMyTrips("passenger/upcoming", "", "", "").observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    if (webResponseResource.data.body != null) {
                        tripItems.addAll(webResponseResource.data.body);

                        long mostRecentTime = 0;
                        String recentOriginTitle = "";
                        LatLng recentOriginGeo = null;
                        String recentDestinationTitle = "";
                        LatLng recentDestinationGeo = null;
                        for (TripItem trip:tripItems) {
                            String endDate = trip.expected_start_time;

                            if(endDate != null)
                            {
                                Calendar calendarEnd = Calendar.getInstance();
                                calendarEnd.setTime(DateTimeHelper.parseDate(endDate,"yyyy-MM-dd hh:mm:ss"));
                                calendarEnd.add(Calendar.HOUR, 18);
                                long endTime = calendarEnd.getTimeInMillis();

                                if(currentTime < endTime)
                                {
                                    if(endTime > mostRecentTime)
                                    {
                                        mostRecentTime = endTime;

                                        // Because this is the return trip, swap Origin and Destinations
                                        recentOriginTitle = trip.destination_title;
                                        recentOriginGeo = trip.getDestinationLatLng();

                                        recentDestinationTitle = trip.origin_title;
                                        recentDestinationGeo = trip.getOriginLatLng();
                                    }
                                }
                            }

                            // Because this is the return trip, swap Origin and Destinations
                            recentOriginTitle = trip.destination_title;
                            recentOriginGeo = trip.getDestinationLatLng();

                            recentDestinationTitle = trip.origin_title;
                            recentDestinationGeo = trip.getOriginLatLng();
                        }

                        if(recentOriginGeo!=null && recentDestinationGeo!=null)
                        {
                            getTempItem().origin_text = recentOriginTitle;
                            getTempItem().origin_geo = recentOriginGeo.latitude + "," + recentOriginGeo.longitude;
                            updateTempItem();
                            txtOrigin.setText(getTempItem().origin_text);
                            txtOrigin.setTag(getTempItem().origin_geo);

                            getTempItem().destination_text = recentDestinationTitle;
                            getTempItem().destination_geo = recentDestinationGeo.latitude + "," + recentDestinationGeo.longitude;
                            updateTempItem();
                            txtDestination.setText(getTempItem().destination_text);
                            txtDestination.setTag(getTempItem().destination_geo);

                            Calendar currTime = Calendar.getInstance();
                            getTempItem().setDate(currTime.getTime());
                            updateTempItem();
                            txtDate.setText(DateTimeHelper.getDateToShow(getTempItem().getDate()));
                            txtDate.setTag(getTempItem().getDate());

                            fetchRoutes();
                            showAllSearch();

                            if (getUserItem().isProfileInComplete()) {
                                DialogHelper.showAlertDialog(getContext(), "Your Profile is incomplete, do you wish to complete your profile ?", (dialog, which) -> {
                                    getFragmentActivity().replaceFragmentWithBackstack(new EditProfileFragment());
                                });
                                return;
                            }
                            if (areInitialFieldsValid(false))
                                getFragmentActivity().replaceFragmentWithBackstack(getUserItem().getCurrentInterface() ? new FindRideFragment() : new SearchRidersFragment());
                        }
                    }
                    break;
                default:
                    showLoader();
                    break;
            }
        });
    }

    private void quickSearchCheck() {

        btnSearchLocal.setVisibility(View.INVISIBLE);

        if(!getUserItem().getCurrentInterface())
            return;

        String currentDateTime = DateTimeHelper.currentDateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeHelper.parseDate(currentDateTime,"yyyy-MM-dd hh:mm:ss"));
        long currentTime = calendar.getTimeInMillis();

        getActivityViewModel().getMyTrips("passenger/upcoming", "", "", "").observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    btnSearchLocal.setVisibility(View.INVISIBLE);
                    break;
                case success:
                    hideLoader();
                    if (webResponseResource.data.body != null) {
                        tripItems.addAll(webResponseResource.data.body);
                        for (TripItem trip:tripItems) {
                            /*String endDate = trip.ended_at;

                            if(endDate != null)
                            {
                                Calendar calendarEnd = Calendar.getInstance();
                                calendarEnd.setTime(DateTimeHelper.parseDate(endDate,"yyyy-MM-dd hh:mm:ss"));
                                calendarEnd.add(Calendar.HOUR, 4);
                                long endTime = calendarEnd.getTimeInMillis();

                                if(currentTime < endTime)
                                {
                                    btnSearchLocal.setVisibility(View.VISIBLE);
                                }
                            }*/

                            String endDate = trip.expected_start_time;

                            if(endDate != null)
                            {
                                Calendar calendarEnd = Calendar.getInstance();
                                calendarEnd.setTime(DateTimeHelper.parseDate(endDate,"yyyy-MM-dd hh:mm:ss"));
                                calendarEnd.add(Calendar.HOUR, 18);
                                long endTime = calendarEnd.getTimeInMillis();

                                if(currentTime < endTime)
                                {
                                    btnSearchLocal.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    else
                    {
                        btnSearchLocal.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    btnSearchLocal.setVisibility(View.INVISIBLE);
                    break;
            }
        });
    }

    private void fetchRoutes() {
        if (TextUtils.isEmpty(getTempItem().origin_geo) || TextUtils.isEmpty(getTempItem().destination_geo))
            return;

        getTempItem().selectedRoute = null;
        updateTempItem();

        try{
            withServerKey(getResources().getString(R.string.google_places_key))
                    .from(StaticMethods.parseLatLng(getTempItem().origin_geo))
                    .to(StaticMethods.parseLatLng(getTempItem().destination_geo))
                    .alternativeRoute(false)
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);
        }
        catch (Exception e1) {
            getTempItem().origin_geo = null;
            getTempItem().destination_geo = null;
        }

    }

    private void quickOriginPicker(Marker marker) {

        checkboxShowhide.setChecked(true);

        if(tempMarker != null) {
            Location location = new Location(marker.getPosition().latitude + "," + marker.getPosition().longitude);
            location.setLatitude(marker.getPosition().latitude);
            location.setLongitude(marker.getPosition().longitude);
            String origin = StaticMethods.getLocationString(getActivity(), location);

            if(marker.getTitle()!=null)
                getTempItem().origin_text = marker.getTitle() + " - " + origin;
            else
                getTempItem().origin_text = origin;
            getTempItem().origin_geo = marker.getPosition().latitude + "," + marker.getPosition().longitude;
            updateTempItem();
            txtOrigin.setText(getTempItem().origin_text);
            txtOrigin.setTag(getTempItem().origin_geo);

            getEstimation();
            fetchRoutes();
        }

        // Hide Self
        showLocationPicker(false);
    }

    private void quickDestinationPicker(Marker marker) {

        checkboxShowhide.setChecked(true);

        if(tempMarker != null) {
            Location location = new Location(marker.getPosition().latitude + "," + marker.getPosition().longitude);
            location.setLatitude(marker.getPosition().latitude);
            location.setLongitude(marker.getPosition().longitude);
            String destination = StaticMethods.getLocationString(getActivity(), location);

            if(marker.getTitle()!=null)
                getTempItem().destination_text = marker.getTitle() + " - " + destination;
            else
                getTempItem().destination_text = destination;

            getTempItem().destination_geo = marker.getPosition().latitude + "," + marker.getPosition().longitude;
            updateTempItem();
            txtDestination.setText(getTempItem().destination_text);
            txtDestination.setTag(getTempItem().destination_geo);

            getEstimation();
            fetchRoutes();
        }

        // Hide Self
        showLocationPicker(false);
    }

    @Override
    protected void onLocationSelected(boolean isOrigin, String address, String geo) {
        if (isOrigin) {
            txtOrigin.setText(address);
            txtOrigin.setTag(geo);
        } else {
            txtDestination.setText(address);
            txtDestination.setTag(geo);
        }

        hideLayoutAfterDirection = true;
        fetchRoutes();
    }

    @Override
    protected void estimateFound(double lowerEstimate, double upperEstimate) {
        showEstimate(getTempItem().isRoundTrip == 1);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK() && direction.getRouteList().size() > 0) {

            if (!isAdded())
                return;
            if (checkboxShowhide.isChecked() && hideLayoutAfterDirection) {
                checkboxShowhide.setChecked(false);
                hideLayoutAfterDirection = false;
            }

            viewInfo.setAlpha(1.0f);

            Route route = direction.getRouteList().get(0);
            getTempItem().selectedRoute = route;
            updateTempItem();
            googleMap.clear();

            // Draw Route Markers and Lines
            routeLine = googleMap.addPolyline(createPolyline(getContext(), route.getAllCoordinates(), 5, ContextCompat.getColor(getContext(), R.color.colorAccent)));
            try {

                // Add Origin and Destination markers
                mOrigin = googleMap.addMarker(new MarkerOptions().position(StaticMethods.parseLatLng(getTempItem().origin_geo))
                        .title("Origin").snippet(getTempItem().origin_text).draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mDestination = googleMap.addMarker(new MarkerOptions().position(StaticMethods.parseLatLng(getTempItem().destination_geo))
                        .title("Destination").snippet(getTempItem().destination_text).draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                mDestination.showInfoWindow();

                // Zoom Camera to Origin Destination
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(mOrigin.getPosition());
                builder.include(mDestination.getPosition());
                // Zoom the map a little bit down by including a higher point
                builder.include(new LatLng(mOrigin.getPosition().latitude * 1.002, mOrigin.getPosition().longitude));
                builder.include(new LatLng(mDestination.getPosition().latitude * 1.002, mDestination.getPosition().longitude));
                LatLngBounds newbounds = builder.build();

                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(newbounds, 150));

                // Custom Markers
                loadMarkers();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(route.getLatLngBounds(), 300));
                } catch (Exception e1) {
                }
            }
        }
    }

    public void loadMarkers() {

        // Custom Markers / Recommended Locations
        Marker marker;
        // Lewis University
        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.607505,-88.123169))
                .title("Walmart").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_walmart)));

        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.6026022,-88.0826332))
                .title("Lewis University Bookstore").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_school)));

        // U of I
        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.112070,-88.159980))
                .title("Walmart").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_walmart)));

        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.103531,-88.238302 ))
                .title("Wassaja Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_school)));

        // U of W
        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(43.074992,-89.453643))
                .title("Target").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_target)));

        marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(43.077484,-89.415662))
                .title("Sullivan Residence Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_school)));

        quickSearchCheck();
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {

        tempMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(pointOfInterest.latLng.latitude,pointOfInterest.latLng.longitude))
                .title(pointOfInterest.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        tempMarker.showInfoWindow();

        // Show Origin | Destination chooser
        showLocationPicker(true);
    }

    private void showLocationPicker(boolean show){
        if(show)
        {
            btn_location_picker.setVisibility(View.VISIBLE);

            if(btnSearchLocal.getVisibility() == View.VISIBLE)
            {
                btnSearchLocal.setVisibility(View.INVISIBLE);
            }
        }
        else
            btn_location_picker.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Show info on Origin and Destination markers
        if(marker.isDraggable())
        {
            marker.showInfoWindow();
            return true;
        }

        marker.showInfoWindow();
        // Set the temporary marker to the current marker
        tempMarker = marker;

        // Show Origin | Destination chooser
        showLocationPicker(true);

        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        routeLine.setVisible(false);
        showLocationPicker(false);

        viewInfo.setAlpha(0.2f);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        if(!bounds.contains(mOrigin.getPosition()) || !bounds.contains(mDestination.getPosition())){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(mOrigin.getPosition());
            builder.include(mDestination.getPosition());

            LatLngBounds newbounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(newbounds, 300));
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    Location location = new Location(marker.getPosition().latitude + "," + marker.getPosition().longitude);
    location.setLatitude(marker.getPosition().latitude);
    location.setLongitude(marker.getPosition().longitude);
    String origin = StaticMethods.getLocationString(getActivity(), location);

        if (marker.getTitle().equals("Origin")) {
            getTempItem().origin_text = origin;
            getTempItem().origin_geo = marker.getPosition().latitude + "," + marker.getPosition().longitude;
            updateTempItem();
            txtOrigin.setText(getTempItem().origin_text);
            txtOrigin.setTag(getTempItem().origin_geo);
        } else if (marker.getTitle().equals("Destination"))
        {
            getTempItem().destination_text = origin;
            getTempItem().destination_geo = marker.getPosition().latitude + "," + marker.getPosition().longitude;
            updateTempItem();
            txtDestination.setText(getTempItem().destination_text);
            txtDestination.setTag(getTempItem().destination_geo);
        }

        getEstimation();
        fetchRoutes();

    }

    @Override
    public boolean onMyLocationButtonClick() {

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            String locationString = StaticMethods.getLocationString(getActivity(), location);
                            getTempItem().origin_text = locationString;
                            getTempItem().origin_geo = location.getLatitude() + "," + location.getLongitude();

                            tempMarker = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        }
                    });
        }

        // Show Origin | Destination chooser
        showLocationPicker(true);

        checkboxShowhide.setChecked(false);

        return false;
    }
}