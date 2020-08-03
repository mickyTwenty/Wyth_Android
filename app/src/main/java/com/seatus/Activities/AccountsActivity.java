package com.seatus.Activities;

import android.os.Bundle;
import android.support.v4.widget.Space;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.BaseClasses.FacebookAuthActivity;
import com.seatus.Fragments.LoginFragment;
import com.seatus.Fragments.RegisterFirstFragment;
import com.seatus.R;
import com.seatus.Utils.AppStore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohail on 27-Sep-17.
 */

public class AccountsActivity extends FacebookAuthActivity {

    public static final String BUTTON_ON_LOGIN = "sign up";
    public static final String BUTTON_ON_REGISTER = "LOGIN";
    public static final String BUTTON_ON_REGISTER_SECOND = "BACK";

    @BindView(R.id.parentView)
    public RelativeLayout parentView;

    @BindView(R.id.frame_register)
    public FrameLayout frameRegister;

    @BindView(R.id.btn_register)
    public TextView btnRegister;

    @BindView(R.id.progress)
    public FrameLayout progress;

    @BindView(R.id.spaceLeft)
    public Space spaceLeft;
    public float registerX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        ButterKnife.bind(this);
        setParentView(parentView);

        frameRegister.post(() -> registerX = frameRegister.getX());
        replaceFragment(new LoginFragment());
        progress.setOnClickListener(view -> Log.e("Clicked", "Loader"));
    }

//    @OnClick(R.id.frame_register)
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.frame_register:
//                switch (btnRegister.getText().toString()) {
//                    case BUTTON_ON_LOGIN:
//                        AppStore.getInstance().setTmpRegisterItem(null);
//                        animateToRegister(new RegisterFirstFragment());
//                        break;
//                    case BUTTON_ON_REGISTER:
//                        onBackPressed();
//                        break;
//                    case BUTTON_ON_REGISTER_SECOND:
//                        onBackPressed();
//                        break;
//                }
//                break;
//        }
//    }

    public void animateToRegister(BaseFragment fragment) {
        if (replaceFragmentWithBackstack(fragment)) {
            frameRegister.animate().x(0.0f).setDuration(500).start();
        }
    }

    @Override
    public boolean actionBack() {
        if (getCurrentFragment() instanceof RegisterFirstFragment) {
            if (((RegisterFirstFragment) getCurrentFragment()).hasData()) {
                new AlertDialog.Builder(context, R.style.CustomDialog)
                        .setTitle("Exit")
                        .setMessage("This action will cause you to lose all the data.\n Would you like to still proceed?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> super.actionBack())
                        .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
                return true;
            }
        }
        return false;
    }

    public void animateToLogin() {
        frameRegister.animate().x(registerX).setDuration(500).start();
    }

    @Override
    public void showLoader() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        progress.setVisibility(View.GONE);
    }

    @Override
    protected int getFrameLayoutId() {
        return R.id.frame_main;
    }

}
