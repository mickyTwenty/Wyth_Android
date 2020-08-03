package com.seatus.Models;

import com.akexorcist.googledirection.model.Route;
import com.seatus.enums.InviteCategory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rah on 10-Nov-17.
 */

public class SearchFilterDataItem {

    public String trip_name;
    public String origin_text = "Select From Map";
    public String origin_geo;
    public String destination_text = "Select From Map";
    public String destination_geo;
    public Long date;
    public Long returnDate;
    public int seats = 1;
    public int return_seats = 1;
    public int book_now = 0;

    public int trip_count = 1;

    public boolean morning;
    public boolean afternoon;
    public boolean night;

    public int isRoundTrip;

    public String is_round_trip;

    public int time_range = 0;
    public int return_time_range = 0;

    public String estimate_format;
    public String estimate;

    public double estimate_low;
    public double estimate_high;

    public String preferences;

    public InviteCategory inviteType;

    public String minimum_rating = "0";
    public String gender = "Both";
    public Boolean isSingleRoundTrip = false;
    public Route selectedRoute;

//    public List<UserItem> invited_members;

    public SearchFilterDataItem(String origin_text, String origin_geo, String destination_text, String destination_geo) {
        this.origin_text = origin_text;
        this.origin_geo = origin_geo;
        this.destination_text = destination_text;
        this.destination_geo = destination_geo;
    }

    public SearchFilterDataItem() {
    }

    public Date getDate() {
        if (date != null)
            return new Date(date);
        else return null;
    }

    public void setDate(Date date) {
        this.date = date.getTime();
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate.getTime();
    }

    public Date getReturnDate() {
        if (returnDate != null)
            return new Date(returnDate);
        else return null;
    }


    public Map<String, Object> toMap(String inviter_name, String inviter_id) {
        Map<String, Object> map = new HashMap<>();
        map.put("origin_geo", origin_geo);
        map.put("origin_text", origin_text);
        map.put("destination_geo", destination_geo);
        map.put("destination_text", destination_text);
        map.put("inviter_name", inviter_name);
        map.put("inviter_id", inviter_id);
        map.put("date", getDate());
        map.put("is_round_trip", isRoundTrip + "");
        map.put("time_range", time_range + "");
        return map;

    }

}
