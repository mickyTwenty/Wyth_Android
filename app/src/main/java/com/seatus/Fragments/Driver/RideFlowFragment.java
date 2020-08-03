package com.seatus.Fragments.Driver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.seatus.Activities.MainActivity;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Models.MatrixApiResponse;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rah on 11-Dec-17.
 */

public class RideFlowFragment extends BaseFragment implements OnMapReadyCallback {

    public static final String ACTION_PICKUP = "pickup";
    public static final String ACTION_DROPOFF = "dropoff";


    @BindView(R.id.btn_endride)
    TextView btnEndride;
    @BindView(R.id.btn_pick)
    TextView btnPickUp;
    @BindView(R.id.btn_drop)
    TextView btnDrop;
    @BindView(R.id.layout_pick_drop)
    LinearLayout layoutPickDrop;

    @BindView(R.id.layout_info)
    LinearLayout layoutInfo;
    @BindView(R.id.txt_distance)
    TextView txtDistance;
    @BindView(R.id.txt_time)
    TextView txtTimeEstimate;


    ArrayList<UserItem> listNeedPickup = new ArrayList<>();
    ArrayList<UserItem> listNeedDrop = new ArrayList<>();
    ArrayList<Marker> listMemmberMarkers = new ArrayList<>();

    private boolean routeDrawn;
    GoogleMap googleMap;
    public TripItem tripItem;

    Marker myMarker;


    public long lastEstimateTime = 0l;

    public static RideFlowFragment newInstance(TripItem tripItem) {
        RideFlowFragment fragment = new RideFlowFragment();
        fragment.tripItem = tripItem;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_ride_flow;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.enableBack().setTitle("Ride Started");
    }

