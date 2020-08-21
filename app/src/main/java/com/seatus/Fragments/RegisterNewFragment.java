package com.seatus.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
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
import com.seatus.Models.SchoolItem;
import com.seatus.Models.StateItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Services.MyFirebaseInstanceIdService;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.database.AppDatabase;
import com.seatus.Views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.seatus.Fragments.LoginFragment.KEY_REMEMBER;

/**
 * Created by rohail on 27-Sep-17.
 */

public class RegisterNewFragment extends BaseFragment implements VerificationSuccessInterface {


    private static final String ARG_FB_TOKEN = "fb_token";
    private static final String ARG_FB_NAME = "fb_name";

    @BindView(R.id.field_firstname)
    TextInputEditText fieldFirstName;
    @BindView(R.id.inputlayout_firstname)
    TextInputLayout inputlayoutFirstName;

    @BindView(R.id.field_lastname)
    TextInputEditText fieldLastName;
    @BindView(R.id.inputlayout_lastname)
    TextInputLayout inputlayoutLastName;

    @BindView(R.id.inputlayout_gender)
    TextInputLayout inputlayoutGender;

    @BindView(R.id.field_schoolname)
    TextInputEditText fieldSchoolname;
    @BindView(R.id.inputlayout_schoolname)
    TextInputLayout inputlayoutSchoolname;

    @BindView(R.id.field_studentOrg)
    TextInputEditText fieldStudentOrg;
    @BindView(R.id.inputlayout_studentOrg)
    TextInputLayout inputlayoutStudentOrg;

    @BindView(R.id.field_schoolemail)
    TextInputEditText fieldSchoolemail;
    @BindView(R.id.inputlayout_schoolemail)
    TextInputLayout inputlayoutSchoolemail;
    @BindView(R.id.field_phone)
    TextInputEditText fieldPhone;
    @BindView(R.id.inputlayout_phone)
    TextInputLayout inputlayoutPhone;

    @BindView(R.id.field_gender)
    TextInputEditText fieldGender;


    @BindView(R.id.inputlayout_source)
    TextInputLayout inputlayoutSource;
    @BindView(R.id.field_source)
    TextInputEditText fieldSource;
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


    String selectedYearOfGrad;
    private String facebookToken = null;

