package com.seatus.BaseClasses;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;

import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Views.TitleBar;

/**
 * Created by rohail on 13-Oct-17.
 */

public abstract class ChildFragmentHandlingFragment extends BaseFragment {

    boolean transactionDelay = false;



    public boolean actionBack() {
        try {
            if (needDelay())
                return true;

            if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                getChildFragmentManager().popBackStack();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void actionBackTill(int count) {
        try {
            AppConstants.sDisableFragmentAnimations = true;

            if (getChildFragmentManager() == null)
                return;
            if (getChildFragmentManager().getBackStackEntryCount() <= count)
                return;
            FragmentManager.BackStackEntry entry = getChildFragmentManager().getBackStackEntryAt(
                    count);
            if (entry != null) {
                getChildFragmentManager().popBackStack(entry.getId(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            AppConstants.sDisableFragmentAnimations = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fragment getCurrentFragment() {
        return getChildFragmentManager().findFragmentById(getFrameLayoutId());
    }

    protected abstract int getFrameLayoutId();

    public void ReplaceFragmentWithBackstack(Fragment fragment) {
        try {

            if (needDelay())
                return;

            FragmentTransaction trans = getChildFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
            trans.replace(getFrameLayoutId(), fragment);
            trans.addToBackStack(fragment.getClass().getName());
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public void ReplaceFragmentWithBackstack(final Fragment fragment, boolean delay, boolean animation) {

        if (needDelay())
            return;

        if (delay) {
            handler.postDelayed(() -> {
                FragmentTransaction trans = getChildFragmentManager().beginTransaction();
                if (animation)
                    trans.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
                trans.replace(getFrameLayoutId(), fragment);
                trans.addToBackStack(fragment.getClass().getName());
                trans.commit();
            }, 300);
        } else {
            FragmentTransaction trans = getChildFragmentManager().beginTransaction();
            if (animation)
                trans.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
            trans.replace(getFrameLayoutId(), fragment);
            trans.addToBackStack(fragment.getClass().getName());
            trans.commit();
        }
    }

    public void ReplaceFragment(Fragment fragment) {

        if (needDelay())
            return;

        clearBackStack();
        FragmentTransaction trans = getChildFragmentManager().beginTransaction();
        trans.replace(getFrameLayoutId(), fragment);
        trans.commit();
    }

    public void ReplaceFragment(Fragment fragment, boolean animation) {

        if (needDelay())
            return;


        clearBackStack();
        FragmentTransaction trans = getChildFragmentManager().beginTransaction();
        if (animation)
            trans.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out, R.anim.push_left_in, R.anim.push_left_out);
        trans.replace(getFrameLayoutId(), fragment);
        trans.commit();
    }

    public void clearBackStack() {
        try {
            AppConstants.sDisableFragmentAnimations = true;
            getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            AppConstants.sDisableFragmentAnimations = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {

    }
}
