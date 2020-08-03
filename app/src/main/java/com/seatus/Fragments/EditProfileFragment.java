package com.seatus.Fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.seatus.BaseClasses.ImageChooserFragment;
import com.seatus.Holders.DocumentItemHolder;
import com.seatus.Models.CityItem;
import com.seatus.Models.DocumentItem;
import com.seatus.Models.SchoolItem;
import com.seatus.Models.StateItem;
import com.seatus.Models.UserItem;
import com.seatus.Models.VehicleMake;
import com.seatus.R;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.DateTimeHelper;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.database.AppDatabase;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;

/**
 * Created by saqib on 10/26/2017.
 */

public class EditProfileFragment extends ImageChooserFragment {

    @BindView(R.id.circleImageView)
    CircleImageView circleImageView;
    @BindView(R.id.field_dob)
    TextInputEditText fieldDob;
    @BindView(R.id.inputlayout_dob)
    TextInputLayout inputlayoutDob;
    @BindView(R.id.field_gender)
    TextInputEditText fieldGender;
    @BindView(R.id.inputlayout_gender)
    TextInputLayout inputlayoutGender;
    @BindView(R.id.field_phoneNo)
    TextInputEditText fieldPhoneNo;
    @BindView(R.id.inputlayout_phoneNo)
    TextInputLayout inputlayoutPhoneNo;
    @BindView(R.id.field_state)
    TextInputEditText fieldState;
    @BindView(R.id.inputlayout_state)
    TextInputLayout inputlayoutState;
    @BindView(R.id.field_city)
    TextInputEditText fieldCity;
    @BindView(R.id.inputlayout_city)
    TextInputLayout inputlayoutCity;
    @BindView(R.id.field_zip)
    TextInputEditText fieldZip;
    @BindView(R.id.inputlayout_zip)
    TextInputLayout inputlayoutZip;
    @BindView(R.id.field_schoolName)
    TextInputEditText fieldSchoolName;
    @BindView(R.id.inputlayout_schoolName)
    TextInputLayout inputlayoutSchoolName;
    @BindView(R.id.field_schoolEmail)
    TextInputEditText fieldSchoolEmail;
    @BindView(R.id.inputlayout_schoolEmail)
    TextInputLayout inputlayoutSchoolEmail;
    @BindView(R.id.field_studentOrg)
    TextInputEditText fieldStudentOrg;
    @BindView(R.id.inputlayout_studentOrg)
    TextInputLayout inputlayoutStudentOrg;
    @BindView(R.id.field_yearofGrad)
    TextInputEditText fieldYearofGrad;
    @BindView(R.id.inputlayout_yearofGrad)
    TextInputLayout inputlayoutYearofGrad;
    @BindView(R.id.field_drivingLicenceNo)
    TextInputEditText fieldDrivingLicenceNo;
    @BindView(R.id.inputlayout_drivingLicenceNo)
    TextInputLayout inputlayoutDrivingLicenceNo;
    @BindView(R.id.field_vehicleType)
    TextInputEditText fieldVehicleType;
    @BindView(R.id.inputlayout_vehicleType)
    TextInputLayout inputlayoutVehicleType;
    @BindView(R.id.driverFieldsLayout)
    LinearLayout driverFieldsLayout;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.txt_user_name)
    TextView txtUserName;

    @BindView(R.id.field_vehicleno)
    TextInputEditText fieldVehicleno;
    @BindView(R.id.inputlayout_vehicleno)
    TextInputLayout inputlayoutVehicleno;
    @BindView(R.id.field_make)
    TextInputEditText fieldMake;
    @BindView(R.id.inputlayout_make)
    TextInputLayout inputlayoutMake;
    @BindView(R.id.field_model)
    TextInputEditText fieldModel;
    @BindView(R.id.inputlayout_model)
    TextInputLayout inputlayoutModel;
    @BindView(R.id.field_year)
    TextInputEditText fieldYear;
    @BindView(R.id.inputlayout_year)
    TextInputLayout inputlayoutYear;


    @BindView(R.id.field_ssn)
    TextInputEditText fieldSsn;
    @BindView(R.id.inputlayout_ssn)
    TextInputLayout inputlayoutSsn;


    StateItem selectedState;
    CityItem selectedCity;
    String selectedYearOfGrad;

    File selectedFile = null;

    private boolean pickProfile = false;
    private VehicleMake selectedMake;

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_edit_profile;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("edit profile").enableBack();
    }

    @Override
    public void inits() {


        fieldPhoneNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        getUserLiveData().observe(this, userItem -> {

            try {
                txtUserName.setText(userItem.getFull_name());
                fieldDob.setText(userItem.birth_date);
                fieldGender.setText(userItem.gender);

                fieldState.setText(userItem.state_text);
                fieldCity.setText(userItem.city_text);
                fieldZip.setText(userItem.zip);
                fieldSchoolName.setText(userItem.school_name);
                fieldStudentOrg.setText(userItem.student_organization);
                fieldYearofGrad.setText(userItem.graduation_year);

                selectedCity = new CityItem(userItem.city, userItem.city_text);
                selectedState = new StateItem(userItem.state, userItem.state_text);
                selectedYearOfGrad = userItem.graduation_year;

                if (!TextUtils.isEmpty(userItem.profile_picture)) {
                    Picasso.with(getContext()).load(userItem.profile_picture).fit().centerCrop().into(circleImageView);
                    if (!userItem.profile_picture.contains("default.jpg"))
                        circleImageView.setTag("image");
                }

                if (userItem.getUser_type() == UserItem.UserType.normal) {
                    driverFieldsLayout.setVisibility(View.GONE);
                } else {
                    driverFieldsLayout.setVisibility(View.VISIBLE);
                    fieldDrivingLicenceNo.setText(userItem.driving_license_no);

                    fieldSsn.setText(userItem.ssn);

                    fieldVehicleType.setText(userItem.vehicle_type);
                    fieldVehicleno.setText(userItem.vehicle_id_number);
                    if (!TextUtils.isEmpty(userItem.vehicle_make)) {
                        fieldMake.setText(userItem.vehicle_make);
                        ArrayList<VehicleMake> list = AppStore.getInstance().getBootUpData().make;
                        if (list == null || list.isEmpty())
                            getActivityViewModel().bootMeUp(getContext());
                        else {
                            for (VehicleMake make : list)
                                if (make.name.equals(userItem.vehicle_make))
                                    selectedMake = make;

                        }
                    }
                    fieldModel.setText(userItem.vehicle_model);
                    fieldYear.setText(userItem.vehicle_year);

                }

                fieldPhoneNo.setText(PhoneNumberUtils.formatNumber(userItem.phone));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void setEvents() {

        fieldPhoneNo.setOnEditorActionListener((v, actionId, event) -> {
            try {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    fieldState.performClick();
                    hideKeyboard();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });

        fieldZip.setOnEditorActionListener((v, actionId, event) -> {
            try {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    fieldSchoolName.performClick();
                    hideKeyboard();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });


        fieldStudentOrg.setOnEditorActionListener((v, actionId, event) -> {
            try {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    fieldYearofGrad.performClick();
                    hideKeyboard();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public void removeImage() {
        getActivityViewModel().removeProfilePic().observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    circleImageView.setImageBitmap(null);
                    circleImageView.setTag(null);
                    if (webResponseResource.data.body != null) {
                        getActivityViewModel().setUserItem(webResponseResource.data.body);
                    }
                    makeSnackbar(webResponseResource.data.message);
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    @Override
    public void onImageChosen(ChosenImage chosenImage) {
        handler.post(() -> {
            if (pickProfile) {
                Picasso.with(getContext()).load(new File(chosenImage.getFileThumbnail())).into(circleImageView);
                circleImageView.setTag("image");
                selectedFile = new File(chosenImage.getFileThumbnail());
                pickProfile = false;
            }
        });
    }


    @OnClick({R.id.field_make, R.id.field_model, R.id.field_year, R.id.circleImageView, R.id.field_dob, R.id.field_gender, R.id.field_state, R.id.field_city, R.id.field_yearofGrad, R.id.field_vehicleType, R.id.field_schoolName, R.id.btn_save_changes, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.circleImageView:
                pickProfile = true;
                if (circleImageView.getTag() != null)
                    pickAndRemoveImage();
                else pickImage();
                break;
            case R.id.field_year:
                DialogHelper.showCarYearPicker(getContext(), value -> {
                    fieldYear.setText(value + "");
                });
                break;
            case R.id.field_dob:
                StaticMethods.datePopup(getContext(), fieldDob, true, false, 2000);
                break;
            case R.id.field_gender:
                DialogHelper.showGenderDialog(getContext(), item -> fieldGender.setText(item));
                break;
            case R.id.field_state:
                try {
                    DialogHelper.showLocationPickerDialog(getContext(), AppDatabase.getInstance(getContext()).stateDoa().getAll(), item -> {
                        try {
                            selectedState = (StateItem) item;
                            fieldState.setText(selectedState.name);
                            if (selectedCity != null && selectedCity.state_id != null && !selectedCity.state_id.equals(selectedState.id)) {
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
            case R.id.field_yearofGrad:
                DialogHelper.showNumberPickerDialog(getContext(), value -> {
                    selectedYearOfGrad = String.valueOf(value);
                    fieldYearofGrad.setText(selectedYearOfGrad + "");
                });
                break;
            case R.id.field_vehicleType:
                DialogHelper.showSimpleListDialog(getContext(), AppStore.getInstance().getVehicleTypeList(), item -> fieldVehicleType.setText(item));
                break;
            case R.id.field_make:
                DialogHelper.showVehicleMakeDialog(getContext(), item -> {
                    selectedMake = item;
                    fieldMake.setText(selectedMake.name);
                });
                break;
            case R.id.field_model:
                if (selectedMake == null)
                    makeSnackbar("Please Select Vehicle Make First");
                else
                    DialogHelper.showSearchableListDialog(getContext(), selectedMake.models, item -> fieldModel.setText(item));
                break;
            case R.id.field_schoolName:
                ArrayList<SchoolItem> schools = AppStore.getInstance().getSchoolsList(getContext());
                DialogHelper.showSchoolsDialog(getContext(), schools, item -> fieldSchoolName.setText(item));
                break;
            case R.id.btn_save_changes:
                actionSaveChanges();
                break;
            case R.id.btn_cancel:
                getFragmentActivity().actionBack();
                break;
        }
    }

    private void actionSaveChanges() {
        Date dob = null;
        if (fieldDob.getTag() != null && fieldDob.getTag() instanceof Date)
            dob = (Date) fieldDob.getTag();

        String gender = fieldGender.getText().toString().trim();
        String phone = fieldPhoneNo.getText().toString().trim();
        String zip = fieldZip.getText().toString().trim();
        String school_name = fieldSchoolName.getText().toString().trim();
        String student_org = fieldStudentOrg.getText().toString().trim();
        String licence = fieldDrivingLicenceNo.getText().toString().trim();


        String selectedVehicleType = fieldVehicleType.getText().toString().trim();
        String vehicleId = fieldVehicleno.getText().toString().trim();
        String make = fieldMake.getText().toString().trim();
        String model = fieldModel.getText().toString().trim();
        String year = fieldYear.getText().toString().trim();

        String ssn = fieldSsn.getText().toString().trim();


        phone = StaticAppMethods.normalizePhone(phone);

        if (areFieldsValid(selectedCity, zip, school_name, selectedYearOfGrad, licence, selectedVehicleType, vehicleId, make, model, year)) {

            getActivityViewModel().updateProfile(
                    StaticMethods.getMultiPartBody("profile_picture", selectedFile),
                    dob == null ? null : StaticMethods.getRequestBody(DateTimeHelper.getDateToShow(dob)),
                    StaticMethods.getRequestBody(gender),
                    StaticMethods.getRequestBody(phone),
                    StaticMethods.getRequestBody(selectedState.id),
                    StaticMethods.getRequestBody(selectedCity.id),
                    StaticMethods.getRequestBody(zip),
                    StaticMethods.getRequestBody(school_name),
                    StaticMethods.getRequestBody(student_org),
                    StaticMethods.getRequestBody(selectedYearOfGrad),
                    StaticMethods.getRequestBody(licence),
                    StaticMethods.getRequestBody(selectedVehicleType),
                    StaticMethods.getRequestBody(vehicleId),
                    StaticMethods.getRequestBody(make),
                    StaticMethods.getRequestBody(model),
                    StaticMethods.getRequestBody(year)).observe(this, webResponseResource -> {

                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case success:
                        hideLoader();
                        if (webResponseResource.data.body != null) {
                            getActivityViewModel().setUserItem(webResponseResource.data.body);
                            getFragmentActivity().actionBack();
                        }
                        makeSnackbar(webResponseResource.data.message);
                        break;
                    default:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        break;
                }
            });
        }
    }

    private boolean areFieldsValid(CityItem selectedCity, String zip, String school_name, String selectedYearOfGrad, String licence, String selectedVehicleType, String vehicleid, String make, String model, String year) {

        boolean valid = true;

       /* if (selectedCity == null) {
            inputlayoutCity.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutCity.setError(null);*/

      /*  if (zip.length() < 4) {
            inputlayoutZip.setError(getString(R.string.error_validation_empty));
            valid = false;
        } else inputlayoutZip.setError(null);*/

      /*  if (TextUtils.isEmpty(selectedYearOfGrad)) {
            inputlayoutYearofGrad.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutYearofGrad.setError(null);*/

        if (getUserItem().getUser_type() == UserItem.UserType.normal)
            return valid;

//        if (licence.length() < 4) {
//            inputlayoutDrivingLicenceNo.setError(getString(R.string.error_validation_empty));
//            valid = false;
//        } else inputlayoutDrivingLicenceNo.setError(null);

//        if (ssn.length() < 9) {
//            inputlayoutSsn.setError("Please Enter Valid SSN");
//            valid = false;
//        } else inputlayoutSsn.setError(null);

        if (TextUtils.isEmpty(selectedVehicleType)) {
            inputlayoutVehicleType.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutVehicleType.setError(null);

        if (vehicleid.length() < 4) {
            inputlayoutVehicleno.setError(getString(R.string.error_validation_empty));
            valid = false;
        } else inputlayoutVehicleno.setError(null);

        if (TextUtils.isEmpty(make)) {
            inputlayoutMake.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutMake.setError(null);

        if (TextUtils.isEmpty(model)) {
            inputlayoutModel.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutModel.setError(null);

        if (TextUtils.isEmpty(year)) {
            inputlayoutYear.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutYear.setError(null);


//        if (insurance.length() < 4) {
//            inputlayoutInsuranceNo.setError(getString(R.string.error_validation_empty));
//            valid = false;
//        } else inputlayoutInsuranceNo.setError(null);
//
//        if (TextUtils.isEmpty(company_name)) {
//            inputlayoutCompanyName.setError(getString(R.string.error_validation_select));
//            valid = false;
//        } else inputlayoutCompanyName.setError(null);
//
//        if (TextUtils.isEmpty(effecitve)) {
//            inputlayoutInsuranceEffectivedate.setError(getString(R.string.error_validation_select));
//            valid = false;
//        } else inputlayoutInsuranceEffectivedate.setError(null);
//
//        if (TextUtils.isEmpty(expiry)) {
//            inputlayoutInsuranceExpirydate.setError(getString(R.string.error_validation_select));
//            valid = false;
//        } else inputlayoutInsuranceExpirydate.setError(null);

        return valid;
    }

}
