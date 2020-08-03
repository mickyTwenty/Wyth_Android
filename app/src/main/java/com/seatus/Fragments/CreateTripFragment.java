package com.seatus.Fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.seatus.Adapters.FriendsAdapter;
import com.seatus.BaseClasses.BaseLocalFiltersFragment;
import com.seatus.Fragments.Driver.ChooseRouteFragment;
import com.seatus.Fragments.Passenger.MyTripsFragment;
import com.seatus.Models.FireStoreUserDocument;
import com.seatus.Models.SearchFilterDataItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.Help;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.database.AppDatabase;
import com.seatus.Views.NumberPicker;
import com.seatus.Views.PreferencesView;
import com.seatus.Views.TitleBar;
import com.seatus.enums.InviteCategory;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rohail on 3/22/2018.
 */

public class CreateTripFragment extends BaseLocalFiltersFragment {

    @BindView(R.id.checkbox_morning)
    CheckBox checkboxMorning;
    @BindView(R.id.checkbox_afternoon)
    CheckBox checkboxAfternoon;
    @BindView(R.id.checkbox_night)
    CheckBox checkboxNight;
    @BindView(R.id.txt_allday)
    ImageView txtAllday;

    @BindView(R.id.layout_return)
    LinearLayout layoutReturn;
    @BindView(R.id.checkbox_morning_return)
    CheckBox checkboxMorningReturn;
    @BindView(R.id.checkbox_afternoon_return)
    CheckBox checkboxAfternoonReturn;
    @BindView(R.id.checkbox_night_return)
    CheckBox checkboxNightReturn;
    @BindView(R.id.txt_allday_return)
    ImageView txtAlldayReturn;
    @BindView(R.id.checkbox_bookNow)
    CheckBox checkboxBookNow;
    @BindView(R.id.icon_advancedprefs)
    ImageView iconAdvancedprefs;
    @BindView(R.id.btn_advancedprefs)
    LinearLayout btnAdvancedprefs;
    @BindView(R.id.spinner_gender)
    Spinner spinnerGender;
    @BindView(R.id.view_preference)
    PreferencesView viewPreference;
    @BindView(R.id.layout_advancedprefs)
    LinearLayout layoutAdvancedprefs;

