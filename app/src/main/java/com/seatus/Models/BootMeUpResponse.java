package com.seatus.Models;

import java.util.ArrayList;

/**
 * Created by rah on 17-Nov-17.
 */

public class BootMeUpResponse {

    public ArrayList<SearchPreferenceItem> preferences;
    public ArrayList<String> vehicle_type;
    public ArrayList<String> reference_source;
    public ArrayList<VehicleMake> make;

    public float min_estimate = 0.125f;
    public float max_estimate = 0.25f;
    public float transaction_fee = 0.0f;
    public float transaction_fee_local = 0.0f;
    public int local_max_distance = 16093;

}
