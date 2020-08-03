package com.seatus.BaseClasses;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.seatus.Activities.MainActivity;
import com.seatus.Models.UserItem;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.permissionutils.FragmentManagePermission;
import com.seatus.ViewModels.ActivityViewModel;
import com.seatus.Views.TitleBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends FragmentManagePermission {

    private Activity context;
    public FragmentHandlingActivity frragmentActivity;
    public Handler handler;
    public boolean isLoaded = false;

    public Unbinder unbinder;
    private Gson gson;
    public View view;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //    if (view == null)
        view = inflater.inflate(getLayout(), container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    protected abstract int getLayout();

    protected abstract void getTitleBar(TitleBar titleBar);

    protected abstract void activityCreated(Bundle savedInstanceState);



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new Handler(Looper.getMainLooper());
        if (context == null)
            context = getActivity();

        inits();
        setEvents();

        if (context instanceof MainActivity) {
            ((MainActivity) context).titleBar.resetTitleBar();
            getTitleBar(((MainActivity) context).titleBar);
        }

        activityCreated(savedInstanceState);
        isLoaded = true;
    }

    public ActivityViewModel getActivityViewModel() {
        return ViewModelProviders.of(getFragmentActivity()).get(ActivityViewModel.class);
    }

    public Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }


    public void showLoader() {
        StaticMethods.hideSoftKeyboard(getContext());
        getFragmentActivity().showLoader();
    }

    public void hideLoader() {
        getFragmentActivity().hideLoader();
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (AppConstants.sDisableFragmentAnimations) {
            Animation a = new Animation() {
            };
            a.setDuration(0);
            return a;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
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

    public Activity getContext() {
        if (context == null)
            context = getActivity();

        return context;
    }

    public UserItem getUserItem() {
        return getActivityViewModel().getUserItem();
    }

    public LiveData<UserItem> getUserLiveData() {
        return getActivityViewModel().getUserLiveData();
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

    public String getFieldTexT(EditText edit) {
        try {
            return edit.getText().toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    abstract public void inits();

    abstract public void setEvents();


    public void Log(String tag, String value) {
        if (AppConstants.onTest) Log.e(tag, value);
    }

    public void Log(String value) {
        if (AppConstants.onTest) Log.e(getClass().getSimpleName() + "", value);
    }

    public void commingSoonToast() {
        makeToast("Will be implemented in BETA");
    }

    public void makeConnectionSnackbar() {
        ((BaseActivity) context).makeConnectionSnackbar();
    }

    public void makeSnackbar(WebResponse body) {
        ((BaseActivity) context).makeSnackbar(body);
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

    @Override
    public void onStop() {
        try {
            getFragmentActivity().hideLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }


}
