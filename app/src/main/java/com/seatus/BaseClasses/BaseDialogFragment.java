package com.seatus.BaseClasses;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.seatus.Models.UserItem;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;

import butterknife.Unbinder;

public abstract class BaseDialogFragment extends DialogFragment {

    private Activity context;
    private UserItem userItem;
    View parentView;

    public Unbinder unbinder;

    public FragmentHandlingActivity frragmentActivity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (context == null)
            context = ((Activity) getActivity());

    }

    public Activity getContext() {
        if (context == null)
            context = getActivity();

        return context;
    }

    public UserItem getUserItem() {
        if (userItem == null)
            userItem = PreferencesManager.getObject(AppConstants.KEY_USER, UserItem.class);

        return userItem;
    }

    public void setUserItem(UserItem userItem) {
        this.userItem = userItem;
    }

    public void hideKeyboard() {
        StaticMethods.hideSoftKeyboard(getActivity());
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        if (context == null)
            context = (Activity) activity;
    }


    public void Log(String tag, String value) {
        if (AppConstants.onTest) Log.e(tag, value);
    }

    public void Log(String value) {
        if (AppConstants.onTest) Log.e(getClass().getSimpleName() + "", value);
    }

    public void makeConnectionToast() {
        makeSnackbar("Connection Timeout !");
    }

    public void makeSnackbar(String str) {
        ((BaseActivity) context).makeSnackbar(str);
    }

    public void makeToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void makeToastLong(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public String getText(String string) {
        if (string == null)
            return "";
        else return string;
    }

    public boolean isResponseSuccuess(WebResponse response) {
        try {
            if (response.isSuccess())
                return true;
            else if (response.isExpired()) {
                makeSnackbar("Session Expired");
//                getFragmentActivity().sessionExpired();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log("Error", e.getMessage() + "");
        }
        return false;
    }

    public void showLoader() {
        StaticMethods.hideSoftKeyboard(getContext());
        getFragmentActivity().showLoader();
    }

    public void hideLoader() {
        getFragmentActivity().hideLoader();
    }


    public FragmentHandlingActivity getFragmentActivity() {
        if (context == null)
            context = (Activity) getActivity();

        if (frragmentActivity == null) {
            if (context instanceof FragmentHandlingActivity) {
                frragmentActivity = (FragmentHandlingActivity) context;
            }
        }
        return frragmentActivity;
    }

}
