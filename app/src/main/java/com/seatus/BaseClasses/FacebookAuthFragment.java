package com.seatus.BaseClasses;

import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.seatus.R;
import com.seatus.Views.TitleBar;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by rohail on 5/31/2016.
 */
public abstract class FacebookAuthFragment extends BaseFragment {


    protected List<String> mPermissions = Arrays.asList("email", "user_friends");
    protected LoginManager mLoginManager;
    protected FacebookCallback mFacebookCallback;
    protected CallbackManager mCallbackManager;
    private FBLoginInterface LoginInterface;

    public void loginWithFaceBook(FBLoginInterface iFace) {
        mLoginManager.logInWithReadPermissions(this, mPermissions);
        this.LoginInterface = iFace;
    }

    protected void initFaceBook() {
        FacebookSdk.sdkInitialize(getContext());
        AppEventsLogger.activateApp(getContext());

        mLoginManager = LoginManager.getInstance();
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        if (mFacebookCallback == null)
            mFacebookCallback = new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log("LoginActivity login", loginResult.toString());
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> LoginInterface.FBLoginSuccesfull(object, response));
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Log("LoginActivity", "facebook login canceled");
                }

                @Override
                public void onError(FacebookException e) {
                    Log("LoginActivity", "facebook login failed error");
                }
            };

        if (mCallbackManager == null)
            mCallbackManager = CallbackManager.Factory.create();
        if (mLoginManager == null)
            mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(mCallbackManager, mFacebookCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {

    }

    public interface FBLoginInterface {
        public void FBLoginSuccesfull(JSONObject object, GraphResponse response);
    }

    ;
}
