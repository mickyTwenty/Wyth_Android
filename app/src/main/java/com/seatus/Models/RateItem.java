package com.seatus.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by saqib on 2/9/2018.
 */

public class RateItem {

    public String user_id;
    public String trip_id;
    public Double rating;
    public String feedback;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "{" +
                "user_id='" + user_id + '\'' +
                ", trip_id='" + trip_id + '\'' +
                ", rating=" + rating +
                ", feedback='" + feedback + '\'' +
                '}';
    }
    public JSONObject toJsonObject(){
        try {
            return new JSONObject(toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
