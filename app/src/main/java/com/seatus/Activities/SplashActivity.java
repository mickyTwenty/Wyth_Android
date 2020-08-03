package com.seatus.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.instabug.library.Instabug;
import com.seatus.BaseClasses.BaseActivity;
import com.seatus.R;
import com.seatus.Utils.DeveloperHelper;
import com.seatus.Utils.EncryptionHelper;
import com.seatus.Utils.StaticAppMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.img_logo)
    ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);


        // Preloads
        getActivityViewModel().bootMeUp(context);
        StaticAppMethods.preloadStateCityData(context);
        StaticAppMethods.preloadSchoolsData(context);


        DeveloperHelper.logTokens(context);


        imgLogo.animate().alpha(1).setDuration(1000).start();

        handler.postDelayed(() -> {
            if (getUserItem() == null)
                startActivity(new Intent(context, AccountsActivity.class));
            else
                startActivity(new Intent(context, MainActivity.class));
            finish();
        }, 2000);
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

}
