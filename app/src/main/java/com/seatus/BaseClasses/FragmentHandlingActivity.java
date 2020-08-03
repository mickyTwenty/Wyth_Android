package com.seatus.BaseClasses;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import com.seatus.R;
import com.seatus.Utils.AppConstants;


public abstract class FragmentHandlingActivity extends BaseActivity {

    public boolean transactionDelay = false;
    private boolean isCloseDialogEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public boolean actionBack() {
        try {
            if (needDelay())
                return true;

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setCloseDialogEnabled(boolean closeDialogEnabled) {
        isCloseDialogEnabled = closeDialogEnabled;
    }

    @Override
    public void onBackPressed() {
        if (!actionBack()) {
            if (!isCloseDialogEnabled)
                super.onBackPressed();
            else
                new AlertDialog.Builder(context, R.style.CustomDialog)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit ?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> super.onBackPressed())
                        .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
        }

    }

    public void actionBackTill(int count) {
        try {
            AppConstants.sDisableFragmentAnimations = true;

            if (getSupportFragmentManager() == null)
                return;
            if (getSupportFragmentManager().getBackStackEntryCount() <= count)
                return;
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(
                    count);
            if (entry != null) {
                getSupportFragmentManager().popBackStack(entry.getId(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            AppConstants.sDisableFragmentAnimations = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(getFrameLayoutId());
    }

    protected abstract int getFrameLayoutId();

    public boolean replaceFragmentWithBackstack(Fragment fragment) {
        try {

            if (needDelay())
                return false;

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
            trans.replace(getFrameLayoutId(), fragment);
            trans.addToBackStack(fragment.getClass().getName());
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean needDelay() {
        if (transactionDelay)
            return true;

        transactionDelay = true;
        handler.postDelayed(() -> {
            try {
                transactionDelay = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 600);

        return false;
    }


    public void replaceFragmentWithBackstack(final Fragment fragment, boolean delay, boolean animation) {

        if (needDelay())
            return;

        if (delay) {
            handler.postDelayed(() -> {
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                if (animation)
                    trans.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
                trans.replace(getFrameLayoutId(), fragment);
                trans.addToBackStack(fragment.getClass().getName());
                trans.commit();
            }, 300);
        } else {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            if (animation)
                trans.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
            trans.replace(getFrameLayoutId(), fragment);
            trans.addToBackStack(fragment.getClass().getName());
            trans.commit();
        }
    }

    public void replaceFragment(Fragment fragment) {

        if (needDelay())
            return;

        clearBackStack();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(getFrameLayoutId(), fragment);
        trans.commit();
    }

    public void replaceFragment(Fragment fragment, int millisDelay) {

        if (needDelay())
            return;

        handler.postDelayed(() -> {
            clearBackStack();
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            trans.replace(getFrameLayoutId(), fragment);
            trans.commit();
        }, millisDelay);
    }

    public void replaceFragmentWithBackstack(Fragment fragment, int millisDelay) {

        if (needDelay())
            return;

        handler.postDelayed(() -> {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
            trans.replace(getFrameLayoutId(), fragment);
            trans.addToBackStack(fragment.getClass().getName());
            trans.commit();
        }, millisDelay);
    }

    public void replaceFragment(Fragment fragment, boolean animation) {

        if (needDelay())
            return;


        clearBackStack();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        if (animation)
            trans.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
        trans.replace(getFrameLayoutId(), fragment);
        trans.commit();
    }

    public void clearBackStack() {
        try {
            AppConstants.sDisableFragmentAnimations = true;
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            AppConstants.sDisableFragmentAnimations = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void resetDelay() {
        transactionDelay = false;
    }
}
