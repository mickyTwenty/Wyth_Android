package com.seatus.Services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seatus.Activities.SplashActivity;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;

import java.util.HashMap;
import java.util.Map;

import static com.seatus.Utils.StaticAppMethods.getUserItem;

/**
 * Created by rah on 25-Jan-18.
 */

public class ForegroundLocationService extends Service {

    public static final String START = "stop_foreground";
    public static final String STOP = "start_foreground";
    public static boolean IS_SERVICE_RUNNING = false;

    public static final int FOREGROUND_NOTIF_ID = 181;

    Context context = this;
    private UserItem userItem;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    public String CHANNEL_ID = "track_location";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StaticMethods.initPreferences(context);
        userItem = getUserItem();
        try {
            if (intent.getAction().equals(START)) {
                showNotification();
                startLocationWork();
                IS_SERVICE_RUNNING = true;
                Log.e("Foreground Service", "Started");
            } else {
                stopForeground(true);
                stopSelf();
                IS_SERVICE_RUNNING = false;
                Log.e("Foreground Service", "Stoped");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getActiveTrip() != null)
            return START_STICKY;
        else
            return START_NOT_STICKY;
    }

    private void startLocationWork() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.e("Foreground Service", location.toString());
                    AppStore.getInstance().getLocationLiveData().postValue(location);

                    TripItem trip = getActiveTrip();
                    if (trip != null) {
                        Map<String, String> map = new HashMap<>();
                        map.put("coordinates", location.getLatitude() + "," + location.getLongitude());
                        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_LOCATION).document(trip.trip_id).set(map, SetOptions.merge());
                    }
                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }


    public void showNotification() {
        if (userItem == null) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logo_whereto);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Wherr2");
        builder.setContentText("Your Location is being tracked..");
        startForeground(FOREGROUND_NOTIF_ID, builder.build());
    }

    public TripItem getActiveTrip() {
        try {
            String data = PreferencesManager.getString(AppConstants.KEY_TRIP);
            return new Gson().fromJson(data, TripItem.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

}
