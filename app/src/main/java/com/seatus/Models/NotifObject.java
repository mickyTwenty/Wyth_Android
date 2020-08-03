package com.seatus.Models;

import android.text.TextUtils;

import java.util.Date;

/**
 */

public class NotifObject {


    // No Action
    public static final String ACTION_ACCEPTED_PASSENGER = "offer_accepted_passenger";
    public static final String ACTION_REJECTED_PASSENGER = "offer_rejected_passenger";
    public static final String ACTION_OFFERS_CONTRADICTED = "offer_contradicted";


    // RideDetails
    public static final String ACTION_PASSENGER_REMOVED = "passenger_removed";
    public static final String ACTION_PASSENGERS_CONFIRMED = "passengers_confirmed";
    public static final String ACTION_OFFERS_EXPIRED = "offer_expired";
    public static final String ACTION_UPDATE_TIME = "passengers_confirmed";
    public static final String ACTION_NEW_TRIP_SUGGESTION = "new_trip_suggestion";
    public static final String ACTION_PASSENGER_TRIP_STARTED = "trip_started_passenger";
    public static final String ACTION_PASSENGER_DROPED = "marked_dropoff";
    public static final String ACTION_PASSENGER_PICKED = "marked_pickup";
    public static final String ACTION_PASSENGER_LEFT = "passenger_left";
    public static final String ACTION_TRIP_CANCELED = "trip_canceled";
    public static final String ACTION_UPDATE_RETURN_TIME = "update_returning_ride_time";

    // Special Screens
    public static final String ACTION_NEW_MESSAGE = "chat_message";
    public static final String ACTION_NEW_OFFER = "trip_offer";


    // Special Actions Dialogs
    public static final String ACTION_ACCEPTED_DRIVER = "offer_accepted_driver";
    public static final String ACTION_NEW_INVITE = "trip_invitation";
    public static final String ACTION_NEW_DRIVER_INVITE = "driver_invitation";


    public String data_click_action;
    public String data_action;
    public String data_message;
    public String data_title;

    // Invite Request Data
    public String origin_geo;
    public String card_number;
    public String origin_text;
    public String driver_name;
    public String destination_geo;
    public String destination_text;
    public String date;
    public String return_date;
    public String time_range;
    public String inviter_name;
    public String inviter_id;

    // New Offer Data
    public String passenger_id;
    public String driver_id;
    public String trip_id;
    public String sender;

    // Driver Accepted Offer
    public String trip_name;
    public String bags_quantity;
    public String proposed_amount;
    public String estimated_distance;


    public String is_round_trip;

    public String getAction() {
        if (!TextUtils.isEmpty(data_click_action))
            return data_click_action;
        else return data_action;
    }

    public Date getDate() {
        try {
            return new Date(Long.parseLong(date));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getOtherUserId() {
        try {
            if (sender.equals("driver"))
                return driver_id;
            else
                return passenger_id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isForPassenger() {
        if (sender.equals("driver"))
            return true;
        else
            return false;
    }

    public boolean isTripDetailNotif() {
        if (getAction().equals(ACTION_PASSENGER_REMOVED) ||
                getAction().equals(ACTION_PASSENGERS_CONFIRMED) ||
                getAction().equals(ACTION_OFFERS_EXPIRED) ||
                getAction().equals(ACTION_UPDATE_TIME) ||
                getAction().equals(ACTION_PASSENGER_TRIP_STARTED) ||
                getAction().equals(ACTION_PASSENGER_DROPED) ||
                getAction().equals(ACTION_PASSENGER_PICKED) ||
                getAction().equals(ACTION_PASSENGER_LEFT) ||
                getAction().equals(ACTION_ACCEPTED_PASSENGER) ||
                getAction().equals(ACTION_UPDATE_RETURN_TIME)) {
            if (!TextUtils.isEmpty(trip_id))
                return true;
        }
        return false;
    }

}
