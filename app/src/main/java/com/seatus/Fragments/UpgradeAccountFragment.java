package com.seatus.Fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.seatus.BaseClasses.ImageChooserFragment;
import com.seatus.Models.DocumentItem;
import com.seatus.Models.UserItem;
import com.seatus.Models.VehicleMake;
import com.seatus.R;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MultipartBody;

/**
 * Created by saqib on 10/26/2017.
 */

public class UpgradeAccountFragment extends ImageChooserFragment {

    @BindView(R.id.field_drivingLicenceNo)
    TextInputEditText fieldDrivingLicenceNo;
    @BindView(R.id.inputlayout_drivingLicenceNo)
    TextInputLayout inputlayoutDrivingLicenceNo;
    @BindView(R.id.field_vehicleType)
    TextInputEditText fieldVehicleType;
    @BindView(R.id.inputlayout_vehicleType)
    TextInputLayout inputlayoutVehicleType;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    EfficientRecyclerAdapter mAdp;
    ArrayList<DocumentItem> mList = new ArrayList<>();
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

    private VehicleMake selectedMake;

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_upgrade_profile;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("become a driver").enableBack();
    }

    @Override
    public void inits() {


    }


    @Override
    public void setEvents() {

    }

    @Override
    public void removeImage() {

    }

    @Override
    public void onImageChosen(ChosenImage chosenImage) {
    }


    @OnClick({R.id.field_make, R.id.field_model, R.id.field_year, R.id.field_vehicleType, R.id.btn_save_changes, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.field_year:
                DialogHelper.showCarYearPicker(getContext(), value -> {
                    fieldYear.setText(value + "");
                });
                break;
            case R.id.field_vehicleType:
                DialogHelper.showSimpleListDialog(getContext(), AppStore.getInstance().getVehicleTypeList(), item -> fieldVehicleType.setText(item));
                break;
            case R.id.btn_save_changes:
                actionSaveChanges();
                break;
            case R.id.btn_cancel:
                getFragmentActivity().actionBack();
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
        }
    }

    private void actionSaveChanges() {
        Date dob = null;
        String licence = fieldDrivingLicenceNo.getText().toString().trim();
        String selectedVehicleType = fieldVehicleType.getText().toString().trim();

        String vehicleId = fieldVehicleno.getText().toString().trim();
        String make = fieldMake.getText().toString().trim();
        String model = fieldModel.getText().toString().trim();
        String year = fieldYear.getText().toString().trim();


        ArrayList<MultipartBody.Part> documentFiles = getDocumentsFiles();

        if (areFieldsValid(licence, selectedVehicleType, vehicleId, make, model, year)) {


            DialogHelper.showTermsDialog(getContext(), UserItem.UserType.driver, (dialog, which) -> {

                getActivityViewModel().upgradeToDriver(
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
                            UserItem item = getUserItem();
                            item.user_type = "driver";
                            getActivityViewModel().setUserItem(item);
                            DialogHelper.showAlertDialog(getContext(), webResponseResource.data.message);
                            getFragmentActivity().actionBack();
                            break;
                        default:
                            hideLoader();
                            makeSnackbar(webResponseResource.data);
                            break;
                    }
                });
            });
        }
    }


    private boolean areFieldsValid(String licence, String selectedVehicleType, String vehicleid, String make, String model, String year) {

        boolean valid = true;

//        if (licence.length() < 4) {
//            inputlayoutDrivingLicenceNo.setError(getString(R.string.error_validation_empty));
//            valid = false;
//        } else inputlayoutDrivingLicenceNo.setError(null);

        if (TextUtils.isEmpty(selectedVehicleType)) {
            inputlayoutVehicleType.setError(getString(R.string.error_validation_select));
            valid = false;
        } else inputlayoutVehicleType.setError(null);

        if (vehicleid.length() < 4) {
            inputlayoutVehicleno.setError(getString(R.string.error_validation_select));
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

    public String getDocumentsJson() {
        ArrayList<DocumentItem> list = new ArrayList<>();
        for (DocumentItem item : mList)
            if (!item.isLocal)
                list.add(item);
        return getGson().toJson(list);
    }

    public ArrayList<MultipartBody.Part> getDocumentsFiles() {
        ArrayList<File> list = new ArrayList<>();
        for (DocumentItem item : mList)
            if (item.isLocal)
                list.add(new File(item.absolute_url));
        return StaticMethods.getMultiPartBodyList("driver_documents", list);

    }

}
