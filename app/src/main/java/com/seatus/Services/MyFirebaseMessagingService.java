package com.seatus.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seatus.Activities.MainActivity;
import com.seatus.Activities.SplashActivity;
import com.seatus.Models.NotifObject;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.BusProvider;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;

import java.util.Random;

/**
 * Created by rohail on 21-Mar-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private Context context = this;
    private UserItem userItem;

    public String CHANNEL_ID = "wyth_updates";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        try {
            if (remoteMessage.getData() != null) {
                Gson gson = new Gson();
                String notifJson = gson.toJson(remoteMessage.getData());
                NotifObject notif = gson.fromJson(notifJson, NotifObject.class);
                if (AppStore.getInstance().getNotificationItem().hasActiveObservers())
                    AppStore.getInstance().getNotificationItem().postValue(notif);
                sendNotif(notif.data_title, notif.data_message, notifJson);
            }
            Log.e("FCM", "Notification Received : " + remoteMessage.getData().get("data_click_action"));
        } catch (Exception e) {
            e.printStackTrace();
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

    public void sendNotif(String title, String msg, String notifObject) {

        StaticMethods.initPreferences(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logo_whereto);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        Intent intent;
        if (getUserItem() != null) {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.ARG_PUSHDATA, notifObject);
        } else {
            intent = new Intent(context, SplashActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(title);
        builder.setContentText(msg);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_ID, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;

        notificationManager.notify(new Random().nextInt(1000), builder.build());

    }


    public UserItem getUserItem() {
        userItem = PreferencesManager.getObject(AppConstants.KEY_USER, UserItem.class);
        return userItem;
    }
}