    @BindView(R.id.layout_multi_trip)
    LinearLayout layoutMultiTrip;
    @BindView(R.id.switch_multi_trip)
    CheckBox switchMultiTrip;
    @BindView(R.id.layout_multi_trip_count)
    LinearLayout layoutMultiTripCount;
    @BindView(R.id.multi_trip_count)
    NumberPicker multiTripCount;
    @BindView(R.id.layout_return_trip)
    LinearLayout layoutReturnTrip;
    @BindView(R.id.switch_return_trip)
    CheckBox switchReturnTrip;

    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.btn_add_driver)
    TextView btnAddDriver;
    @BindView(R.id.btn_add_frnd)
    TextView btnAddFrnd;
    @BindView(R.id.searchView)
    AppCompatAutoCompleteTextView searchView;
    @BindView(R.id.driver_img)
    CircleImageView driverImg;
    @BindView(R.id.driver_name)
    TextView driverName;
    @BindView(R.id.driver_seats)
    TextView driverSeats;
    @BindView(R.id.driver_seats_return)
    TextView driverSeatsReturning;
    @BindView(R.id.driver_status)
    TextView driverStatus;
    @BindView(R.id.layout_driver)
    LinearLayout layoutDriver;
    @BindView(R.id.btn_continue)
    TextView btnContinue;

    @BindView(R.id.seat_view)
    NumberPicker seatView;
    @BindView(R.id.seat_view_return)
    NumberPicker seatViewReturn;

    @BindView(R.id.layout_seats)
    LinearLayout layoutSeats;
    @BindView(R.id.layout_seats_return)
    LinearLayout layoutSeatsReturn;

    FireStoreUserDocument userDocument;
    ListenerRegistration mListener;

    private ArrayAdapter<String> genderAdapter;

    @Override
    protected int getLayout() {
        return R.layout.fragment_create_new;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.resetTitleBar().enableBack().setTitle(getUserItem().getCurrentInterface() ? "Create Ride" : "Post Ride").enableInfo(Help.CreateRide);
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        listenDocument();
    }

    @Override
    public void inits() {
        viewPreference.initData();
        poppulateSpinner();
        searchView.setAdapter(new FriendsAdapter(getContext(), AppDatabase.getInstance(getContext()).friendsDao().getAllDrivers()));
        setUi();

    }

    private void listenDocument() {
        mListener = FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).addSnapshotListener((documentSnapshot, e) -> {
            try {
                if (e != null)
                    return;
                userDocument = documentSnapshot.toObject(FireStoreUserDocument.class);

                if (userDocument.isExpired()) {
                    StaticAppMethods.deleteRequestData(null);
                }
                setDriverUI();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        try {
            mListener.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void setDriverUI() {

        if (userDocument != null && userDocument.invited_driver != null) {
            UserItem invited_driver = userDocument.invited_driver;

            layoutDriver.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);

            driverName.setText(invited_driver.getFull_name());
            if (invited_driver.seats == null || invited_driver.seats == 0)
                driverSeats.setText("");
            else
                driverSeats.setText(String.format("No of Seats: %d", invited_driver.seats));

            if (invited_driver.seats_returning == null || invited_driver.seats_returning == 0 || getTempItem().isRoundTrip == 0)
                driverSeatsReturning.setVisibility(View.GONE);
            else {
                driverSeatsReturning.setVisibility(View.VISIBLE);
                driverSeatsReturning.setText(String.format("Returning Seats: %d", invited_driver.seats_returning));
            }


            if (invited_driver.status == null) {
                driverStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
                driverStatus.setText("Pending");
            } else
                switch (invited_driver.status) {
                    case 0:
                        driverStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
                        driverStatus.setText("Pending");
                        break;
                    case 1:
                        driverStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                        driverStatus.setText("Accepted");
                        break;
                    case -1:
                        driverStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                        driverStatus.setText("Rejected");
                        break;
                }

            try {
                Picasso.with(getContext()).load(invited_driver.profile_picture).fit().centerCrop().into(driverImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            layoutDriver.setVisibility(View.GONE);
    }

    private void setUi() {

        if (getUserItem().getCurrentInterface()) {
            btnAddDriver.setVisibility(View.VISIBLE);
            getTempItem().inviteType = InviteCategory.user_search;
            layoutSeats.setVisibility(View.GONE);
            layoutSeats.setVisibility(View.GONE);
            layoutSeatsReturn.setVisibility(View.GONE);
            layoutMultiTrip.setVisibility(View.GONE);
            btnContinue.setText("Create Ride");
        } else {
            btnAddDriver.setVisibility(View.GONE);
            getTempItem().inviteType = InviteCategory.driver_create;
            layoutSeats.setVisibility(View.VISIBLE);
            layoutSeatsReturn.setVisibility(View.VISIBLE);
            layoutMultiTrip.setVisibility(View.VISIBLE);
            btnContinue.setText("Post Ride");
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

        seatView.setNumber(getTempItem().seats);
        seatViewReturn.setNumber(getTempItem().return_seats);

        switchMultiTrip.setChecked(false);
        multiTripCount.setNumber(1);
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

        switchMultiTrip.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                multiTripCount.setNumber(1);
                layoutMultiTripCount.setVisibility(View.VISIBLE);
            }
            else {
                multiTripCount.setNumber(1);
                layoutMultiTripCount.setVisibility(View.GONE);
            }
        });

        switchReturnTrip.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                getTempItem().book_now = 1;
            }
            else {
                getTempItem().book_now = 1;
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

        seatView.getObservable().observe(this, integer -> {
            if (integer != 0) {
                getTempItem().seats = integer;
                updateTempItem();
            }
        });
        seatViewReturn.getObservable().observe(this, integer -> {
            if (integer != 0) {
                getTempItem().return_seats = integer;
                updateTempItem();
            }

        });
        multiTripCount.getObservable().observe(this, integer -> {
            if (integer != 0) {
                getTempItem().trip_count = integer;
                updateTempItem();
            }

        });

        layoutDriver.setOnLongClickListener(v -> {
            new AlertDialog.Builder(getContext()).setMessage("Do you want to cancel this invitation?").setPositiveButton("Yes", (dialog, which) -> {
                FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).update("invited_driver", FieldValue.delete());
            }).setNegativeButton("No", null).show();
            return true;
        });


        searchView.setOnClickListener(v -> searchView.showDropDown());

        searchView.setOnItemClickListener((adapterView, view, i, l) -> {
            searchView.setText("");
            hideKeyboard();

            if (userDocument == null) {
                hideLoader();
                makeConnectionSnackbar();
                return;
            }

            hideLoader();

            UserItem item = (UserItem) adapterView.getAdapter().getItem(i);
            searchView.setText("");

            Map<String, Object> map = new HashMap<>();
            map.put("trip_search_data", getTempItem().toMap(getUserItem().getFull_name(), getUserItem().user_id));
            map.put("last_req_edited", FieldValue.serverTimestamp());
            map.put("invite_type", getTempItem().inviteType.name());
            map.put("invited_driver", item.toMap());


            FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).update(map);
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


    @OnClick({R.id.txt_allday, R.id.txt_allday_return, R.id.btn_advancedprefs, R.id.btn_add_driver, R.id.btn_add_frnd, R.id.btn_continue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
            case R.id.btn_advancedprefs:
//                if (layoutAdvancedprefs.isShown()) {
//                    layoutAdvancedprefs.setVisibility(View.GONE);
//                    iconAdvancedprefs.setImageResource(R.drawable.icon_collapse_inactive);
//                } else {
//                    layoutAdvancedprefs.setVisibility(View.VISIBLE);
//                    iconAdvancedprefs.setImageResource(R.drawable.icon_collapse_active);
//                }
                break;
            case R.id.btn_add_driver:
                if (userDocument == null) {
                    makeSnackbar(getString(R.string.error_connection));
                    return;
                }
                if (areFieldsValid(false)) {
                    if (userDocument.invited_driver != null) {
                        if (userDocument.isExpired()) {
                            FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).update("invited_driver", FieldValue.delete()).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    searchView.setVisibility(View.VISIBLE);
                                    layoutDriver.setVisibility(View.GONE);

                                    searchView.showDropDown();
                                    handler.postDelayed(() -> searchView.showDropDown(), 700);
                                }
                            });
                        } else
                            makeSnackbar("Please remove the current driver to invite another");
                    } else {
                        searchView.setVisibility(View.VISIBLE);
                        layoutDriver.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.btn_add_frnd:
                if (areFieldsValid(false)) {
                    getFragmentActivity().replaceFragmentWithBackstack(InviteFriendFragment.newInstance(getTempItem()));
                }
                break;
            case R.id.btn_continue:
                if (getUserItem().getCurrentInterface()) {
                    if (areFieldsValid(false)) {
                        getTempItem().preferences = viewPreference.getPrefJson();
                        updateTempItem();
                        actionCreatePassenger(getTempItem());
                    }
                } else {
                    if (areFieldsValid(true)) {
                        getTempItem().preferences = viewPreference.getPrefJson();
                        updateTempItem();
                        getFragmentActivity().replaceFragmentWithBackstack(ChooseRouteFragment.newInstance(getTempItem()));
                    }
                }
                break;
        }
    }


    private void actionCreatePassenger(SearchFilterDataItem dataItem) {
        showLoader();
        StaticAppMethods.fetchValidInvitesAndDriver(getContext(), dataItem, (status, invited_members, invited_driver) -> {
            switch (status) {
                case connectionError:
                    makeConnectionSnackbar();
                    hideLoader();
                    break;
                case noInvites:
                    hideLoader();
                    createRideForPassenger(dataItem, null, null);
                    break;
                case expiredContinue:
                    hideLoader();
                    createRideForPassenger(dataItem, null, null);
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
                    createRideForPassenger(dataItem, invited_members, invited_driver);
                    break;
            }
        });
    }

    private void createRideForPassenger(SearchFilterDataItem dataItem, List<UserItem> members, UserItem driver) {

        if (driver != null && members != null) {
            if ((members.size() + 1) > driver.seats) {
                DialogHelper.showAlertDialog(getContext(), "The Driver you invited does not have enough seats for all the invited Passengers");
                return;
            }
        }

        //region Gathering Params
        Map<String, Object> map = new HashMap<>();
        LatLng origin, destination;
        origin = StaticMethods.parseLatLng(dataItem.origin_geo);
        destination = StaticMethods.parseLatLng(dataItem.destination_geo);
        Route.TimeDistanceInfo info = dataItem.selectedRoute.getRouteTimeDistance();

        map.put("_token", getUserItem().token);
        map.put("origin_latitude", origin.latitude);
        map.put("origin_longitude", origin.longitude);
        map.put("origin_title", dataItem.origin_text);
        map.put("destination_latitude", destination.latitude);
        map.put("destination_longitude", destination.longitude);
        map.put("destination_title", dataItem.destination_text);
        map.put("expected_distance_format", info.getDistanceText());
        map.put("expected_distance", info.getDistanceValue());
//        map.put("estimates", dataItem.estimate);
//        map .put("estimates_format", dataItem.estimate_format);
        map.put("expected_duration", info.getTimeText());
//        map.put("expected_duration_format", info.getTimeText());

        map.put("time_range", dataItem.time_range);
        map.put("is_roundtrip", dataItem.isRoundTrip);
        map.put("is_enabled_booknow", dataItem.book_now);

//        map.put("stepped_route", getGson().toJson(selectedRoute.getAllCoordinates(500)));
        map.put("stepped_route", dataItem.selectedRoute.getOverviewPolyline().getRawPointList());
        map.put("preferences", dataItem.preferences);
        map.put("desired_gender", StaticAppMethods.getGenderInt(dataItem.gender));

        if (members != null)
            map.put("invited_members", StaticAppMethods.getAcceptedUserIds(members));

        if (driver != null) {
            map.put("driver_id", driver.user_id);
            map.put("seats_total", driver.seats);

            if (dataItem.isRoundTrip == 1) {
                map.put("seats_total_returning", driver.seats_returning);
            }
        }


        map.put("min_estimates", String.format("%.2f", dataItem.estimate_low));
        map.put("max_estimates", String.format("%.2f", dataItem.estimate_high));

//        map.put("expected_start_date", StaticMethods.convertTimeToUTC(dataItem.getDate()));
        map.put("expected_start_date", dataItem.getDate().getTime());

        if (dataItem.isRoundTrip == 1) {
//            map.put("date_returning", StaticMethods.convertTimeToUTC(dataItem.getReturnDate()));
            map.put("date_returning", dataItem.getReturnDate().getTime());
            map.put("time_range_returning", dataItem.return_time_range);
        }

        // endregion

        getActivityViewModel().createRide(map, true).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    StaticAppMethods.fireBaseAnalytics(getContext(), "create_user", map);
                    makeSnackbar(webResponseResource.data);
                    StaticAppMethods.deleteRequestData(null);
                    getFragmentActivity().replaceFragment(MyTripsFragment.newInstance(1));
                    hideLoader();
                    break;
                default:
                    hideLoader();
                    makeConnectionSnackbar();
                    break;
            }
        });
    }
}
