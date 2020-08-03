package com.seatus.Fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;

import java.util.ArrayList;

/**
 * Created by rah on 11-Dec-17.
 */

public class RideMapDetailsFragment extends BaseFragment implements OnMapReadyCallback {

    GoogleMap googleMap;
    public TripItem tripItem;

    public static RideMapDetailsFragment newInstance(TripItem tripItem) {
        RideMapDetailsFragment fragment = new RideMapDetailsFragment();
        fragment.tripItem = tripItem;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_map_detail;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.enableBack().setTitle("map");
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


        ArrayList<LatLng> coordinates = tripItem.getDecodedPolyLine();
        LatLng origin = tripItem.getOriginLatLng();
        LatLng destination = tripItem.getDestinationLatLng();

        if (coordinates != null)
            googleMap.addPolyline(DirectionConverter.createPolyline(getContext(), coordinates, 5, ContextCompat.getColor(getContext(), R.color.colorAccent)));
        if (origin != null && destination != null) {
            googleMap.addMarker(new MarkerOptions().position(origin).title("Origin").snippet(tripItem.origin_title));
            googleMap.addMarker(new MarkerOptions().position(destination).title("Destination").snippet(tripItem.destination_title));
            LatLngBounds bounds = StaticMethods.getLatLngBounds(origin, destination);
            try {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
            } catch (Exception e) {
                e.printStackTrace();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
            }
        }

    }
}
