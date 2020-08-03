package com.seatus.Models;

import java.util.ArrayList;

/**
 * Created by saqib on 2/9/2018.
 */

public class PendingRatingItem {

    public ArrayList<Data> driver;
    public ArrayList<Data> passenger;

    public class Data {
        public TripItem trip;
        public UserItem user;
    }
}