    @Override
    public void inits() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void setEvents() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            googleMap.setMyLocationEnabled(true);
        }
        setData();

        AppStore.getInstance().getLocationLiveData().observe(this, location -> {
            if (location != null) {
                getEstimate(location);
                if (myMarker == null) {
                    myMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_car)).rotation(location.getBearing()));
                } else {
                    animateMarker(myMarker, new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        });
    }

    public String getCurrLocation() {
        try {
            if (myMarker != null) {
                return myMarker.getPosition().latitude + "," + myMarker.getPosition().longitude;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getEstimate(Location location) {

        long currTime = System.currentTimeMillis();
        if (currTime - lastEstimateTime < 10000)
            return;

        lastEstimateTime = currTime;

        try {
            WebServiceFactory.getInstance().distanceMatrixApi(getString(R.string.google_places_key), location.getLatitude() + "," + location.getLongitude(), tripItem.destination_latitude + "," + tripItem.destination_longitude).enqueue(new Callback<MatrixApiResponse>() {
                @Override
                public void onResponse(Call<MatrixApiResponse> call, Response<MatrixApiResponse> response) {
                    if (response.isSuccessful() && response.body().Status.equals("OK")) {
                        try {
                            layoutInfo.setVisibility(View.VISIBLE);
                            txtTimeEstimate.setText(response.body().Rows.get(0).Elements.get(0).Duration.Text);
                            txtDistance.setText(response.body().Rows.get(0).Elements.get(0).Distance.Text);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MatrixApiResponse> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        sortMembers();
        plotTrip();
        plotMembers();

        if (listNeedPickup.isEmpty()) {
            if (listNeedDrop.isEmpty()) {
                layoutPickDrop.setVisibility(View.GONE);
                btnEndride.setVisibility(View.VISIBLE);
            } else
                btnPickUp.setVisibility(View.GONE);
        }

        if (listNeedDrop.isEmpty())
            btnDrop.setVisibility(View.GONE);
        else
            btnDrop.setVisibility(View.VISIBLE);
    }

    private void sortMembers() {
        listNeedPickup.clear();
        listNeedDrop.clear();
        for (UserItem passenger : tripItem.passengers) {
            if (!passenger.is_picked)
                listNeedPickup.add(passenger);
            else if (!passenger.is_dropped)
                listNeedDrop.add(passenger);
        }
    }


    private void plotMembers() {

        for (Marker marker : listMemmberMarkers)
            marker.remove();

        listMemmberMarkers.clear();

        for (UserItem passenger : listNeedPickup) {
            if (!passenger.pickup_latitude.equals("") && !passenger.pickup_longitude.equals("")) {
                LatLng origin = new LatLng(Double.parseDouble(passenger.pickup_latitude), Double.parseDouble(passenger.pickup_longitude));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(origin).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pick)).title(passenger.getFull_name()).snippet(passenger.pickup_title));
                marker.showInfoWindow();
                listMemmberMarkers.add(marker);
            }
        }

        for (UserItem passenger : listNeedDrop) {
            if (!passenger.dropoff_latitude.equals("") && !passenger.dropoff_longitude.equals("")) {
                LatLng destination = new LatLng(Double.parseDouble(passenger.dropoff_latitude), Double.parseDouble(passenger.dropoff_longitude));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(destination).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_drop)).title(passenger.getFull_name()).snippet(passenger.dropoff_title));
                marker.showInfoWindow();
                listMemmberMarkers.add(marker);
            }
        }

    }

    private void plotTrip() {
        if (routeDrawn)
            return;
        ArrayList<LatLng> coordinates = tripItem.getDecodedPolyLine();
        LatLng origin = tripItem.getOriginLatLng();
        LatLng destination = tripItem.getDestinationLatLng();

        if (coordinates != null)
            googleMap.addPolyline(DirectionConverter.createPolyline(getContext(), coordinates, 5, ContextCompat.getColor(getContext(), R.color.colorAccent)));
        if (origin != null && destination != null) {
//            googleMap.addMarker(new MarkerOptions().position(origin).title("Origin").snippet(tripItem.origin_title));
            googleMap.addMarker(new MarkerOptions().position(destination).title("Destination").snippet(tripItem.destination_title));
            LatLngBounds bounds = StaticMethods.getLatLngBounds(origin, destination);
            try {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
            } catch (Exception e) {
                e.printStackTrace();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
            }
        }
        routeDrawn = true;
    }

    @OnClick({R.id.btn_endride, R.id.btn_pick, R.id.btn_drop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_endride:
                getActivityViewModel().endRide(tripItem.trip_id).observe(this, webResponseResource -> {
                    switch (webResponseResource.status) {
                        case loading:
                            showLoader();
                            break;
                        case success:
                            hideLoader();
                            makeSnackbar(webResponseResource.data);
                            getUserItem().has_pending_ratings = true;
                            PreferencesManager.putObject(AppConstants.KEY_USER, getUserItem());
                            ((MainActivity) getFragmentActivity()).stopForegroundService();
                            getFragmentActivity().actionBack();
                            getFragmentActivity().resetDelay();
                            getFragmentActivity().replaceFragmentWithBackstack(PassengerRatingFragment.newInstance(tripItem, tripItem.passengers));
                            break;
                        default:
                            hideLoader();
                            makeSnackbar(webResponseResource.data);
                            break;
                    }
                });
                break;
            case R.id.btn_pick:
                DialogHelper.showPickUpDropOffDialog(getContext(), listNeedPickup, "Mark PickUp", passengersCsv -> {
                    getActivityViewModel().markPickDrop(ACTION_PICKUP, tripItem.trip_id, passengersCsv, getCurrLocation()).observe(this, webResponseResource -> {
                        switch (webResponseResource.status) {
                            case loading:
                                showLoader();
                                break;
                            case success:
                                hideLoader();
                                tripItem = webResponseResource.data.body;
                                setData();
                                break;
                            default:
                                hideLoader();
                                makeSnackbar(webResponseResource.data);
                                break;
                        }
                    });
                });
                break;
            case R.id.btn_drop:
                DialogHelper.showPickUpDropOffDialog(getContext(), listNeedDrop, "Mark DropOff", passengersCsv -> {
                    getActivityViewModel().markPickDrop(ACTION_DROPOFF, tripItem.trip_id, passengersCsv, getCurrLocation()).observe(this, webResponseResource -> {
                        switch (webResponseResource.status) {
                            case loading:
                                showLoader();
                                break;
                            case success:
                                hideLoader();
                                tripItem = webResponseResource.data.body;
                                setData();
                                break;
                            default:
                                hideLoader();
                                makeSnackbar(webResponseResource.data);
                                break;
                        }
                    });
                });
                break;
        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition) {
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
//                marker.setRotation(bearing);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    marker.setVisible(true);
                }
            }
        });
    }
}
