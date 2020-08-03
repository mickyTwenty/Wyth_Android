package com.seatus.BaseClasses;

import android.content.Intent;
import android.text.TextUtils;

import com.facebook.AccessToken;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rohail on 5/31/2016.
 */
public abstract class FacebookAuthActivity extends FragmentHandlingActivity {


    protected List<String> mPermissions = Arrays.asList("email", "user_friends");
    protected LoginManager mLoginManager;
    protected FacebookCallback mFacebookCallback;
    protected CallbackManager mCallbackManager;
    private fbLoginIface LoginInterface;
    private fbLoginFailureIface fbLoginFailureIface;

    public void loginWithFaceBook(fbLoginIface iFace) {
        mLoginManager.logInWithReadPermissions(this, mPermissions);
        this.LoginInterface = iFace;
    }

    public void loginWithFaceBook(fbLoginIface iFace, fbLoginFailureIface failureListener) {
        mLoginManager.logInWithReadPermissions(this, mPermissions);
        this.LoginInterface = iFace;
        this.fbLoginFailureIface = failureListener;
    }

    public void initFaceBook() {
        FacebookSdk.sdkInitialize(context);
        AppEventsLogger.activateApp(context);
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
                    if (fbLoginFailureIface != null)
                        fbLoginFailureIface.loginCanceled();
                }

                @Override
                public void onError(FacebookException e) {
                    Log("LoginActivity", "facebook login failed error");
                    if (fbLoginFailureIface != null)
                        fbLoginFailureIface.loginFailed(e);
                    else
                        makeSnackbar("Facebook Login Failed");
                }
            };

        if (mCallbackManager == null)
            mCallbackManager = CallbackManager.Factory.create();
        if (mLoginManager == null)
            mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(mCallbackManager, mFacebookCallback);
    }

    public void getFacebookFriends(fbFriendsInterface iface) {
        if (getUserItem() != null && !TextUtils.isEmpty(getUserItem().user_id)) {
            GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), (objects, response) -> {
                iface.OnFacebookFriendsResponse(objects, response);
            }).executeAsync();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface fbLoginIface {
        void FBLoginSuccesfull(JSONObject object, GraphResponse response);
    }

    public interface fbLoginFailureIface {
        void loginCanceled();

        void loginFailed(FacebookException e);
    }

    public interface fbFriendsInterface {
        void OnFacebookFriendsResponse(JSONArray object, GraphResponse response);
    }

    ;
}
