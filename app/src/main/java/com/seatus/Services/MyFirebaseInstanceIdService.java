package com.seatus.Services;

import android.app.Notification;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;

/**
 * Created by rohail on 21-Mar-17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (refreshedToken != null) {
            StaticMethods.initPreferences(this);
            PreferencesManager.putString(AppConstants.KEY_TOKEN, refreshedToken);
            Log.e("FCM", "Refreshed token: " + refreshedToken);
        }
    }

    public static String getAppToken() {
        String token = PreferencesManager.getString(AppConstants.KEY_TOKEN);
        if (TextUtils.isEmpty(token)) {
            token = FirebaseInstanceId.getInstance().getToken();
            PreferencesManager.putString(AppConstants.KEY_TOKEN, token);
        }
        return token;
    }
}
