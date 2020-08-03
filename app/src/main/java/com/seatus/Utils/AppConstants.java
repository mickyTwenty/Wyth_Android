package com.seatus.Utils;

import com.seatus.BuildConfig;

/**
 * Created by rohail on 20-Jan-17.
 */

public class AppConstants {

    public static String url_local = "http://192.168.168.114/seatus/public/";
    public static String url_live = "https://portal.gowyth.com/";
    public static String url_staging = "http://34.213.248.253/";

    //public static String ServerUrl = url_staging;
    public static String ServerUrl = url_live;

    public static String ApiUrl = ServerUrl + "api/v1/";

    public static String MIXPANEL_KEY = "d0f09a7f5c1ba1e219ed3c30d2a5594a";
    public static String INSTABUG_KEY = "53fa90ceed1b6da384c5ba2e080923a0";

    public static boolean onTest = true;
    public static boolean sDisableFragmentAnimations = false;
    public static String PreferencesName = "Wyth_v" + BuildConfig.VERSION_NAME;

    public static String SERVICE_START;
    public static final String EVENT_PUSH_REVEIVED = "new_push_received";

    public static int FIELD_MINIMUM_LENGTH = 6;

    public static float RATE_PER_MILE = 0.25f;

    public static final String KEY_USER = "user_pref_obj";
    public static final String KEY_INTERFACE = "curr_interface";
    public static final String KEY_SERVICES = "services_list";
    public static final String KEY_TRIP = "active_trip";

    public static String KEY_TOKEN = "device_token";

    public static String KEY_LATITUDE = "key_location_latitude";
    public static String KEY_LONGITUDE = "key_location_longitude";

    // public static final String STRIPE_KEY = "pk_test_3mNpJpdwD47TQyZcOOzLZfQr";
    public static final String STRIPE_KEY = "pk_live_JsROXvemcrUVJN91G7Zmm7X8";

    public static final String FROM_PUSH = "arg_from_push";
    public static final String PUSH_ID = "arg_push_id";

    public static String STORE_COLLECTION_USERS = "users";
    public static String STORE_COLLECTION_LOCATION = "locations";
    public static String STORE_COLLECTION_GROUPS = "groups";
    public static String STORE_COLLECTION_CHAT = "chat";
    public static String STORE_COLLECTION_INVITED_MEMBERS = "invited_members";
    public static String STORE_NODE_REQUESTS = "request_data";

    public static String KEY_TEMP_FILTERS = "temp_filter_item";
    public static String BOOT_ME_UP = "boot_me_up_data";

    public static int ExpiryTime = 15;
    public static int MAX_SEAT_LIMIT = 8;
    public static int MAX_TRIP_LIMIT = 8;

    // 1609.3 Meters = 1 Mile
    public static int LOCAL_TRIP_RANGE = 16093;

}
