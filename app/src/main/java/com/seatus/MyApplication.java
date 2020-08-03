package com.seatus;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import io.fabric.sdk.android.Fabric;

import static com.seatus.Utils.AppConstants.INSTABUG_KEY;
import static com.seatus.Utils.AppConstants.MIXPANEL_KEY;

public class MyApplication extends MultiDexApplication {


    @Override
    public void onCreate() {
        super.onCreate();


        Fabric.with(this, new Crashlytics());
//        TestFairy.begin(this, "0d214628fc17621672de9113b24e97cc48c454eb");
        FirebaseAnalytics.getInstance(this);
        MixpanelAPI.getInstance(this, MIXPANEL_KEY);
        new Instabug.Builder(this, INSTABUG_KEY)
                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT_GESTURE)
                .build();
    }
}
