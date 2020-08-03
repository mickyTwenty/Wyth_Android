package com.seatus.Fragments;

import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.seatus.Views.RippleView;
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

public class OtherProfileFragment extends BaseFragment {

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
    @BindView(R.id.btn_friends)
    FrameLayout btnFriends;

    @BindView(R.id.btn_delete_accnt)
    RippleView btnDeleteAccount;

    @BindView(R.id.layout_buttons)
    LinearLayout layoutButtons;

    public String userId;
    @BindView(R.id.layout_phone)
    LinearLayout layoutPhone;
    @BindView(R.id.divider_phone)
    View dividerPhone;

    public static OtherProfileFragment newInstance(String user_id) {
        OtherProfileFragment fragment = new OtherProfileFragment();
        fragment.userId = user_id;
        return fragment;
    }

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
        titleBar.setTitle("profile").enableBack();
    }

    @Override
    public void inits() {
        btnFriends.setVisibility(View.GONE);
        dividerPhone.setVisibility(View.GONE);
        layoutPhone.setVisibility(View.GONE);
        layoutButtons.setVisibility(View.GONE);

    }

    @Override
    public void setEvents() {
    }

    private void refreshProfile() {
        getActivityViewModel().getProfile(userId).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    if (webResponseResource.data.body != null) {
                        try {
                            UserItem userItem = webResponseResource.data.body;
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

                            try {
                                txtRating.setRating(userItem.rating.floatValue());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (imgUser != null && !TextUtils.isEmpty(userItem.profile_picture))
                                Picasso.with(getContext()).load(userItem.profile_picture).fit().centerCrop().into(imgUser);

//                    if (userItem.getUser_type() == UserItem.UserType.normal)
                            layoutDriverFields.setVisibility(View.GONE);
//                    else {
//                        layoutDriverFields.setVisibility(View.VISIBLE);
//                        txtDrivingLicence.setText(userItem.driving_license_no);
//
//                        txtVehicleType.setText(userItem.vehicle_type);
//                        txtVehicleNo.setText(userItem.vehicle_id_number);
//                        txtVehicleModel.setText(userItem.vehicle_model);
//                        txtVehicleMake.setText(userItem.vehicle_make);
//                        txtVehicleYear.setText(userItem.vehicle_year);
//
//                        txtInsuranceCompany.setText(userItem.insurance_company);
//                        txtInsurance.setText(userItem.insurance_no);
//                        txtInsuranceEffectivedate.setText(userItem.policy_effective_date);
//                        txtInsuranceExpirydate.setText(userItem.policy_expiry_date);
//
//                        if (userItem.driver_documents != null) {
//                            txt_no_of_docs.setText(userItem.driver_documents.size() + "");
//                            if (userItem.driver_documents.size() > 0)
//                                btnViewDocs.setOnClickListener(view1 -> {
//                                    getFragmentActivity().replaceFragmentWithBackstack(GalleryFragment.newInstance(userItem.driver_documents));
//                                });
//                            else
//                                btnViewDocs.setOnClickListener(null);
//                        }
//                    }
                            txtPhone.setText(PhoneNumberUtils.formatNumber(userItem.phone));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }
}