    public static RegisterNewFragment newInstance(String fbToken, String fbName) {
        Bundle args = new Bundle();
        args.putString(ARG_FB_TOKEN, fbToken);
        args.putString(ARG_FB_NAME, fbName);
        RegisterNewFragment fragment = new RegisterNewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        checkIfFromFacebook(getArguments());
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_register_new;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {

    }

    @Override
    public void inits() {


        if (!TextUtils.isEmpty(AppStore.getInstance().getTmpRegisterItem().graduation_year)) {
            selectedYearOfGrad = AppStore.getInstance().getTmpRegisterItem().graduation_year;
            fieldYearofgrad.setText(AppStore.getInstance().getTmpRegisterItem().graduation_year);
        }

    }


    private void checkIfFromFacebook(Bundle arguments) {
        try {
            String fbToken = arguments.getString(ARG_FB_TOKEN);
            if (fbToken != null) {
                facebookToken = fbToken;
                inputlayoutFirstName.setHint("Name (From Facebook)");
            }
            String[] fbName = arguments.getString(ARG_FB_NAME, "").split(" ");
            fieldFirstName.setText(fbName[0]);
            fieldLastName.setText(fbName[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setEvents() {

        fieldPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        fieldLastName.setOnEditorActionListener((v, actionId, event) -> {
            try {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    fieldGender.performClick();
                    hideKeyboard();
                    fieldLastName.clearFocus();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });

        fieldPhone.setOnEditorActionListener((v, actionId, event) -> {
            try {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    fieldYearofgrad.performClick();
                    hideKeyboard();
                    fieldPhone.clearFocus();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });

    }

    @OnClick({R.id.field_gender, R.id.field_schoolname, R.id.field_source, R.id.btn_signup_user, R.id.btn_signup_driver, R.id.field_yearofgrad})
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
            case R.id.field_yearofgrad:
                DialogHelper.showNumberPickerDialog(getContext(), value -> {
                    selectedYearOfGrad = String.valueOf(value);
                    fieldYearofgrad.setText(selectedYearOfGrad + "");
                });
                break;
            case R.id.field_gender:
                DialogHelper.showGenderDialog(getContext(), item -> fieldGender.setText(item));
                break;
            case R.id.field_schoolname:
                ArrayList<SchoolItem> schools = AppStore.getInstance().getSchoolsList(getContext());
                if (schools == null || schools.isEmpty()) {
                    showLoader();
                    WebServiceFactory.getInstance().getAllSchools().enqueue(new Callback<WebResponse<ArrayList<SchoolItem>>>() {
                        @Override
                        public void onResponse(Call<WebResponse<ArrayList<SchoolItem>>> call, Response<WebResponse<ArrayList<SchoolItem>>> response) {
                            hideLoader();
                            if (response.isSuccessful() && response.body().isSuccess() && !response.body().body.isEmpty()) {
                                ArrayList<SchoolItem> schoolsList = response.body().body;
                                AppDatabase.getInstance(getContext()).schoolsDao().deleteAll();
                                AppDatabase.getInstance(getContext()).schoolsDao().insertAll(schoolsList);
                                DialogHelper.showSchoolsDialog(getContext(), schoolsList, item -> fieldSchoolname.setText(item));
                                PreferencesManager.putLong("last_update", System.currentTimeMillis());
                            }
                        }

                        @Override
                        public void onFailure(Call<WebResponse<ArrayList<SchoolItem>>> call, Throwable t) {
                            hideLoader();
                        }
                    });
                } else
                    DialogHelper.showSchoolsDialog(getContext(), schools, item -> fieldSchoolname.setText(item));
                break;
        }
    }

    private void actionRegister(UserItem.UserType userType) {


        String firstname = fieldFirstName.getText().toString().trim();
        String lastname = fieldLastName.getText().toString().trim();
        String schoolname = fieldSchoolname.getText().toString().trim();
        String schoolemail = fieldSchoolemail.getText().toString().trim();
        String phone = fieldPhone.getText().toString().trim();
        String gender = fieldGender.getText().toString().trim();


        String pass = fieldPass.getText().toString().trim();
        String pass2 = fieldPass2.getText().toString().trim();
        String reference_source = fieldSource.getText().toString().trim();

        String studentOrg = fieldStudentOrg.getText().toString().trim();

        if (!areFieldsValid(firstname, lastname, gender, schoolname, schoolemail, phone, selectedYearOfGrad, pass, pass2))
            return;

        String email = fieldSchoolemail.getText().toString().trim();
        String passw = fieldPass.getText().toString().trim();

        boolean check = true;
        PreferencesManager.putBoolean(KEY_REMEMBER, check);
        if (check) {
            PreferencesManager.putString("user", email);
            PreferencesManager.putString("pass", passw);
        }

        String deviceToken = MyFirebaseInstanceIdService.getAppToken();

        DialogHelper.showTermsDialog(getContext(), userType, (dialog, which) -> {

            getActivityViewModel().register(
                    firstname,
                    lastname,
                    gender,
                    schoolname,
                    schoolemail,
                    phone,
                    studentOrg,
                    null,
                    null,
                    null,
                    selectedYearOfGrad,
                    pass,
                    userType.name(),
                    reference_source,
                    deviceToken,
                    facebookToken).observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case success:
                        hideLoader();
                        getFragmentActivity().getSupportFragmentManager().popBackStack();
                        getFragmentActivity().getSupportFragmentManager().popBackStack();
                        makeSnackbar(webResponseResource.data.message);
                        //handler.postDelayed(() -> DialogHelper.showEmailValidationDialog(getContext(), this), 1000);
                        break;
                    default:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        break;
                }
            });
        });
    }

    private boolean areFieldsValid(String firstname, String lastname, String gender, String schoolname, String schoolemail, String phone, String selectedYearOfGrad, String pass, String pass2) {
        boolean valid = true;

        if (firstname.length() < 1) {
            inputlayoutFirstName.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutFirstName.setError(null);

        if (lastname.length() < 1) {
            inputlayoutLastName.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutLastName.setError(null);

        if (gender.length() < 1) {
            inputlayoutGender.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutGender.setError(null);

        if (schoolname.length() < 1) {
            inputlayoutSchoolname.setError(getString(R.string.error_validation_null));
            valid = false;
        } else inputlayoutSchoolname.setError(null);

        if (!StaticMethods.isValidEmail(schoolemail)) {
            inputlayoutSchoolemail.setError(getString(R.string.error_validation_email));
            valid = false;
        } else inputlayoutSchoolemail.setError(null);

        if (phone.length() < 4) {
            inputlayoutPhone.setError(getString(R.string.error_validation_empty));
            valid = false;
        } else inputlayoutPhone.setError(null);

        /*if (TextUtils.isEmpty(selectedYearOfGrad)) {
            inputlayoutYearofgrad.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutYearofgrad.setError(null);*/

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
