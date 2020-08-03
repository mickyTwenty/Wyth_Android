package com.seatus.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.seatus.Adapters.FindRideFilterableAdapter;
import com.seatus.BaseClasses.BaseLocalFiltersFragment;
import com.seatus.Dialogs.SeatUsDialog;
import com.seatus.Models.SearchFilterDataItem;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.NumberPicker;
import com.seatus.Views.PreferencesView;
import com.seatus.Views.RippleView;
import com.seatus.Views.TitleBar;
import com.seatus.enums.InviteCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by rohail on 4/9/2018.
 */

public class FindRideFragment extends BaseLocalFiltersFragment implements FindRideFilterableAdapter.OnItemClickInterface {

    @BindView(R.id.txt_categories_filter)
    TextView txtCategoriesFilter;
    @BindView(R.id.checkbox_morning)
    CheckBox checkboxMorning;
    @BindView(R.id.checkbox_afternoon)
    CheckBox checkboxAfternoon;
    @BindView(R.id.checkbox_night)
    CheckBox checkboxNight;
    @BindView(R.id.txt_allday)
    ImageView txtAllday;
    @BindView(R.id.seat_view)
    NumberPicker seatView;
    @BindView(R.id.layout_seats)
    LinearLayout layoutSeats;
    @BindView(R.id.checkbox_morning_return)
    CheckBox checkboxMorningReturn;
    @BindView(R.id.checkbox_afternoon_return)
    CheckBox checkboxAfternoonReturn;
    @BindView(R.id.checkbox_night_return)
    CheckBox checkboxNightReturn;
    @BindView(R.id.txt_allday_return)
    ImageView txtAlldayReturn;
    @BindView(R.id.seat_view_return)
    NumberPicker seatViewReturn;
    @BindView(R.id.layout_seats_return)
    LinearLayout layoutSeatsReturn;
    @BindView(R.id.layout_return)
    LinearLayout layoutReturn;
    @BindView(R.id.checkbox_bookNow)
    CheckBox checkboxBookNow;
    @BindView(R.id.spinner_gender)
    Spinner spinnerGender;
    @BindView(R.id.view_preference)
    PreferencesView viewPreference;
    @BindView(R.id.layout_advancedprefs)
    LinearLayout layoutAdvancedprefs;
    @BindView(R.id.btn_add_driver)
    TextView btnAddDriver;
    @BindView(R.id.btn_add_frnd)
    TextView btnAddFrnd;
    @BindView(R.id.btn_continue)
    RippleView btnContinue;
    @BindView(R.id.ll_fields)
    LinearLayout llFields;
    @BindView(R.id.recyclerview_passengers)
    RecyclerView recyclerView;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    private ArrayAdapter<String> genderAdapter;


    public ArrayList<TripItem> listSearchResult;
    private FindRideFilterableAdapter mAdp;


    @Override
    protected int getLayout() {
        return R.layout.fragment_findride_new;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
//        titleBar.setTitle(getUserItem().getCurrentInterface() ? "Find Ride" : "Post Ride");
        titleBar.setTitle("Find Ride");
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        if (!isLoaded)
            getData();
    }

    @Override
    public void inits() {
        viewPreference.initData();
        poppulateSpinner();
        setUi();

        if (mAdp == null) {
            listSearchResult = new ArrayList<>();
        }
        StaticMethods.initVerticalRecycler(getContext(), true, recyclerView);
        mAdp = new FindRideFilterableAdapter(getContext(), getUserItem().getCurrentInterface(), listSearchResult, this);
        getTempItem().preferences = viewPreference.getPrefJson();
        mAdp.getFilter().filter(getGson().toJson(getTempItem()));
        recyclerView.setAdapter(mAdp);
    }


