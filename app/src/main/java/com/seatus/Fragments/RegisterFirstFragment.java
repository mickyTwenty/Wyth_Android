package com.seatus.Fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.media.session.IMediaControllerCallback;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.seatus.Activities.AccountsActivity;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Models.SchoolItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
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

/**
 * Created by rohail on 27-Sep-17.
 */

public class RegisterFirstFragment extends BaseFragment {

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
    @BindView(R.id.field_schoolorg)
    TextInputEditText fieldStudentOrg;
    @BindView(R.id.inputlayout_schoolname)
    TextInputLayout inputlayoutSchoolname;
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


    public static RegisterFirstFragment newInstance(String fbToken, String fbName) {
        Bundle args = new Bundle();
        args.putString(ARG_FB_TOKEN, fbToken);
        args.putString(ARG_FB_NAME, fbName);
        RegisterFirstFragment fragment = new RegisterFirstFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        checkIfFromFacebook(getArguments());
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_register1;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        fieldStudentOrg.setError("");
    }

    @Override
    public void inits() {
        ((AccountsActivity) getContext()).btnRegister.setText(AccountsActivity.BUTTON_ON_REGISTER);
        fieldPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    @Override
    public void setEvents() {

        fieldLastName.setOnEditorActionListener((v, actionId, event) -> {
            try {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    fieldGender.performClick();
                    hideKeyboard();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });

    }

    @OnClick({R.id.btn_continue, R.id.field_schoolname, R.id.field_gender})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_continue:
                actionContinue();
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

    private void actionContinue() {

        String firstname = fieldFirstName.getText().toString().trim();
        String lastname = fieldLastName.getText().toString().trim();
        String schoolname = fieldSchoolname.getText().toString().trim();
        String schoolemail = fieldSchoolemail.getText().toString().trim();
        String phone = fieldPhone.getText().toString().trim();
        String gender = fieldGender.getText().toString().trim();

        if (!areFieldsValid(firstname, lastname, gender, schoolname, schoolemail, phone))
            return;

        AppStore.getInstance().getTmpRegisterItem().first_name = firstname;
        AppStore.getInstance().getTmpRegisterItem().last_name = lastname;
        AppStore.getInstance().getTmpRegisterItem().school_name = schoolname;
        AppStore.getInstance().getTmpRegisterItem().email = schoolemail;
        AppStore.getInstance().getTmpRegisterItem().phone = phone;
        AppStore.getInstance().getTmpRegisterItem().student_organization = fieldStudentOrg.getText().toString().trim();
        AppStore.getInstance().getTmpRegisterItem().gender = gender;

        getFragmentActivity().replaceFragmentWithBackstack(new RegisterSecondFragment());
    }

    private boolean areFieldsValid(String firstname, String lastname, String gender, String schoolname, String schoolemail, String phone) {

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


        return valid;
    }

    private void checkIfFromFacebook(Bundle arguments) {
        try {
            String fbToken = arguments.getString(ARG_FB_TOKEN);
            if (fbToken != null) {
                AppStore.getInstance().getTmpRegisterItem().facebookToken = fbToken;
                inputlayoutFirstName.setHint("Name (From Facebook)");
            }
            String[] fbName = arguments.getString(ARG_FB_NAME, "").split(" ");

            fieldFirstName.setText(fbName[0]);
            fieldLastName.setText(fbName[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasData() {
        UserItem item = AppStore.getInstance().getTmpRegisterItem();

        String firstname = fieldFirstName.getText().toString().trim();
        String lastname = fieldLastName.getText().toString().trim();
        String schoolname = fieldSchoolname.getText().toString().trim();
        String schoolemail = fieldSchoolemail.getText().toString().trim();
        String phone = fieldPhone.getText().toString().trim();
        String gender = fieldGender.getText().toString().trim();
        String studentorg = fieldStudentOrg.getText().toString().trim();


        if (!TextUtils.isEmpty(firstname))
            return true;
        if (!TextUtils.isEmpty(lastname))
            return true;
        if (!TextUtils.isEmpty(schoolname))
            return true;
        if (!TextUtils.isEmpty(schoolemail))
            return true;
        if (!TextUtils.isEmpty(phone))
            return true;
        if (!TextUtils.isEmpty(gender))
            return true;
        if (!TextUtils.isEmpty(studentorg))
            return true;

        return false;
    }
}
