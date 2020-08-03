package com.seatus.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;

import com.google.firebase.messaging.FirebaseMessaging;
import com.seatus.Activities.AccountsActivity;
import com.seatus.Activities.MainActivity;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Dialogs.VerificationDialog;
import com.seatus.Interfaces.VerificationSuccessInterface;
import com.seatus.Models.CityItem;
import com.seatus.Models.StateItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.database.AppDatabase;
import com.seatus.Views.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

import static com.seatus.Fragments.LoginFragment.KEY_REMEMBER;

/**
 * Created by rohail on 27-Sep-17.
 */

public class RegisterSecondFragment extends BaseFragment implements VerificationSuccessInterface {

    @BindView(R.id.field_state)
    TextInputEditText fieldState;
    @BindView(R.id.inputlayout_state)
    TextInputLayout inputlayoutState;
    @BindView(R.id.field_city)
    TextInputEditText fieldCity;
    @BindView(R.id.inputlayout_source)
    TextInputLayout inputlayoutSource;
    @BindView(R.id.field_source)
    TextInputEditText fieldSource;
    @BindView(R.id.inputlayout_city)
    TextInputLayout inputlayoutCity;
    @BindView(R.id.field_zip)
    TextInputEditText fieldZip;
    @BindView(R.id.inputlayout_zip)
    TextInputLayout inputlayoutZip;
    @BindView(R.id.field_yearofgrad)
    TextInputEditText fieldYearofgrad;
    @BindView(R.id.inputlayout_yearofgrad)
    TextInputLayout inputlayoutYearofgrad;
    @BindView(R.id.field_pass)
    TextInputEditText fieldPass;
    @BindView(R.id.inputlayout_pass)
    TextInputLayout inputlayoutPass;
    @BindView(R.id.field_pass2)
    TextInputEditText fieldPass2;
    @BindView(R.id.inputlayout_pass2)
    TextInputLayout inputlayoutPass2;

    @BindView(R.id.checkbox_terms)
    CheckBox checkboxTerms;

