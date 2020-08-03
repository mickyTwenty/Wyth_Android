package com.seatus.Models;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by saqib on 12/7/2017.
 */

public class OfferItem {

    public Integer bags;
    public Integer driver_id;
    public Integer passenger_id;
    public Integer sender_id;

    public String first_name;
    public String last_name;
    public String price;
    public String sender;
    public String trip_id;
    public Date timestamp;


    public String getPrice() {
        return String.format("$%s", price);
    }
}
