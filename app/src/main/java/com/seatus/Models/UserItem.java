package com.seatus.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rohail on 26-Sep-17.
 */

@Entity
public class UserItem {

    public boolean is_third_party = false;

    public String gender = "";
    public String city;
    public String email = "";
    public String city_text = "";
    public String state;
    public String state_text = "";
    public String profile_picture = "";
    public int share_count = 0;

    @NonNull
    @PrimaryKey
    public String user_id;

    public String first_name = "";
    public String last_name = "";

    public String school_name = "";
    public String phone = "";
    @SerializedName("postal_code")
    public String zip = "";
    public String graduation_year = "";
    public String student_organization = "";
    public String birth_date = "";
    public String pass = "";
    public String user_type = "normal";  // "normal" & "driver"
    public String role_id = "normal";  // "normal" & "driver"
    public boolean has_sync_friends = false;
    public boolean has_facebook_integrated = false;
    public Integer follower_count;
    public Double rating;
    public String feedback;
    public String trip_id;
    public boolean has_pending_ratings = false;

    @SerializedName("_token")
    public String token;

    public String driving_license_no;

    public String vehicle_type;
    public String vehicle_make;
    public String vehicle_id_number;
    public String vehicle_model;
    public String vehicle_year;


    @Ignore
    public boolean is_confirmed;
    @Ignore
    public boolean has_payment_made;
    @Ignore
    public boolean checked;


    @Nullable
    @Ignore
    public SearchFilterDataItem trip_search_data;

    //Temp Fields
    @Ignore
    public StateItem selected_state;
    @Ignore
    public CityItem selected_city;
    @Ignore
    public String facebookToken;

    @Nullable
    @Ignore
    public Integer status = 0;
    @Nullable
    @Ignore
    public Integer seats = 0;
    @Nullable
    @Ignore
    public Integer seats_returning = 0;

    @Nullable
    @Ignore
    public String bags_quantity = "0";
    @Nullable
    @Ignore
    public String fare = "-";

    public String group_id;


    //RideFlowDetails
    @Ignore
    public String trips_canceled;
    @Ignore
    public String trips_canceled_driver;
    @Ignore
    public String ssn;
    @Ignore
    public boolean is_picked;
    @Ignore
    public boolean is_dropped;
    @Ignore
    public String pickup_latitude;
    @Ignore
    public String pickup_longitude;
    @Ignore
    public String pickup_title;
    @Ignore
    public String dropoff_latitude;
    @Ignore
    public String dropoff_longitude;
    @Ignore
    public String dropoff_title;
    @Ignore
    public boolean is_rated = false;

    @Ignore
    public String mobile = "";

    @Ignore
    public UserItem(@NonNull String user_id) {
        this.user_id = user_id;
    }

    public static final String ROLE_PASSENGER = "passenger";
    public static final String ROLE_DRIVER = "driver";

    public UserItem() {
    }

    public static UserItem newThirdPartyItem(String firstName, String lastName, String email, String phone) {
        UserItem item = new UserItem();
        item.first_name = firstName;
        item.last_name = lastName;
        item.email = email;
        item.phone = phone;
        item.is_third_party = true;
        item.user_id = "dummy_" + System.currentTimeMillis();

        return item;
    }

    public boolean isProfileInComplete() {
        if (!getCurrentInterface()) {
            if (TextUtils.isEmpty(vehicle_type) || TextUtils.isEmpty(vehicle_id_number) || TextUtils.isEmpty(vehicle_make) || TextUtils.isEmpty(vehicle_model))
                return true;
        }

        return false;
    }


    public enum UserType {
        normal,
        driver
    }

    public UserType getUser_type() {
        switch (user_type) {
            case "normal":
                return UserType.normal;
            case "driver":
                return UserType.driver;
            default:
                return UserType.normal;
        }
    }

    @Nullable
    public Integer getStatus() {
        return status;
    }


    public String logString() {
        return "UserItem{" +
                "user_id='" + user_id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", user_type='" + user_type + '\'' +
                ", has_sync_friends=" + has_sync_friends +
                ", has_facebook_integrated=" + has_facebook_integrated +
                ", follower_count=" + follower_count +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return getFull_name();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserItem) {
            UserItem userObj = (UserItem) obj;
            if (!TextUtils.isEmpty(user_id))
                return user_id.equals(userObj.user_id);
            else
                return email.equals(userObj.email);
        } else return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("first_name", first_name);
        map.put("last_name", last_name);
        map.put("profile_picture", profile_picture);
//        map.put("status", 0);
        map.put("ref", FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(user_id));
        return map;
    }

//    public Map<String, Object> toMap(String inviter_name) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("user_id", user_id);
//        map.put("full_name", full_name);
//        map.put("profile_picture", profile_picture);
//        map.put("status", 0);
//        map.put("trip_search_data", trip_search_data.toMap(inviter_name));
//        return map;
//    }


    public String getFull_name() {
        try {
            return first_name + " " + last_name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean getCurrentInterface() {
        boolean isLastNormalInterface = PreferencesManager.getBoolean(AppConstants.KEY_INTERFACE);

        if (isLastNormalInterface)
            return true;

        if (getUser_type() == UserItem.UserType.normal)
            return true;
        else
            return false;
    }

    public String getRoleString() {
        boolean isLastNormalInterface = PreferencesManager.getBoolean(AppConstants.KEY_INTERFACE);

        if (isLastNormalInterface)
            return ROLE_PASSENGER;

        if (getUser_type() == UserItem.UserType.normal)
            return ROLE_PASSENGER;
        else
            return ROLE_DRIVER;
    }

}