    StateItem selectedState;
    CityItem selectedCity;
    String selectedYearOfGrad;

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void onPause() {
        super.onPause();
        AppStore.getInstance().getTmpRegisterItem().selected_state = selectedState;
        AppStore.getInstance().getTmpRegisterItem().selected_city = selectedCity;
        AppStore.getInstance().getTmpRegisterItem().zip = fieldZip.getText().toString();
        AppStore.getInstance().getTmpRegisterItem().graduation_year = selectedYearOfGrad;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_register2;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {

    }

    @Override
    public void inits() {
        ((AccountsActivity) getContext()).btnRegister.setText(AccountsActivity.BUTTON_ON_REGISTER_SECOND);

        if (AppStore.getInstance().getTmpRegisterItem().selected_state != null) {
            selectedState = AppStore.getInstance().getTmpRegisterItem().selected_state;
            fieldState.setText(AppStore.getInstance().getTmpRegisterItem().selected_state.name);
        }

        if (AppStore.getInstance().getTmpRegisterItem().selected_city != null) {
            selectedCity = AppStore.getInstance().getTmpRegisterItem().selected_city;
            fieldCity.setText(AppStore.getInstance().getTmpRegisterItem().selected_city.name);
        }

        if (!TextUtils.isEmpty(AppStore.getInstance().getTmpRegisterItem().graduation_year)) {
            selectedYearOfGrad = AppStore.getInstance().getTmpRegisterItem().graduation_year;
            fieldYearofgrad.setText(AppStore.getInstance().getTmpRegisterItem().graduation_year);
        }

        fieldZip.setText(AppStore.getInstance().getTmpRegisterItem().zip);

    }

    @Override
    public void setEvents() {

        fieldZip.setOnEditorActionListener((v, actionId, event) -> {
            try {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    fieldYearofgrad.performClick();
                    hideKeyboard();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @OnClick({R.id.field_source, R.id.btn_signup_user, R.id.btn_signup_driver, R.id.field_state, R.id.field_city, R.id.field_yearofgrad})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.field_source:
                DialogHelper.showSimpleListDialog(getContext(), AppStore.getInstance().getBootUpData().reference_source, item -> fieldSource.setText(item));
                break;
            case R.id.btn_signup_user:
                actionRegister(UserItem.UserType.normal);
                break;
            case R.id.btn_signup_driver:
                actionRegister(UserItem.UserType.driver);
                break;
            case R.id.field_state:
                try {
                    DialogHelper.showLocationPickerDialog(getContext(), AppDatabase.getInstance(getContext()).stateDoa().getAll(), item -> {
                        try {
                            selectedState = (StateItem) item;
                            fieldState.setText(selectedState.name);
                            if (selectedCity != null && !selectedCity.state_id.equals(selectedState.id)) {
                                selectedCity = null;
                                fieldCity.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.field_city:
                try {
                    if (selectedState == null)
                        inputlayoutState.setError(getString(R.string.error_validation_select));
                    else {
                        inputlayoutState.setError(null);
                        DialogHelper.showLocationPickerDialog(getContext(), AppDatabase.getInstance(getContext()).cityDoa().getAllCityByStateID(selectedState.id), item -> {
                            try {
                                selectedCity = (CityItem) item;
                                fieldCity.setText(selectedCity.name);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.field_yearofgrad:
                DialogHelper.showNumberPickerDialog(getContext(), value -> {
                    selectedYearOfGrad = String.valueOf(value);
                    fieldYearofgrad.setText(selectedYearOfGrad + "");
                });
                break;
        }
    }

    private void actionRegister(UserItem.UserType userType) {

        String zip = fieldZip.getText().toString().trim();
        String pass = fieldPass.getText().toString().trim();
        String pass2 = fieldPass2.getText().toString().trim();
        String reference_source = fieldSource.getText().toString().trim();


        String phone = AppStore.getInstance().getTmpRegisterItem().phone;

        if (!areFieldsValid(selectedState, selectedCity, zip, selectedYearOfGrad, pass, pass2))
            return;

        DialogHelper.showTermsDialog(getContext(), userType, (dialog, which) -> {

            getActivityViewModel().register(
                    AppStore.getInstance().getTmpRegisterItem().first_name,
                    AppStore.getInstance().getTmpRegisterItem().last_name,
                    AppStore.getInstance().getTmpRegisterItem().gender,
                    AppStore.getInstance().getTmpRegisterItem().school_name,
                    AppStore.getInstance().getTmpRegisterItem().email,
                    phone,
                    AppStore.getInstance().getTmpRegisterItem().student_organization,
                    selectedState.id,
                    selectedCity.id,
                    zip,
                    selectedYearOfGrad,
                    pass,
                    userType.name(),
                    reference_source,
                    "dummy_token",
                    AppStore.getInstance().getTmpRegisterItem().facebookToken).observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case success:
                        hideLoader();
                        getFragmentActivity().getSupportFragmentManager().popBackStack();
                        getFragmentActivity().getSupportFragmentManager().popBackStack();
                        handler.postDelayed(() -> DialogHelper.showEmailValidationDialog(getContext(), this), 1000);
                        break;
                    default:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        break;
                }
            });
        });
    }

    private boolean areFieldsValid(StateItem selectedState, CityItem selectedCity, String zip, String selectedYearOfGrad, String pass, String pass2) {
        boolean valid = true;

        if (selectedState == null) {
            inputlayoutState.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutState.setError(null);

        if (selectedCity == null) {
            inputlayoutCity.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutCity.setError(null);

        if (zip.length() < 4) {
            inputlayoutZip.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutZip.setError(null);

        if (TextUtils.isEmpty(selectedYearOfGrad)) {
            inputlayoutYearofgrad.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutYearofgrad.setError(null);

        if (pass.length() < 6) {
            inputlayoutPass.setError(getString(R.string.error_validation_empty));
            valid = false;
        } else {
            inputlayoutPass.setError(null);
            if (!pass.equals(pass2)) {
                inputlayoutPass2.setError(getString(R.string.error_validation_donotmatch));
                valid = false;
            } else inputlayoutPass2.setError(null);
        }

//        if (!checkboxTerms.isChecked()) {
//            makeSnackbar("Please Accept User Agreement");
//            valid = false;
//        }

        return valid;
    }


    @Override
    public void userVerified(UserItem user) {
        getActivityViewModel().setUserItem(user);
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/android1");
        FirebaseMessaging.getInstance().subscribeToTopic(String.format("/topics/user_%s", user.user_id));
        getContext().startActivity(new Intent(getContext(), MainActivity.class));
        PreferencesManager.putBoolean(KEY_REMEMBER, false);
        getContext().finish();
    }

    @Override
    public void onResend(VerificationDialog dialog) {
        getActivityViewModel().resendVerificationCode(AppStore.getInstance().getTmpRegisterItem().email).observe(getFragmentActivity(), webResponseResource -> {
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
