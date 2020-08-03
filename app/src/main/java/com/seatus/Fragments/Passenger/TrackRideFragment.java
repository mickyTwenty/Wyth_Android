package com.seatus.Fragments.Passenger;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Models.MatrixApiResponse;
import com.seatus.Models.NotifObject;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.akexorcist.googledirection.util.DirectionConverter.createPolyline;

/**
 * Created by rah on 11-Dec-17.
 */

public class TrackRideFragment extends BaseFragment implements OnMapReadyCallback {

    public static final String ACTION_PICKUP = "pickup";
    public static final String ACTION_DROPOFF = "dropoff";


    @BindView(R.id.layout_info)
    LinearLayout layoutInfo;
    @BindView(R.id.txt_distance)
    TextView txtDistance;
    @BindView(R.id.txt_time)
    TextView txtTimeEstimate;


    private boolean routeDrawn;
    GoogleMap googleMap;
    public TripItem tripItem;

    Marker myMarker;


    public long lastEstimateTime = 0l;
    private ListenerRegistration listener;

    public static TrackRideFragment newInstance(TripItem tripItem) {
        TrackRideFragment fragment = new TrackRideFragment();
        fragment.tripItem = tripItem;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_ride_track;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.enableBack().setTitle("Tracking");
    }

    @Override
    public void inits() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void setEvents() {
        AppStore.getInstance().getNotificationItem().observe(this, notifObject -> {
            if (notifObject != null && notifObject.getAction().equals(NotifObject.ACTION_PASSENGER_DROPED))
                getFragmentActivity().actionBack();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            googleMap.setMyLocationEnabled(true);
        }
        plotTrip();

        listener = FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_LOCATION).document(tripItem.trip_id).addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("coordinates")) {
                LatLng location = StaticMethods.parseLatLng(documentSnapshot.getString("coordinates"));
                if (location == null)
                    return;

                getEstimate(location);
                if (myMarker == null) {
                    myMarker = googleMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_car)));
                } else {
                    animateMarker(myMarker, location);
                }
            }
        });


    }

    private void plotTrip() {
        if (routeDrawn)
            return;
        ArrayList<LatLng> coordinates = tripItem.getDecodedPolyLine();
        LatLng origin = tripItem.getOriginLatLng();
        LatLng destination = tripItem.getDestinationLatLng();

        if (coordinates != null)
            googleMap.addPolyline(createPolyline(getContext(), coordinates, 5, ContextCompat.getColor(getContext(), R.color.colorAccent)));
        if (origin != null && destination != null) {
//            googleMap.addMarker(new MarkerOptions().position(origin).title("Origin").snippet(tripItem.origin_title));
//            googleMap.addMarker(new MarkerOptions().position(destination).title("Destination").snippet(tripItem.destination_title));
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

    private void getEstimate(LatLng location) {

        long currTime = System.currentTimeMillis();
        if (currTime - lastEstimateTime < 10000)
            return;

        lastEstimateTime = currTime;

        try {
            WebServiceFactory.getInstance().distanceMatrixApi(getString(R.string.google_places_key), location.latitude + "," + location.longitude, tripItem.destination_latitude + "," + tripItem.destination_longitude).enqueue(new Callback<MatrixApiResponse>() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            listener.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
