package com.seatus.Fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.CheckBox;

import com.facebook.AccessToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.seatus.Activities.AccountsActivity;
import com.seatus.Activities.MainActivity;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.BaseClasses.FacebookAuthActivity;
import com.seatus.Dialogs.SeatUsDialog;
import com.seatus.Dialogs.VerificationDialog;
import com.seatus.Interfaces.VerificationSuccessInterface;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Services.MyFirebaseInstanceIdService;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;
import com.seatus.ViewModels.Resource;
import com.seatus.Views.RippleView;
import com.seatus.Views.TitleBar;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rohail on 27-Sep-17.
 */

public class LoginFragment extends BaseFragment implements VerificationSuccessInterface {

    @BindView(R.id.btn_login)
    RippleView btnLogin;
    @BindView(R.id.field_email)
    TextInputEditText fieldEmail;
    @BindView(R.id.field_password)
    TextInputEditText fieldPassword;
    @BindView(R.id.inputlayout_email)
    TextInputLayout inputlayoutEmail;
    @BindView(R.id.inputlayout_pass)
    TextInputLayout inputlayoutPass;

    @BindView(R.id.checkbox_remember)
    CheckBox checkboxRememberMe;

    public static final String KEY_REMEMBER = "remember_flag";
    private static final String KEY_USER = "user";
    private static final String KEY_PASS = "pass";
    public LiveData<Resource<WebResponse<UserItem>>> loginObserver = new MutableLiveData<>();

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_login;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {

    }

    @Override
    public void inits() {

        ((FacebookAuthActivity) getContext()).initFaceBook();

        if (isLoaded)
            ((AccountsActivity) getContext()).animateToLogin();

        ((AccountsActivity) getContext()).btnRegister.setText(AccountsActivity.BUTTON_ON_LOGIN);

        if (PreferencesManager.getBoolean(KEY_REMEMBER)) {
            checkboxRememberMe.setChecked(true);
            String username = PreferencesManager.getString(KEY_USER);
            String pass = PreferencesManager.getString(KEY_PASS);
            fieldEmail.setText(username);
            fieldPassword.setText(pass);
        }

    }

    @Override
    public void setEvents() {
    }

    @OnClick({R.id.btn_forgotpass, R.id.btn_login, R.id.btn_login_fb,R.id.btn_signup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_forgotpass:
                DialogHelper.showForgotPasswordDialog(getContext(), dialog -> actionForgotPass(dialog));
                break;
            case R.id.btn_login:
                actionLogin();
                break;
            case R.id.btn_login_fb:
                actionFbLogin();
                break;
            case R.id.btn_signup:
                getFragmentActivity().replaceFragmentWithBackstack(new RegisterNewFragment());
                break;
        }
    }

    private void actionForgotPass(SeatUsDialog dialog) {
        String email = dialog.getField().getText().toString().trim();
        if (!StaticMethods.isValidEmail(email)) {
            dialog.setError(getContext().getString(R.string.error_validation_email));
            return;
        } else
            dialog.setError(null);

        StaticMethods.hideSoftKeyboard(getContext());
        dialog.startProgress();
        WebServiceFactory.getInstance().resetPassword(email).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                try {
                    if (response.body().isSuccess()) {
                        dialog.dismiss();
                        DialogHelper.showAlertDialog(getContext(), response.body().message);
                    } else
                        dialog.setError(response.body().message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.stopProgress();
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                dialog.setError(getString(R.string.error_connection));
                dialog.stopProgress();
            }
        });
    }

    private boolean areFieldsValid(String email, String pass) {

        boolean valid = true;

        if (!StaticMethods.isValidEmail(email)) {
            inputlayoutEmail.setError(getString(R.string.error_validation_email));
            valid = false;
        } else inputlayoutEmail.setError(null);

        if (pass.length() < 4) {
            inputlayoutPass.setError(getString(R.string.error_validation_empty));
            valid = false;
        } else inputlayoutPass.setError(null);


        return valid;
    }

    private void actionLogin() {

        String email = fieldEmail.getText().toString().trim();
        String pass = fieldPassword.getText().toString().trim();

        if (!areFieldsValid(email, pass))
            return;

        hideKeyboard();
        loginObserver = getActivityViewModel().login(email, pass, MyFirebaseInstanceIdService.getAppToken());
        loginObserver.observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    UserItem item = webResponseResource.data.body;
                    loginSuccess(item);
                    break;
                case action_validation:
                    hideLoader();
                    DialogHelper.showEmailValidationDialog(getContext(), this);
                    break;
                case error:
                    hideLoader();
                    makeSnackbar(webResponseResource.data.message);
                    break;
                case connection_error:
                    hideLoader();
                    makeConnectionSnackbar();
                    break;
            }
        });
    }

    private void loginSuccess(UserItem item) {
        getActivityViewModel().setUserItem(item);
        saveCredentials();
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/android1");
        FirebaseMessaging.getInstance().subscribeToTopic(String.format("/topics/user_%s", item.user_id));
        getContext().startActivity(new Intent(getContext(), MainActivity.class));
        getContext().finish();
    }

    private void saveCredentials() {

        String email = fieldEmail.getText().toString().trim();
        String pass = fieldPassword.getText().toString().trim();

        boolean check = checkboxRememberMe.isChecked();
        PreferencesManager.putBoolean(KEY_REMEMBER, check);
        if (check) {
            PreferencesManager.putString(KEY_USER, email);
            PreferencesManager.putString(KEY_PASS, pass);
        }
    }

    private void actionFbLogin() {

        ((FacebookAuthActivity) getContext()).loginWithFaceBook((object, response) -> {
            try {
                String name = object.getString("name");
                String id = object.getString("id");

                getActivityViewModel().fbLogin(AccessToken.getCurrentAccessToken().getToken(), MyFirebaseInstanceIdService.getAppToken()).observe(this, webResponseResource -> {
                    switch (webResponseResource.status) {
                        case loading:
                            showLoader();
                            break;
                        case success:
                            hideLoader();
                            UserItem item = webResponseResource.data.body;
                            loginSuccess(item);
                            break;
                        case action_validation:
                            hideLoader();
                            DialogHelper.showEmailValidationDialog(getContext(), this);
                            break;
                        case action_need_signup:
                            hideLoader();
                            ((AccountsActivity) getContext()).animateToRegister(RegisterNewFragment.newInstance(AccessToken.getCurrentAccessToken().getToken(), name));
                            break;
                        default:
                            hideLoader();
                            makeSnackbar(webResponseResource.data);
                            break;
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void userVerified(UserItem user) {
        loginSuccess(user);
    }

    @Override
    public void onResend(VerificationDialog dialog) {
        getActivityViewModel().resendVerificationCode(fieldEmail.getText().toString().trim()).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    dialog.startProgress();
                    break;
                case success:
                    dialog.stopProgress();
                    dialog.setError(webResponseResource.data.message);
                    break;
                default:
                    dialog.stopProgress();
                    break;
            }
        });
    }
}
