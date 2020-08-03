package com.seatus.Models;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.PolyUtil;
import com.seatus.Views.PreferencesView;

import java.util.ArrayList;

/**
 * Created by rah on 28-Nov-17.
 */

public class TripItem{

    public String origin_title;
    public String destination_title;
    public String trip_id;

    public String trip_name;
    public String gender;

    @SerializedName("id")
    public String single_id;

    public String expected_distance;
    public String expected_distance_format;

    public String estimates;
    public String estimates_format;

    public String min_estimates;
    public String max_estimates;

    public String destination_longitude;
    public String total_distance;
    public String ride_status;
    public String destination_latitude;
    public String payout_type;
    public String date;
    public String expected_start_time;
    public String start_time;
    public int time_range;
    public String individual_cost;
    public Integer seats_available;
    public String origin_latitude;
    public String origin_longitude;
    public String ended_at;
    public String started_at;
    public int seats_total;
    public String route_polyline;
    public float rating;
    public boolean has_initiated_offer;
    public boolean is_member;
    public boolean is_driver;
    public String offer_id;
    public String group_id;
    public boolean is_enabled_booknow;
    public boolean is_request;

    public float sortWeight;

    public UserItem driver;
    public ArrayList<UserItem> passengers;

    public boolean is_roundtrip;
    public boolean booked_as_roundtrip;
    public boolean needs_payment;

    public UserItem passenger;

    public ArrayList<TripItem> rides;
    public TripItem return_trip;
    public ArrayList<SearchPreferenceItem> preferences;

    public ArrayList<UserItem> itinerary_shared;

    public Integer getPairedItemPosition(ArrayList<TripItem> list) {
        for (int pos = 0; pos < list.size(); pos++) {
            if (!TextUtils.isEmpty(list.get(pos).group_id))
                if (list.get(pos).group_id.equals(group_id) && !list.get(pos).trip_id.equals(trip_id))
                    return pos;
        }
        return null;
    }

    public LatLng getOriginLatLng() {
        try {
            double lat = Double.parseDouble(origin_latitude);
            double longi = Double.parseDouble(origin_longitude);
            return new LatLng(lat, longi);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LatLng getDestinationLatLng() {
        try {
            double lat = Double.parseDouble(destination_latitude);
            double longi = Double.parseDouble(destination_longitude);
            return new LatLng(lat, longi);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<LatLng> getDecodedPolyLine() {
        ArrayList<LatLng> coordinates = new ArrayList<LatLng>(PolyUtil.decode(route_polyline));
        return coordinates;
    }

    public String getTripName() {
       // return trip_name;
        return trip_id;
    }

    public boolean isMember() {
        return is_member || is_driver;
    }

    public String getOtherUserId() {
        try {
            if (driver == null)
                return passenger.user_id;
            else if (passenger == null)
                return driver.user_id;
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserItem getOtherUser() {
        try {
            if (driver == null)
                return passenger;
            else if (passenger == null)
                return driver;
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TripStatus getTripStatus() {
        return TripStatus.valueOf(ride_status);
    }

    public enum TripStatus {
        pending,
        active,
        filled,
        confirmed,
        started,
        goingCompleted,
        returnConfirmed,
        ended,
        canceled,
        failed,
        expired
    }

}
