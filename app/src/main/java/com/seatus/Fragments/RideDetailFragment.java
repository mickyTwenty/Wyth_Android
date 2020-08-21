package com.seatus.Fragments;

import android.arch.lifecycle.LiveData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.seatus.Activities.MainActivity;
import com.seatus.BaseClasses.BaseStatesFragment;
import com.seatus.Dialogs.BaseOfferDialog;
import com.seatus.Dialogs.BookNowDialog;
import com.seatus.Dialogs.MakeOfferDialog;
import com.seatus.Dialogs.MakeOfferFreeReturnDialog;
import com.seatus.Dialogs.MakeOfferLocalDialog;
import com.seatus.Fragments.Driver.RideFlowFragment;
import com.seatus.Fragments.Driver.SearchRidersFragment;
import com.seatus.Fragments.Passenger.BankInfoFragment;
import com.seatus.Fragments.Passenger.MyPaymentsFragment;
import com.seatus.Fragments.Passenger.PassengerOfferFragment;
import com.seatus.Fragments.Passenger.SeatDetailsFragment;
import com.seatus.Fragments.Passenger.TrackRideFragment;
import com.seatus.Models.SearchFilterDataItem;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.DateTimeHelper;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.ViewModels.Resource;
import com.seatus.Views.RippleView;
import com.seatus.Views.TitleBar;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.seatus.Utils.AppConstants.LOCAL_TRIP_RANGE;
import static java.lang.Math.abs;

/**
 * Created by saqib on 11/22/2017.
 */

public class RideDetailFragment extends BaseStatesFragment {

