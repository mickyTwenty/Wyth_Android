package com.seatus.Fragments;

import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.Activities.MainActivity;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Utils.DialogHelper;
import com.seatus.ViewModels.Resource;
import com.seatus.Views.TitleBar;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rohail on 24-Oct-17.
 */

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.img_user)
    ImageView imgUser;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_rating)
    AppCompatRatingBar txtRating;
    @BindView(R.id.txt_friends_badge)
    TextView txtFriendsBadge;
    @BindView(R.id.txt_dateofbirth)
    TextView txtDateofbirth;
    @BindView(R.id.txt_gender)
    TextView txtGender;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.txt_state)
    TextView txtState;
    @BindView(R.id.txt_city)
    TextView txtCity;
    @BindView(R.id.txt_zip)
    TextView txtZip;
    @BindView(R.id.txt_schoolname)
    TextView txtSchoolname;
    @BindView(R.id.txt_email)
    TextView txtEmail;
    @BindView(R.id.txt_yearofgrad)
    TextView txtYearofgrad;
    @BindView(R.id.txt_studentorg)
    TextView txtStudentOrg;
    @BindView(R.id.txt_driving_licence)
    TextView txtDrivingLicence;
    @BindView(R.id.txt_vehicle_type)
    TextView txtVehicleType;
    @BindView(R.id.txt_insurance)
    TextView txtInsurance;
    @BindView(R.id.btn_view_docs)
    LinearLayout btnViewDocs;
    @BindView(R.id.txt_badge_docs)
    TextView txt_no_of_docs;
    @BindView(R.id.layout_driver_fields)
    LinearLayout layoutDriverFields;
    @BindView(R.id.txt_vehicle_no)
    TextView txtVehicleNo;
    @BindView(R.id.txt_vehicle_make)
    TextView txtVehicleMake;
    @BindView(R.id.txt_vehicle_model)
    TextView txtVehicleModel;
    @BindView(R.id.txt_vehicle_year)
    TextView txtVehicleYear;
    @BindView(R.id.txt_insurance_company)
    TextView txtInsuranceCompany;
    @BindView(R.id.txt_insurance_effectivedate)
    TextView txtInsuranceEffectivedate;
    @BindView(R.id.txt_insurance_expirydate)
    TextView txtInsuranceExpirydate;


    @BindView(R.id.txt_canceled_passenger)
    TextView txtCanceledPassenger;
    @BindView(R.id.txt_canceled_driver)
    TextView txtCanceledDriver;

    @BindView(R.id.txt_ssn)
    TextView txtSsn;

    @BindView(R.id.divider_canceled_driver)
    View dividerCanceled;
    @BindView(R.id.layout_canceled_driver)
    LinearLayout layoutCanceledDriver;

    @BindView(R.id.layout_ssn)
    LinearLayout layoutSsn;


    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        refreshProfile();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_userprofile;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("manage profile").enableMenu().enableRightButton(R.drawable.actionbar_edit, v -> {
            getFragmentActivity().replaceFragmentWithBackstack(new EditProfileFragment());
        });
    }

    @Override
    public void inits() {
        getUserLiveData().observe(this, userItem -> {
            try {
                txtName.setText(userItem.getFull_name());
                txtDateofbirth.setText(userItem.birth_date);
                txtGender.setText(userItem.gender);
                txtState.setText(userItem.state_text);
                txtCity.setText(userItem.city_text);
                txtZip.setText(userItem.zip);
                txtSchoolname.setText(userItem.school_name);
                txtEmail.setText(userItem.email);
                txtYearofgrad.setText(userItem.graduation_year);
                txtStudentOrg.setText(userItem.student_organization);
                txtFriendsBadge.setText(userItem.follower_count != null ? userItem.follower_count.toString() : "0");

                txtCanceledPassenger.setText(userItem.trips_canceled);

                try {
                    txtRating.setRating(userItem.rating.floatValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (imgUser != null && !TextUtils.isEmpty(userItem.profile_picture))
                    Picasso.with(getContext()).load(userItem.profile_picture).fit().centerCrop().into(imgUser);

                if (userItem.getUser_type() == UserItem.UserType.normal)
                    layoutDriverFields.setVisibility(View.GONE);
                else {

                    txtCanceledDriver.setText(userItem.trips_canceled_driver);

                    txtSsn.setText(userItem.ssn);

                    layoutDriverFields.setVisibility(View.VISIBLE);
                    txtDrivingLicence.setText(userItem.driving_license_no);

                    txtVehicleType.setText(userItem.vehicle_type);
                    txtVehicleNo.setText(userItem.vehicle_id_number);
                    txtVehicleModel.setText(userItem.vehicle_model);
                    txtVehicleMake.setText(userItem.vehicle_make);
                    txtVehicleYear.setText(userItem.vehicle_year);


                }
                txtPhone.setText(PhoneNumberUtils.formatNumber(userItem.phone));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setEvents() {
    }

    @OnClick({R.id.btn_friends, R.id.btn_delete_accnt, R.id.btn_changepass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_friends:
                getFragmentActivity().replaceFragmentWithBackstack(new FriendsFragment());
                break;
            case R.id.btn_delete_accnt:
                DialogHelper.showAlertDialog(getContext(), "Are you sure you want to delete your Account permanently?", (dialog, which) -> {
                    showLoader();
                    WebServiceFactory.getInstance().logout(getUserItem().token).enqueue(new Callback<WebResponse>() {
                        @Override
                        public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                            hideLoader();
                            dialog.dismiss();
                            ((MainActivity) getContext()).actionLogout();
                        }

                        @Override
                        public void onFailure(Call<WebResponse> call, Throwable t) {
                            hideLoader();
                            dialog.dismiss();
                            makeConnectionSnackbar();
                        }
                    });
                });
                break;
            case R.id.btn_changepass:
                DialogHelper.showChangePasswordDialog(getContext(), (dialog, oldPass, newPass) -> {
                    getActivityViewModel().changeProfile(oldPass, newPass).observe(this, webResponseResource -> {
                        switch (webResponseResource.status) {
                            case loading:
                                dialog.startProgress();
                                break;
                            default:
                                dialog.stopProgress();
                                makeSnackbar(webResponseResource.data);
                                dialog.dismiss();
                                break;
                        }
                    });
                });
                break;
        }
    }

    private void refreshProfile() {
        getActivityViewModel().getProfile().observe(this, webResponseResource -> {
            if (webResponseResource.status == Resource.Status.success && webResponseResource.data.body != null)
                getActivityViewModel().setUserItem(webResponseResource.data.body);
        });
    }
}