    private void setUi() {

        try {
            checkboxMorning.setOnCheckedChangeListener(null);
            checkboxAfternoon.setOnCheckedChangeListener(null);
            checkboxNight.setOnCheckedChangeListener(null);

            checkboxMorning.setChecked((getTempItem().time_range & 1) != 0);
            checkboxAfternoon.setChecked((getTempItem().time_range & 2) != 0);
            checkboxNight.setChecked((getTempItem().time_range & 4) != 0);

            if (getUserItem().getCurrentInterface()) {
               // btnAddFrnd.setVisibility(View.VISIBLE);
                getTempItem().inviteType = InviteCategory.user_search;
            } else {
               // btnAddFrnd.setVisibility(View.GONE);
                getTempItem().inviteType = InviteCategory.driver_create;
            }

            if (getTempItem().isRoundTrip == 1) {
                layoutReturn.setVisibility(View.VISIBLE);
            } else {
                layoutReturn.setVisibility(View.GONE);
            }

            checkboxMorning.setChecked((getTempItem().time_range & 1) != 0);
            checkboxAfternoon.setChecked((getTempItem().time_range & 2) != 0);
            checkboxNight.setChecked((getTempItem().time_range & 4) != 0);

            checkboxMorningReturn.setChecked((getTempItem().return_time_range & 1) != 0);
            checkboxAfternoonReturn.setChecked((getTempItem().return_time_range & 2) != 0);
            checkboxNightReturn.setChecked((getTempItem().return_time_range & 4) != 0);

            spinnerGender.setSelection(genderAdapter.getPosition(getTempItem().gender));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setEvents() {
        checkboxMorning.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                getTempItem().time_range = getTempItem().time_range | 1;
            else {
                if ((getTempItem().time_range & 1) != 0)
                    getTempItem().time_range -= 1;
            }
            updateTempItem();
        });
        checkboxAfternoon.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                getTempItem().time_range = getTempItem().time_range | 2;
            else {
                if ((getTempItem().time_range & 2) != 0)
                    getTempItem().time_range -= 2;
            }
            updateTempItem();
        });

        checkboxNight.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                getTempItem().time_range = getTempItem().time_range | 4;
            else {
                if ((getTempItem().time_range & 4) != 0)
                    getTempItem().time_range -= 4;
            }
            updateTempItem();
        });


        checkboxMorningReturn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                getTempItem().return_time_range = getTempItem().return_time_range | 1;
            else {
                if ((getTempItem().return_time_range & 1) != 0)
                    getTempItem().return_time_range -= 1;
            }
            updateTempItem();
        });
        checkboxAfternoonReturn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                getTempItem().return_time_range = getTempItem().return_time_range | 2;
            else {
                if ((getTempItem().return_time_range & 2) != 0)
                    getTempItem().return_time_range -= 2;
            }
            updateTempItem();
        });

        checkboxNightReturn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                getTempItem().return_time_range = getTempItem().return_time_range | 4;
            else {
                if ((getTempItem().return_time_range & 4) != 0)
                    getTempItem().return_time_range -= 4;
            }
            updateTempItem();
        });

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (getTempItem().gender == null || !getTempItem().gender.equals(genderAdapter.getItem(i))) {
                    getTempItem().gender = genderAdapter.getItem(i);
                    updateTempItem();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void subscribeRide(SeatUsDialog dialog){
        SearchFilterDataItem dataItem = getTempItem();
        LatLng origin = StaticMethods.parseLatLng(dataItem.origin_geo);
        LatLng destination = StaticMethods.parseLatLng(dataItem.destination_geo);

        getActivityViewModel().subscribeRide(dataItem.isRoundTrip, origin.latitude,origin.longitude,dataItem.origin_text,destination.latitude,destination.longitude,dataItem.destination_text).observe(this,webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    dialog.startProgress();
                    break;
                case success:
                    dialog.stopProgress();
                    dialog.dismiss();
                    getFragmentActivity().actionBack();
                    break;
                case error:
                    dialog.stopProgress();
                    dialog.setError(webResponseResource.data.message);
                    break;
                default:
                    dialog.stopProgress();
                    dialog.setError("Connection Timeout!");
                    break;
            }
        });
    }

    private void getData() {

        SearchFilterDataItem dataItem = getTempItem();
        Map<String, Object> paramsMap = new HashMap<>();

        //region Gathering Params
        LatLng origin, destination;
        origin = StaticMethods.parseLatLng(dataItem.origin_geo);
        destination = StaticMethods.parseLatLng(dataItem.destination_geo);
        paramsMap.put("_token", getUserItem().token);
        paramsMap.put("origin_latitude", origin.latitude);
        paramsMap.put("origin_longitude", origin.longitude);
        paramsMap.put("origin_title", dataItem.origin_text);
        paramsMap.put("destination_latitude", destination.latitude);
        paramsMap.put("destination_longitude", destination.longitude);
        paramsMap.put("destination_title", dataItem.destination_text);
//        paramsMap.put("expected_start_date", StaticMethods.convertTimeToUTC(dataItem.getDate()));
        paramsMap.put("expected_start_date", dataItem.getDate().getTime());
        paramsMap.put("is_roundtrip", dataItem.isRoundTrip);

        if (dataItem.isRoundTrip == 1)
            paramsMap.put("date_returning", dataItem.getReturnDate().getTime());


        // endregion
        getActivityViewModel().findRide(paramsMap).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    if (webResponseResource.data.body.size() > 0) {
                        listSearchResult.clear();
                        listSearchResult.addAll(webResponseResource.data.body);
                        getTempItem().preferences = viewPreference.getPrefJson();
                        mAdp.getFilter().filter(getGson().toJson(getTempItem()));
                        mAdp.notifyChange(listSearchResult);
                    } else {
                        llFields.setVisibility(View.VISIBLE);
                        txtCategoriesFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.collapse_active_arrow, 0);

                        new SeatUsDialog.Builder(getContext(), R.style.BounceDialog)
                                .setHeaderImg(R.drawable.dialog_header_noride)
                                .setTitleImg(R.drawable.dialog_title_noride)
                                .setMessage(webResponseResource.data.message)
                                .setPositiveButton("Yes", dialog -> {
                                    subscribeRide(dialog);
                                })
                                .setNegativeButton("No", dialog -> {
                                    getFragmentActivity().actionBack();
                                    dialog.dismiss();
                                })
                                .show();
                    }
                    hideLoader();
                    break;
                default:
                    hideLoader();
                    makeConnectionSnackbar();
                    break;
            }
        });
    }

    private void poppulateSpinner() {

        spinnerGender.setOnItemSelectedListener(null);
        genderAdapter = new ArrayAdapter<>(getContext(), R.layout.item_text_dark);
        genderAdapter.add("Both");
        genderAdapter.add("Male");
        genderAdapter.add("Female");
        spinnerGender.setAdapter(genderAdapter);

    }


    @OnClick({R.id.txt_categories_filter, R.id.txt_allday, R.id.txt_allday_return, R.id.btn_add_frnd, R.id.btn_continue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_categories_filter:
                if (llFields.isShown()) {
                    llFields.setVisibility(View.GONE);
                    txtCategoriesFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.collapse_inactive_arrow, 0);
                } else {
                    llFields.setVisibility(View.VISIBLE);
                    txtCategoriesFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.collapse_active_arrow, 0);
                }
                break;
            case R.id.txt_allday:
                checkboxMorning.setChecked(true);
                checkboxAfternoon.setChecked(true);
                checkboxNight.setChecked(true);
                break;
            case R.id.txt_allday_return:
                checkboxMorningReturn.setChecked(true);
                checkboxAfternoonReturn.setChecked(true);
                checkboxNightReturn.setChecked(true);
                break;
            case R.id.btn_add_frnd:
                if (areFieldsValid(false)) {
                    getFragmentActivity().replaceFragmentWithBackstack(InviteFriendFragment.newInstance(getTempItem()));
                }
                break;
            case R.id.btn_continue:
                getTempItem().preferences = viewPreference.getPrefJson();
                mAdp.getFilter().filter(getGson().toJson(getTempItem()));
                llFields.setVisibility(View.GONE);
                txtCategoriesFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.collapse_inactive_arrow, 0);
                break;
        }
    }

    @Override
    public void onTripItemClickListener(TripItem tripItem) {
        showLoader();
        StaticAppMethods.fetchValidInvites(getContext(), getTempItem(), (status, documents) -> {
            switch (status) {
                case connectionError:
                    makeConnectionSnackbar();
                    hideLoader();
                    break;
                case noInvites:
                    hideLoader();
                    actionFilterSeats(tripItem, null);
                    break;
                case expiredContinue:
                    hideLoader();
                    actionFilterSeats(tripItem, null);
                    break;
                case expiredAbort:
                    hideLoader();
                    break;
                case invitesPending:
                    hideLoader();
                    DialogHelper.showAlertDialog(getContext(), getString(R.string.error_pending_invites));
                    break;
                case invitesAvailaible:
                    hideLoader();
                    actionFilterSeats(tripItem, documents);
                    break;
            }
        });
    }

    private void actionFilterSeats(TripItem tripItem, List<UserItem> documents) {
        if (documents == null)
            actionSearch(tripItem);
        else {
            boolean hasSeats = true;
            for (TripItem ride : tripItem.rides) {
                if (ride.seats_available < (documents.size() + 1))
                    hasSeats = false;
            }
            if (hasSeats)
                actionSearch(tripItem);
            else {
                DialogHelper.showAlertDialog(getContext(), "This Ride does'nt have enough Seats for all of your invited friends");
            }
        }
    }

    private void actionSearch(TripItem tripItem) {
        if (getTempItem().isRoundTrip == 1 && !TextUtils.isEmpty(tripItem.group_id))
            getTempItem().isSingleRoundTrip = true;
        else
            getTempItem().isSingleRoundTrip = false;
        getFragmentActivity().replaceFragmentWithBackstack(RideDetailFragment.newInstance(tripItem.trip_id, getTempItem()));
    }
}