    @BindView(R.id.circleImageView)
    CircleImageView circleImageView;
    @BindView(R.id.rating_driver)
    AppCompatRatingBar ratingDriver;
    @BindView(R.id.txt_driver_name)
    TextView txtDriverName;
    @BindView(R.id.txt_driver_licence)
    TextView txtDriverLicence;
    @BindView(R.id.txt_driver_vehicle_type)
    TextView txtDriverVehicleType;
    @BindView(R.id.txt_trip_name)
    TextView txtTripName;
    @BindView(R.id.txt_driver_cancel_count)
    TextView txtDriverCancelCount;
    @BindView(R.id.txt_ride_status)
    TextView txtRideStatus;
    @BindView(R.id.txt_origin)
    TextView txtOrigin;
    @BindView(R.id.txt_destination)
    TextView txtDestination;
    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.txt_time)
    TextView txtTime;

    @BindView(R.id.layout_timezone)
    GridLayout layoutTimzeZone;

    @BindView(R.id.checkbox_morning)
    CheckBox checkboxMorning;
    @BindView(R.id.checkbox_afternoon)
    CheckBox checkboxAfternoon;
    @BindView(R.id.checkbox_night)
    CheckBox checkboxNight;
    @BindView(R.id.txt_estimate)
    TextView txtEstimate;
    @BindView(R.id.txt_distance)
    TextView txtDistance;
    @BindView(R.id.txt_seats_hint)
    TextView txtSeatsHint;
    @BindView(R.id.txt_seats_count)
    TextView txtSeatsCount;
    @BindView(R.id.layout_available)
    RelativeLayout layoutAvailable;
    @BindView(R.id.layout_unavailable)
    RelativeLayout layoutUnavailable;
    @BindView(R.id.btn_view_map)
    RippleView btnViewMap;
    @BindView(R.id.btn_seats_available)
    RippleView btnSeatsAvailable;
    @BindView(R.id.btn_group_chat)
    RippleView btnGroupChat;
    @BindView(R.id.btn_share_itnerary)
    RippleView btnShareItnerary;
    @BindView(R.id.btn_cancel)
    RippleView btnCancel;
    @BindView(R.id.btn_booknow)
    TextView btnBooknow;
    @BindView(R.id.btn_offer)
    TextView btnOffer;
    @BindView(R.id.btn_decide_time)
    TextView btnDecideTime;
    @BindView(R.id.txt_is_round)
    TextView txtTripType;

    @BindView(R.id.btn_rideflow)
    TextView btnRideflow;
    @BindView(R.id.btn_complete_payment)
    TextView btnCompletePayment;

    @BindView(R.id.layout_member_buttons)
    LinearLayout layoutMember;
    @BindView(R.id.layout_nonmember_buttons)
    LinearLayout layoutNonMember;
    @BindView(R.id.layout_driver_info)
    LinearLayout layoutDriverInfo;
    @BindView(R.id.layout_seats)
    LinearLayout layoutSeats;
    @BindView(R.id.layout_cancel)
    LinearLayout layoutCancel;

    @BindView(R.id.layout_return_date)
    LinearLayout layoutDateReturning;
    @BindView(R.id.layout_timezonetitle_returning)
    LinearLayout layoutTimeZoneReturningTitle;
    @BindView(R.id.layout_timezone_returning)
    GridLayout layoutTimeZoneReturning;
    @BindView(R.id.divider_return)
    FrameLayout dividerReturn;

    @BindView(R.id.txt_date_returning)
    TextView txtDateReturning;

    @BindView(R.id.txt_seats_count_text)
    TextView txtSeatsCountText;
    @BindView(R.id.txt_return_seats_count_text)
    TextView txtReturnSeatsCountText;

    @BindView(R.id.checkbox_morning_return)
    CheckBox checkboxMorningReturning;
    @BindView(R.id.checkbox_afternoon_return)
    CheckBox checkboxAfternoonReturning;
    @BindView(R.id.checkbox_night_return)
    CheckBox checkboxNightReturning;

    @BindView(R.id.layout_trip_type)
    LinearLayout layoutTripType;

    @BindView(R.id.txt_seats_hint_return)
    TextView txtSeatsHintReturn;
    @BindView(R.id.txt_seats_count_return)
    TextView txtSeatsCountReturn;
    @BindView(R.id.layout_available_return)
    RelativeLayout layoutAvailableReturn;
    @BindView(R.id.layout_unavailable_return)
    RelativeLayout layoutUnavailableReturn;

    @BindView(R.id.layout_seats_return)
    LinearLayout layoutReturnSeats;
    @BindView(R.id.divider_seats_return)
    View dividerReturnSeats;
    @BindView(R.id.divider_seats)
    View dividerSeats;


    @BindView(R.id.layout_payment_option)
    LinearLayout layoutPaymentOption;
    @BindView(R.id.divider_payment_option)
    View dividerPaymentOption;
    @BindView(R.id.txt_payment_option)
    TextView txtPaymentOption;

    @BindView(R.id.txt_sharedwith)
    TextView txtSharedWith;
    @BindView(R.id.divider_sharedwith)
    View dividerSharedWith;
    @BindView(R.id.layout_sharedwith)
    LinearLayout layoutSharedWith;

    public String trip_id;
    private TripItem tripItem;
    private SearchFilterDataItem tempSearchItem;

    private ArrayList<TripItem> tripItems = new ArrayList<>();

    public static RideDetailFragment newInstance(String trip_id) {
        RideDetailFragment fragment = new RideDetailFragment();
        fragment.trip_id = trip_id;
        return fragment;
    }

    public static RideDetailFragment newInstance(String trip_id, SearchFilterDataItem tempSearchItem) {
        RideDetailFragment fragment = new RideDetailFragment();
        fragment.trip_id = trip_id;
        fragment.tempSearchItem = tempSearchItem;
        return fragment;
    }

    public void selectTime(Calendar calendar) {
        tripTimePreSelected(calendar);
    }

    @Override
    protected void onRetryClicked() {
        fetchTripData();
    }

    @Override
    protected int getStubLayout() {
        return R.layout.fragment_my_trip_detail;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        fetchTripData();
    }

    private void fetchTripData() {
        boolean searchReturn = false;
        if (tempSearchItem != null) {
            if (tempSearchItem.isSingleRoundTrip ? false : tempSearchItem.isRoundTrip == 1)
                searchReturn = true;
        }
        getActivityViewModel().getTripDetails(getUserItem().getRoleString(), trip_id, searchReturn).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    if (tripItem == null)
                        setContentType(ContentType.loading);
                    else
                        setView();
                    break;
                case success:
                    setContentType(ContentType.content);
                    tripItem = webResponseResource.data.body;
                    setView();
                    break;
                case error:
                    makeSnackbar(webResponseResource.data);
                    getFragmentActivity().resetDelay();
                    getFragmentActivity().actionBack();
                    break;
                default:
                    setContentType(ContentType.error);
                    break;
            }
        });
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("ride details").enableBack();
    }

    @Override
    public void inits() {
    }

    @Override
    public void setEvents() {
        AppStore.getInstance().getNotificationItem().observe(this, notifObject -> {
            if (notifObject == null || notifObject.getAction() == null)
                return;
            fetchTripData();
        });
    }

    private void setView() {
        try {
            if (getUserItem().getCurrentInterface()) {
                layoutDriverInfo.setVisibility(View.VISIBLE);
                txtDriverName.setText(tripItem.driver.getFull_name());
                txtDriverLicence.setText(tripItem.driver.vehicle_id_number);
                txtDriverVehicleType.setText(tripItem.driver.vehicle_type);
                txtDriverCancelCount.setText(tripItem.driver.trips_canceled_driver);

                try {
                    Picasso.with(getContext()).load(tripItem.driver.profile_picture).fit().centerCrop().into(circleImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (tripItem.is_member && tripItem.needs_payment)
                    btnCompletePayment.setVisibility(View.VISIBLE);
                else
                    btnCompletePayment.setVisibility(View.GONE);

                layoutPaymentOption.setVisibility(View.GONE);
                dividerPaymentOption.setVisibility(View.GONE);

            } else {
                layoutDriverInfo.setVisibility(View.GONE);

                layoutPaymentOption.setVisibility(View.VISIBLE);
                dividerPaymentOption.setVisibility(View.VISIBLE);

                txtPaymentOption.setText(StaticMethods.capitalizeFirstChar(tripItem.payout_type));
            }

            txtTripName.setText(tripItem.getTripName());
            txtOrigin.setText(tripItem.origin_title);
            txtDestination.setText(tripItem.destination_title);

            txtDate.setText(tripItem.date);

            checkboxMorning.setChecked((tripItem.time_range & 1) != 0);
            checkboxAfternoon.setChecked((tripItem.time_range & 2) != 0);
            checkboxNight.setChecked((tripItem.time_range & 4) != 0);

            //Old Seats Work
//            int count = 0;
//            if (!getUserItem().getCurrentInterface() && tripItem.is_request) {
//                count = tripItem.passengers.size();
//                txtSeatsCount.setText(String.valueOf(count));
//                txtSeatsHint.setText("Required  ");
//            } else if (tripItem.seats_available > 0) {
//                layoutAvailable.setVisibility(View.VISIBLE);
//                layoutUnavailable.setVisibility(View.GONE);
//                count = tripItem.seats_available;
//                txtSeatsCount.setText(String.valueOf(count));
//                txtSeatsHint.setText("Available  ");
//            } else {
//                layoutAvailable.setVisibility(View.GONE);
//                layoutUnavailable.setVisibility(View.VISIBLE);
//            }

            int count = 0;
            if (!getUserItem().getCurrentInterface() && tripItem.is_request) {
                count = tripItem.passengers.size();
                txtSeatsCountText.setText("Required " + String.valueOf(count));
            } else if (tripItem.is_request) {
                layoutSeats.setVisibility(View.GONE);
                dividerSeats.setVisibility(View.GONE);
            } else {
                txtSeatsCountText.setText(new StringBuilder().append(String.valueOf(tripItem.passengers.size())).append(" of ").append(String.valueOf(tripItem.seats_total)).append(tripItem.seats_total == 1 ? " seat" : " seats").append(" filled").toString());
            }


            if (getUserItem().getCurrentInterface())


                if (tripItem.itinerary_shared != null && !tripItem.itinerary_shared.isEmpty()) {
                    dividerSharedWith.setVisibility(View.VISIBLE);
                    layoutSharedWith.setVisibility(View.VISIBLE);
                    txtSharedWith.setText(tripItem.itinerary_shared.size() + "");
                } else {
                    dividerSharedWith.setVisibility(View.GONE);
                    layoutSharedWith.setVisibility(View.GONE);
                }

            if (tempSearchItem != null) {
                layoutTripType.setVisibility(View.VISIBLE);
                boolean isBookingAsRound = tempSearchItem.isSingleRoundTrip ? false : tempSearchItem.isRoundTrip == 1;
                if(Double.parseDouble(tripItem.expected_distance) < LOCAL_TRIP_RANGE)
                    txtEstimate.setText(StaticAppMethods.getEstimate(tripItem.min_estimates, isBookingAsRound));
                else
                    txtEstimate.setText(StaticAppMethods.getEstimate(tripItem.min_estimates, tripItem.max_estimates, isBookingAsRound));

                if (isBookingAsRound) {
                    txtTripType.setText("Round Trip");
                    setReturnUi();
                } else {
                    txtTripType.setText("Single Trip");
                }

            } else {
                layoutTripType.setVisibility(View.GONE);
                layoutReturnSeats.setVisibility(View.GONE);
                dividerReturnSeats.setVisibility(View.GONE);
                txtEstimate.setText(StaticAppMethods.getEstimate(tripItem.min_estimates, tripItem.max_estimates, false));
            }

            txtDistance.setText(tripItem.expected_distance_format);

            if (tripItem.isMember()) {
                layoutMember.setVisibility(View.VISIBLE);
                layoutNonMember.setVisibility(View.GONE);
            } else {
                layoutMember.setVisibility(View.GONE);
                layoutNonMember.setVisibility(View.VISIBLE);

                //if (tripItem.is_enabled_booknow)
                //    btnBooknow.setVisibility(View.VISIBLE);
                //else
                btnBooknow.setVisibility(View.GONE);

                if (tripItem.has_initiated_offer)
                    btnOffer.setText("View Offers");
                else
                    btnOffer.setText("Make Offer");
            }

//            if (getUserItem().getCurrentInterface())
//                cancelTxt.setText("Leave Ride");
//            else cancelTxt.setText("Cancel Ride");

            if (getUserItem().getCurrentInterface())
                btnCancel.setBackgroundResource(R.drawable.btn_green_leave_ride);
            else
                btnCancel.setBackgroundResource(R.drawable.btn_green_cancel_ride);


            try {
                switch (tripItem.getTripStatus()) {
                    case pending:
                    case active:
                        if(!tripItem.expected_start_time.contains("00:00:00"))
                            showRideTime();

                        btnCancel.setVisibility(View.VISIBLE);
                        if (tripItem.is_driver)
                            for (UserItem user : tripItem.passengers) {
                                if (user.is_confirmed && user.has_payment_made)
                                    btnDecideTime.setVisibility(View.VISIBLE);
                            }
                        break;
                    case filled:
                        if(!tripItem.expected_start_time.contains("00:00:00"))
                            showRideTime();
                        btnCancel.setVisibility(View.VISIBLE);
                        if (tripItem.is_driver)
                            btnDecideTime.setVisibility(View.VISIBLE);
                        break;
                    case confirmed:
                        btnDecideTime.setVisibility(View.GONE);
                        showRideTime();
                        if (tripItem.is_driver) {
                            btnRideflow.setTag(tripItem.getTripStatus());
                            btnRideflow.setText("Start Ride");
                            btnRideflow.setVisibility(View.VISIBLE);
                        } else
                            btnRideflow.setVisibility(View.GONE);
                        break;
                    case started:
                        showRideTime();
                        if (tripItem.is_driver) {
                            btnRideflow.setTag(tripItem.getTripStatus());
                            btnRideflow.setText("Continue to Ride");
                            btnRideflow.setVisibility(View.VISIBLE);
                        } else if (tripItem.is_member && !isDroped()) {
                            btnRideflow.setTag(tripItem.getTripStatus());
                            btnRideflow.setText("Track Ride");
                            btnRideflow.setVisibility(View.VISIBLE);
                        } else {
                            btnRideflow.setVisibility(View.GONE);
                        }
                        break;
                    case goingCompleted:
                        if (tripItem.is_driver)
                            btnDecideTime.setVisibility(View.VISIBLE);
                        break;
                    case returnConfirmed:
                        showRideTime();
                        if (tripItem.is_driver) {
                            btnRideflow.setTag(TripItem.TripStatus.confirmed);
                            btnRideflow.setText("Start Return Trip");
                            btnRideflow.setVisibility(View.VISIBLE);
                        } else
                            btnRideflow.setVisibility(View.GONE);
                        break;
                    case canceled:
                    case failed:
                    case expired:
                        layoutMember.setVisibility(View.GONE);
                        layoutNonMember.setVisibility(View.GONE);
                        layoutSeats.setVisibility(View.GONE);
                        layoutCancel.setVisibility(View.VISIBLE);
                        break;
                    default:
                        showRideTime();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ratingDriver.setRating(tripItem.driver.rating.floatValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isDroped() {
        for (UserItem passenger : tripItem.passengers)
            if (passenger.user_id.equals(getUserItem().user_id) && passenger.is_dropped)
                return true;
        return false;
    }

    private void setReturnUi() {

        try {
            layoutDateReturning.setVisibility(View.VISIBLE);
            layoutTimeZoneReturningTitle.setVisibility(View.VISIBLE);
            layoutTimeZoneReturning.setVisibility(View.VISIBLE);
            dividerReturn.setVisibility(View.VISIBLE);

            TripItem returnTrip = tripItem.return_trip;
            if (returnTrip != null) {
                checkboxMorningReturning.setChecked((returnTrip.time_range & 1) != 0);
                checkboxAfternoonReturning.setChecked((returnTrip.time_range & 2) != 0);
                checkboxNightReturning.setChecked((returnTrip.time_range & 4) != 0);
                txtDateReturning.setText(returnTrip.date);

//                int count = 0;
//                if (!getUserItem().getCurrentInterface() && tripItem.is_request) {
//                    count = returnTrip.seats_total - returnTrip.seats_available;
//                    txtSeatsCountReturn.setText(String.valueOf(count));
//                    txtSeatsHintReturn.setText("Required  ");
//                } else if (returnTrip.seats_available > 0) {
//                    layoutAvailableReturn.setVisibility(View.VISIBLE);
//                    layoutUnavailableReturn.setVisibility(View.GONE);
//                    count = returnTrip.seats_available;
//                    txtSeatsCountReturn.setText(String.valueOf(count));
//                    txtSeatsHintReturn.setText("Available  ");
//                } else {
//                    layoutAvailableReturn.setVisibility(View.GONE);
//                    layoutUnavailableReturn.setVisibility(View.VISIBLE);
//                }

                int count = 0;
                if (!getUserItem().getCurrentInterface() && tripItem.is_request) {
                    count = returnTrip.seats_total - returnTrip.seats_available;
                    txtReturnSeatsCountText.setText("Required " + String.valueOf(count));
                } else if (tripItem.is_request) {
                    layoutReturnSeats.setVisibility(View.GONE);
                    dividerReturnSeats.setVisibility(View.GONE);
                } else {
                    txtReturnSeatsCountText.setText(new StringBuilder().append(String.valueOf(returnTrip.seats_total - returnTrip.seats_available)).append(" of ").append(String.valueOf(returnTrip.seats_total)).append(returnTrip.seats_total == 1 ? " seat" : " seats").append(" filled").toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showRideTime() {
        try {
            layoutTimzeZone.setVisibility(View.GONE);
            txtTime.setVisibility(View.VISIBLE);
            btnDecideTime.setText("Lock in Passengers");
            Log.e("Scheduled Time", tripItem.expected_start_time);
            txtTime.setText(DateTimeHelper.getTimeToShow(DateTimeHelper.convertFromUTC(DateTimeHelper.parseDate(tripItem.expected_start_time, DateTimeHelper.DATE_SERVER_FORMAT).getTime())));
//            txtTime.setText(DateTimeHelper.getTimeToShow(DateTimeHelper.parseDate(tripItem.expected_start_time, DateTimeHelper.DATE_SERVER_FORMAT).getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.layout_sharedwith, R.id.btn_cancel, R.id.btn_complete_payment, R.id.btn_rideflow, R.id.btn_decide_time, R.id.btn_view_map, R.id.btn_seats_available, R.id.btn_group_chat, R.id.btn_share_itnerary, R.id.btn_booknow, R.id.btn_offer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_rideflow:
                switch ((TripItem.TripStatus) btnRideflow.getTag()) {
                    case confirmed:
                        if (tripItem.is_driver)
                            startOrResumeRide(true);
                        break;
                    case started:
                        if (tripItem.is_driver)
                            startOrResumeRide(false);
                        else {
                            getFragmentActivity().replaceFragmentWithBackstack(TrackRideFragment.newInstance(tripItem));
                        }
                        break;
                }
                break;
            case R.id.btn_view_map:
                getFragmentActivity().replaceFragmentWithBackstack(RideMapDetailsFragment.newInstance(tripItem));
                break;
            case R.id.btn_cancel:
                actionCancel();
                break;
            case R.id.btn_complete_payment:
                actionCompletePayment();
                break;
            case R.id.btn_decide_time:
                if(tripItem.expected_start_time!=null && !tripItem.expected_start_time.contains("00:00:00")) {
                    long dateTime = DateTimeHelper.convertFromUTC(DateTimeHelper.parseDate(tripItem.expected_start_time, DateTimeHelper.DATE_SERVER_FORMAT).getTime());
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(dateTime);
                    cal.add(Calendar.DATE, 1);
                    tripTimeSelected(cal, true);
                }
                else {
                    if (tripItem.getTripStatus() == TripItem.TripStatus.active)
                        DialogHelper.showAlertDialog(getContext(), "There are still some seats available. If you choose to proceed with the trip then the remaining seats will be cancelled.", (dialog, which) ->
                                StaticMethods.timePopup(getContext(), DateTimeHelper.parseDate(tripItem.date).getTime(), cal -> tripTimeSelected(cal, true))
                        );
                    else
                        StaticMethods.timePopup(getContext(), DateTimeHelper.parseDate(tripItem.date).getTime(), cal -> tripTimeSelected(cal, false));
                }


                break;
            case R.id.btn_seats_available:
                getFragmentActivity().replaceFragmentWithBackstack(SeatDetailsFragment.newInstance(tripItem));
                break;
            case R.id.btn_group_chat:
                if ((tripItem.passengers.size() > 0 && tripItem.driver != null) || tripItem.passengers.size() > 1)
                    getFragmentActivity().replaceFragmentWithBackstack(ChatFragment.newInstance(tripItem.trip_id));
                else makeSnackbar("There is currently no one to chat in this trip.");
                break;
            case R.id.btn_share_itnerary:
                getFragmentActivity().replaceFragmentWithBackstack(ShareItenraryFragment.newInstance(tripItem, true));
                break;
            case R.id.layout_sharedwith:
                getFragmentActivity().replaceFragmentWithBackstack(ShareItenraryFragment.newInstance(tripItem, false));
                break;
            case R.id.btn_booknow:
                new BookNowDialog.Builder(getContext(), R.style.BounceDialog)
                        .setHeaderImg(R.drawable.dialog_header_booknow)
                        .setTitleImg(R.drawable.dialog_title_booknow)
                        .setMessage(R.string.dialog_message_booknow)
                        .setMessageBold()
                        .setEnableBags(true)
                        .showEstimate(tripItem.estimates_format)
                        .setPositiveButton((dialog, promocode, bags) -> actionJoin(dialog, null, bags, promocode))
                        .show();
                break;
            case R.id.btn_offer:
                if (tempSearchItem == null) {
                    makeSnackbar("Some Error Occurred");
                    getFragmentActivity().resetDelay();
                    getFragmentActivity().actionBack();
                    return;
                }
                if (!tripItem.has_initiated_offer) {
                    if (Double.parseDouble(tripItem.expected_distance) < LOCAL_TRIP_RANGE) {

                        String currentDateTime = DateTimeHelper.currentDateTime();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(DateTimeHelper.parseDate(currentDateTime, "yyyy-MM-dd hh:mm:ss"));
                        long currentTime = calendar.getTimeInMillis();

                        if(tripItem.is_enabled_booknow && !tripItem.expected_start_time.contains("00:00:00")) {

                            new MakeOfferFreeReturnDialog.Builder(getContext(), R.style.BounceDialog)
                                    .setHeaderImg(R.drawable.dialog_header_makeoffer)
                                    .setTitleImg(R.drawable.dialog_title_makeoffer)
                                    .setMessage(R.string.dialog_message_makeofferfreereturn)
                                    .setMessageBold()
                                    .setEnableBagsElseSeats(getUserItem().getCurrentInterface())
                                    .enableReturnSeats(!getUserItem().getCurrentInterface() && tempSearchItem.isRoundTrip == 1)
                                    .setPositiveButton((dialog, price, bagsOrSeats, returnSeats) -> {
                                        //if (getUserItem().getCurrentInterface())
                                        actionJoin(dialog, null, bagsOrSeats, "WAL2MART");
                                        //else
                                        //    makeOffer(null, null, "WAL2MART", bagsOrSeats, returnSeats, null, dialog);
                                    })
                                    .show();

                        }
                        /*    getActivityViewModel().getMyTrips("passenger/past", "", "", "").observe(this, webResponseResource -> {
                                switch (webResponseResource.status) {
                                    case loading:
                                        showLoader();
                                        break;
                                    case success:
                                        hideLoader();
                                        boolean offerMade = false;
                                        if (webResponseResource.data.body != null) {
                                            tripItems.addAll(webResponseResource.data.body);

                                            // Get Current Time
                                            long mostRecentTime = 0;
                                            String recentOriginTitle = "";
                                            LatLng recentOriginGeo = null;
                                            String recentDestinationTitle = "";
                                            LatLng recentDestinationGeo = null;

                                            for (TripItem trip : tripItems) {
                                                String endDate = trip.ended_at;
                                                if (endDate != null) {
                                                    Calendar calendarEnd = Calendar.getInstance();
                                                    calendarEnd.setTime(DateTimeHelper.parseDate(endDate, "yyyy-MM-dd hh:mm:ss"));
                                                    calendarEnd.add(Calendar.HOUR, 4);
                                                    long endTime = calendarEnd.getTimeInMillis();

                                                    if (currentTime < endTime) {
                                                        if (endTime > mostRecentTime) {
                                                            mostRecentTime = endTime;

                                                            // Because this is the return trip, swap Origin and Destinations
                                                            recentOriginTitle = trip.destination_title;
                                                            recentOriginGeo = trip.getDestinationLatLng();

                                                            recentDestinationTitle = trip.origin_title;
                                                            recentDestinationGeo = trip.getOriginLatLng();
                                                        }
                                                    }
                                                }
                                            }
                                            if (recentOriginGeo != null && recentDestinationGeo != null) {
                                                double latDiff = abs(recentOriginGeo.latitude - Double.parseDouble(tripItem.origin_latitude));
                                                double longDiff = abs(recentOriginGeo.latitude - Double.parseDouble(tripItem.origin_latitude));
                                                if (recentOriginTitle.equals(tripItem.destination_title) || (latDiff < 0.007 && longDiff < 0.007)) {
                                                    new MakeOfferFreeReturnDialog.Builder(getContext(), R.style.BounceDialog)
                                                            .setHeaderImg(R.drawable.dialog_header_makeoffer)
                                                            .setTitleImg(R.drawable.dialog_title_makeoffer)
                                                            .setMessage(R.string.dialog_message_makeofferfreereturn)
                                                            .setMessageBold()
                                                            .setEnableBagsElseSeats(getUserItem().getCurrentInterface())
                                                            .enableReturnSeats(!getUserItem().getCurrentInterface() && tempSearchItem.isRoundTrip == 1)
                                                            .setPositiveButton((dialog, price, bagsOrSeats, returnSeats) -> {
                                                                //if (getUserItem().getCurrentInterface())
                                                                actionJoin(dialog, null, bagsOrSeats, "WAL2MART");
                                                                //else
                                                                //    makeOffer(null, null, "WAL2MART", bagsOrSeats, returnSeats, null, dialog);
                                                            })
                                                            .show();

                                                    offerMade = true;
                                                }
                                            }
                                        }

                                        if (!offerMade) {
                                            new BookNowDialog.Builder(getContext(), R.style.BounceDialog)
                                                    .setHeaderImg(R.drawable.dialog_header_booknow)
                                                    .setTitleImg(R.drawable.dialog_title_booknow)
                                                    .setMessage(R.string.dialog_message_booknow)
                                                    .setMessageBold()
                                                    .setEnableBags(true)
                                                    .showEstimate("$5.50")
                                                    .setPositiveButton((dialog, promocode, bags) -> actionJoin(dialog, null, bags, promocode))
                                                    .show();
                                        }

                                        break;
                                    default:
                                        showLoader();
                                        break;
                                }
                            });
                        }
                        else
                        {
                            new MakeOfferLocalDialog.Builder(getContext(), R.style.BounceDialog)
                                    .setHeaderImg(R.drawable.dialog_header_makeoffer)
                                    .setTitleImg(R.drawable.dialog_title_makeoffer)
                                    .setMessage(R.string.dialog_message_makeofferlocal)
                                    .setMessageBold()
                                    .setEnableBagsElseSeats(getUserItem().getCurrentInterface())
                                    .enableReturnSeats(!getUserItem().getCurrentInterface() && tempSearchItem.isRoundTrip == 1)
                                    .setPositiveButton((dialog, price, bagsOrSeats, returnSeats) -> {
                                        if (getUserItem().getCurrentInterface())
                                            actionJoin(dialog, price, bagsOrSeats, null);
                                        else
                                            makeOffer(price, null, null, bagsOrSeats, returnSeats, null, dialog);
                                    })
                                    .show();
                        }
*/
                    } else {
                        new MakeOfferDialog.Builder(getContext(), R.style.BounceDialog)
                                .setHeaderImg(R.drawable.dialog_header_makeoffer)
                                .setTitleImg(R.drawable.dialog_title_makeoffer)
                                .setMessage(R.string.dialog_message_makeoffer)
                                .setMessageBold()
                                .setEnablePrice()
                                .showEstimate(StaticAppMethods.getEstimate(tripItem.min_estimates, tripItem.max_estimates, tempSearchItem.isSingleRoundTrip ? false : tempSearchItem.isRoundTrip == 1))
                                .setEnableBagsElseSeats(getUserItem().getCurrentInterface())
                                .enableReturnSeats(!getUserItem().getCurrentInterface() && tempSearchItem.isRoundTrip == 1)
                                .setPositiveButton((dialog, price, bagsOrSeats, returnSeats) -> {
                                    if (getUserItem().getCurrentInterface())
                                        actionJoin(dialog, price, bagsOrSeats, null);
                                    else
                                        makeOffer(price, null, null, bagsOrSeats, returnSeats, null, dialog);
                                })
                                .show();
                    }
                }
                else {
                    if (getUserItem().getCurrentInterface())
                        getFragmentActivity().replaceFragmentWithBackstack(PassengerOfferFragment.newInstance(tripItem.trip_id, tripItem.getOtherUserId()));
                    else
                        makeSnackbar("Please Check all the offers for this trip in your Offers");
                }
                break;
        }
    }

    private String getEstimate() {

        return null;
    }

    private void actionCancel() {

        String actionString = getUserItem().getCurrentInterface() ? "Leave Ride" : "Cancel Ride";
        DialogInterface.OnClickListener dialogListener = (dialog, which) -> {
            getActivityViewModel().cancelRide(getUserItem().getRoleString(), tripItem.trip_id).observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case success:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        if (getUserItem().getCurrentInterface()) {
                            tripItem = webResponseResource.data.body;
                            setView();
                        } else {
                            getFragmentActivity().actionBack();
                        }
                        break;
                    default:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        break;
                }
            });
        };

        new AlertDialog.Builder(getContext())
                .setTitle(actionString)
                .setMessage("Are you sure you want to " + actionString + "?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", dialogListener)
                .show();
    }

    private void actionCompletePayment() {
        getActivityViewModel().completePayment(tripItem.trip_id).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    fetchTripData();
                    showLoader();
                    break;
                case error:
                    hideLoader();
                    if (webResponseResource.data != null && !TextUtils.isEmpty(webResponseResource.data.message))
                        DialogHelper.showAlertDialog(getContext(), webResponseResource.data.message);
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }


    private void startOrResumeRide(boolean isStartRide) {

        ((MainActivity) getFragmentActivity()).checkLocationEnabled(() -> {

            ((MainActivity) getFragmentActivity()).startForegroundService();

            LiveData<Resource<WebResponse<TripItem>>> call = null;
            if (isStartRide)
                call = getActivityViewModel().startRide(tripItem.trip_id);
            else
                call = getActivityViewModel().resumeRide(tripItem.trip_id);

            call.observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case success:
                        hideLoader();
                        TripItem tmpItem = webResponseResource.data.body;
                        PreferencesManager.putString(AppConstants.KEY_TRIP, getGson().toJson(tmpItem));
                        getFragmentActivity().replaceFragmentWithBackstack(RideFlowFragment.newInstance(tmpItem));
                        break;
                    default:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        break;
                }
            });
        }, true);
    }

    private void tripTimePreSelected(Calendar calendar) {

        calendar.add(Calendar.SECOND, 1);

        Log.e("Scheduletime", calendar.getTimeInMillis() + "");

        if (!isFutureTime(calendar)) {
            makeSnackbar("Please Select Future Time");
        } else if (!isTimeInTimeZone(calendar)) {
            makeSnackbar("Selected time does not belong in the Trip TimeZone");
        } else {
            getActivityViewModel().preScheduleTripTime(tripItem.trip_id, calendar.getTimeInMillis()).observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case success:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        fetchTripData();
                        break;
                    default:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        break;
                }
            });
        }
    }

    private void tripTimeSelected(Calendar calendar, boolean forcefully) {

        Log.e("Scheduletime", calendar.getTimeInMillis() + "");

        if (!isFutureTime(calendar)) {
            makeSnackbar("Please Select Future Time");
        } else if (!isTimeInTimeZone(calendar)) {
            makeSnackbar("Selected time does not belong in the Trip TimeZone");
        } else {
            getActivityViewModel().scheduleTripTime(forcefully, tripItem.trip_id, calendar.getTimeInMillis()).observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case success:
                        hideLoader();
                        fetchTripData();
                        break;
                    default:
                        hideLoader();
                        makeSnackbar(webResponseResource.data);
                        break;
                }
            });
        }
    }

    private boolean isFutureTime(Calendar calendar) {

        Calendar currTime = Calendar.getInstance();
        Calendar selectedTime = Calendar.getInstance();
        selectedTime.setTimeInMillis(calendar.getTimeInMillis());

        if (currTime.after(selectedTime))
            return false;


        return true;
    }

    private boolean isTimeInTimeZone(Calendar calendar) {

        boolean valid = false;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if ((tripItem.time_range & 1) != 0)
            if (6 <= hour && hour <= 11)
                valid = true;
        if ((tripItem.time_range & 2) != 0)
            if (12 <= hour && hour <= 17)
                valid = true;
        if ((tripItem.time_range & 4) != 0)
            if (18 <= hour && hour <= 24)
                valid = true;


        return valid;
    }

    private void actionJoin(BaseOfferDialog dialog, Float price, Integer bags, String promocode) {

        if (tempSearchItem == null || tripItem == null) {
            makeSnackbar(getString(R.string.error_some_issue));
            getFragmentActivity().actionBack();
            return;
        }

        dialog.startProgress();
        StaticAppMethods.fetchValidInvites(getContext(), tempSearchItem, (status, documents) -> {
            switch (status) {
                case connectionError:
                    dialog.setError(getString(R.string.error_connection));
                    dialog.stopProgress();
                    break;
                case noInvites:
                    makeOffer(price, bags, promocode, null, null, null, dialog);
                    break;
                case expiredContinue:
                    makeOffer(price, bags, promocode, null, null, null, dialog);
                    break;
                case expiredAbort:
                    dialog.dismiss();
                    break;
                case invitesPending:
                    dialog.dismiss();
                    DialogHelper.showAlertDialog(getContext(), getString(R.string.error_pending_invites));
                    break;
                case invitesAvailaible:
                    makeOffer(price, bags, promocode, null, null, documents, dialog);
                    break;
            }
        });
    }

    private void makeOffer(Float price, Integer bags, String promoCode, Integer seats, Integer returnSeats, List<UserItem> documents, BaseOfferDialog dialog) {


        if (price!=null && price < 5) {
            makeToast(getString(R.string.error_minimum_offer_amount));
            dialog.stopProgress();
            return;
        }

        boolean isBookingAsRound = false;
        if (getUserItem().getCurrentInterface())
            isBookingAsRound = tempSearchItem.isSingleRoundTrip ? false : tempSearchItem.isRoundTrip == 1;
        else
            isBookingAsRound = tempSearchItem.isRoundTrip == 1;

        Map<String, Object> map = new HashMap<>();
        map.put("_token", getUserItem().token);
        map.put("trip_id", tripItem.trip_id);
        map.put("time_range", tempSearchItem.time_range);

        if (isBookingAsRound) {
            map.put("time_range_returning", tempSearchItem.return_time_range);
        }

        if (price != null)
            map.put("price", price);

        if (getUserItem().getCurrentInterface()) {

            map.put("driver_id", tripItem.driver.user_id);
            map.put("bags", bags);
            map.put("is_roundtrip", isBookingAsRound ? 1 : 0);


            LatLng origin = StaticMethods.parseLatLng(tempSearchItem.origin_geo);
            LatLng destination = StaticMethods.parseLatLng(tempSearchItem.destination_geo);

            map.put("pickup_latitude", origin.latitude);
            map.put("pickup_longitude", origin.longitude);
            map.put("pickup_title", tempSearchItem.origin_text);
            map.put("dropoff_latitude", destination.latitude);
            map.put("dropoff_longitude", destination.longitude);
            map.put("dropoff_title", tempSearchItem.destination_text);
            map.put("dropoff_title", tempSearchItem.destination_text);

            if (documents != null)
                map.put("invited_members", StaticAppMethods.getAcceptedUserIds(documents));
        } else {
            map.put("seats_total", seats);
            map.put("is_roundtrip", tempSearchItem.isRoundTrip);
            if (tempSearchItem.isRoundTrip == 1) {
                map.put("seats_total_returning", returnSeats);
                int returnSeatsRequired = tripItem.return_trip.seats_total - tripItem.seats_available;
                if (tripItem.return_trip != null && seats < returnSeatsRequired) {
                    DialogHelper.showAlertDialog(getContext(), String.format(" Minimum %d seats are required for this Ride !", returnSeatsRequired));
                    return;
                }
            }

            if (seats < tripItem.passengers.size()) {
                DialogHelper.showAlertDialog(getContext(), String.format(" Minimum %d seats are required for this Ride !", tripItem.passengers.size()));
                return;
            }
        }

        LiveData<Resource<WebResponse>> task;
        if (price == null) {
            if (!TextUtils.isEmpty(promoCode))
                map.put("promo_code", promoCode);
            task = getActivityViewModel().bookNow(map);
        } else
            task = getActivityViewModel().makeOffer(getUserItem().getRoleString(), map);

        task.observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    dialog.startProgress();
                    break;
                case success:
                    makeSnackbar(webResponseResource.data);
                    dialog.dismiss();
                    if (price == null) {
                        tripItem.is_member = true;
                        setView();
                    } else {
                        tripItem.has_initiated_offer = true;
                        setView();
                    }
                    fetchTripData();
                    break;
                case action_card_not_added:
                    dialog.dismiss();
                    DialogHelper.showAlertDialog(getContext(), "You will need to add a credit card in order to proceed with making an offer. Would you like to add a credit card now?", (dialog1, which) -> {
//                        getFragmentActivity().replaceFragment(new PaymentsFragment());
                        getFragmentActivity().replaceFragmentWithBackstack(MyPaymentsFragment.newInstance(true));
                    });
                    break;
                case add_bank_account:
                    dialog.dismiss();
                    makeSnackbar(webResponseResource.data);
                    getFragmentActivity().replaceFragmentWithBackstack(BankInfoFragment.newInstance(true));
                    break;
                case error:
                    dialog.dismiss();
                    if (webResponseResource.data != null && !TextUtils.isEmpty(webResponseResource.data.message))
                        DialogHelper.showAlertDialog(getContext(), webResponseResource.data.message);
                    break;
                default:
                    dialog.dismiss();
                    if (webResponseResource.data != null)
                        dialog.setError(webResponseResource.data.message);
                    break;
            }
        });
    }

}
