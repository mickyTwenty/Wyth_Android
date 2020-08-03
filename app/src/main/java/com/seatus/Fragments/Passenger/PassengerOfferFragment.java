package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.seatus.Adapters.PassengersOffersAdapter;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.BaseClasses.BaseOfferFragment;
import com.seatus.Dialogs.SeatUsDialog;
import com.seatus.Fragments.Driver.OtherDriverProfileFragment;
import com.seatus.Fragments.OtherProfileFragment;
import com.seatus.Interfaces.OnOfferAcceptInterface;
import com.seatus.Models.CardItem;
import com.seatus.Models.NotifObject;
import com.seatus.Models.OfferItem;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.DateTimeHelper;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Views.TitleBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by saqib on 12/7/2017.
 */

public class PassengerOfferFragment extends BaseOfferFragment implements OnOfferAcceptInterface {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.edit_msg)
    EditText editMsg;
    @BindView(R.id.circleImageView)
    CircleImageView img;
    @BindView(R.id.rating_driver)
    AppCompatRatingBar ratingDriver;
    @BindView(R.id.txt_driver_name)
    TextView txtDriverName;
    @BindView(R.id.txt_driver_licence)
    TextView txtDriverLicence;
    @BindView(R.id.txt_driver_vehicle_type)
    TextView txtDriverVehicleType;
    @BindView(R.id.layout_driver)
    LinearLayout layoutDriver;
    @BindView(R.id.txt_trip_name)
    TextView txtTripName;
    @BindView(R.id.txt_ride_status)
    TextView txtRideStatus;
    @BindView(R.id.txt_origin)
    TextView txtOrigin;
    @BindView(R.id.txt_destination)
    TextView txtDestination;
    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.checkbox_morning)
    CheckBox checkboxMorning;
    @BindView(R.id.checkbox_afternoon)
    CheckBox checkboxAfternoon;
    @BindView(R.id.checkbox_night)
    CheckBox checkboxNight;
    @BindView(R.id.txt_estimate)
    TextView txtEstimate;
    @BindView(R.id.ll_past_trip)
    LinearLayout llPastTrip;
    @BindView(R.id.layout_details)
    LinearLayout layoutDetails;
    @BindView(R.id.btn_toggle_details)
    TextView btnToggleDetails;
    @BindView(R.id.txt_bags)
    TextView txtBags;

    @BindView(R.id.txt_tooltip)
    TextView txtTooltip;

    private PassengersOffersAdapter offerAdapter;
    private FirestoreRecyclerOptions<OfferItem> offersOption;
    private SeatUsDialog offerDialog;
    private String cardNumber = "**** **** ";
    private String defaultCard;

    public static Fragment newInstance(String trip_id, String other_id) {
        PassengerOfferFragment fragment = new PassengerOfferFragment();
        fragment.trip_id = trip_id;
        fragment.other_id = other_id;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        fetchOfferDetails();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_offer;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("offers").enableBack();
    }

    @Override
    public void inits() {

        getCreditCards(null);

        Query chatQuery = FirebaseFirestore.getInstance()
                .collection(AppConstants.STORE_COLLECTION_GROUPS)
                .document(trip_id)
                .collection(StaticAppMethods.generateOfferId(getUserItem().user_id, other_id))
                .orderBy("timestamp");

        offersOption = new FirestoreRecyclerOptions.Builder<OfferItem>()
                .setQuery(chatQuery, OfferItem.class)
                .build();

        offerAdapter = new PassengersOffersAdapter(getContext(), getUserItem(), offersOption, this);

        setRecyclerview();
        recyclerview.setAdapter(offerAdapter);

    }

    private void setRecyclerview() {

        LinearLayoutManager ll = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ll.setStackFromEnd(true);
        recyclerview.setLayoutManager(ll);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        recyclerview.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerview.postDelayed(() -> recyclerview.scrollToPosition(
                        offerAdapter.getItemCount() - 1), 100);
            }
        });

        editMsg.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                recyclerview.scrollToPosition(offerAdapter.getItemCount() - 1);
            }
        });

        RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = offerAdapter.getItemCount();
                int lastVisiblePosition = ll.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    if (recyclerview != null)
                        recyclerview.scrollToPosition(positionStart);
                }
            }

        };

        offerAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    @Override
    public void setEvents() {

        AppStore.getInstance().getNotificationItem().observe(this, notifObject -> {
            if (notifObject == null || TextUtils.isEmpty(notifObject.trip_id) || !notifObject.trip_id.equals(trip_id) || TextUtils.isEmpty(notifObject.getOtherUserId()) || !other_id.equals(notifObject.getOtherUserId()))
                return;

            switch (notifObject.getAction()) {
                case NotifObject.ACTION_OFFERS_CONTRADICTED:
                case NotifObject.ACTION_PASSENGER_REMOVED:
                    DialogHelper.showAlertDialog(getContext(), notifObject.data_message);
                    getFragmentActivity().actionBack();
                    break;
                case NotifObject.ACTION_ACCEPTED_DRIVER:
                    getFragmentActivity().actionBack();
                    break;
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        offerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        offerAdapter.stopListening();
    }

    @OnClick({R.id.btn_toggle_details, R.id.btn_seat_minus, R.id.btn_seat_plus, R.id.btn_send, R.id.circleImageView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_toggle_details:
                if (layoutDetails.isShown()) {
                    layoutDetails.setVisibility(View.GONE);
                    btnToggleDetails.setText("View Details +");
                } else {
                    layoutDetails.setVisibility(View.VISIBLE);
                    btnToggleDetails.setText("Hide Details -");
                }
                break;
            case R.id.btn_seat_minus:
                int bags = Integer.parseInt(txtBags.getText().toString());
                if (bags > 0)
                    bags--;
                txtBags.setText(String.valueOf(bags));
                break;
            case R.id.btn_seat_plus:
                int noOfBags = Integer.parseInt(txtBags.getText().toString());
                if (noOfBags < 9)
                    noOfBags++;
                txtBags.setText(String.valueOf(noOfBags));
                break;
            case R.id.btn_send:
                actionSend();
                break;
            case R.id.circleImageView:
                getFragmentActivity().replaceFragmentWithBackstack(OtherDriverProfileFragment.newInstance(tripItem.driver.user_id));
                break;
        }
    }

    private void actionSend() {

        Integer bags = Integer.parseInt(txtBags.getText().toString());

        if (editMsg.getText().length() == 0)
            return;

        try {
            Float price = Float.valueOf(editMsg.getText().toString().trim());

            if (price < 5) {
                hideKeyboard();
                makeToast(getString(R.string.error_minimum_offer_amount));
                return;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("_token", getUserItem().token);
            map.put("trip_id", trip_id);
            if (getUserItem().getCurrentInterface()) {
                map.put("driver_id", other_id);
                map.put("bags", bags);
            } else {
                map.put("passenger_id", other_id);
            }

            map.put("price", price.toString());

            editMsg.setText("");
            getActivityViewModel().makeOffer(UserItem.ROLE_PASSENGER, map).observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
                        break;
                    case action_card_not_added:
                        hideLoader();
                        DialogHelper.showAlertDialog(getContext(), "You will need to add a credit card in order to proceed with making an offer. Would you like to add a credit card now?", (dialog1, which) -> {
//                            getFragmentActivity().replaceFragment(new PaymentsFragment());
                            getFragmentActivity().replaceFragmentWithBackstack(MyPaymentsFragment.newInstance(true));
                        });
                        break;
                    case success:
                        makeSnackbar(webResponseResource.data);
                        hideLoader();
                        break;
                    default:
                        makeSnackbar(webResponseResource.data);
                        hideLoader();
                        break;
                }
            });

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void fetchOfferDetails() {

        Map<String, Object> map = new HashMap<>();
        map.put("_token", getUserItem().token);
        if (getUserItem().getCurrentInterface())
            map.put("driver_id", other_id);
        else
            map.put("passenger_id", other_id);

        getActivityViewModel().getOfferDetails(UserItem.ROLE_PASSENGER, trip_id, map).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    if (webResponseResource.data.body != null) {
                        tripItem = webResponseResource.data.body;
                        setView();
                    }
                    break;
                default:
                    hideLoader();
                    break;
            }
        });
    }

    private void setView() {
        try {
            txtDriverName.setText(tripItem.driver.getFull_name());
            txtDriverLicence.setText(tripItem.driver.vehicle_id_number);
            txtDriverVehicleType.setText(tripItem.driver.vehicle_type);

            txtTripName.setText(tripItem.getTripName());
            txtOrigin.setText(tripItem.origin_title);
            txtDestination.setText(tripItem.destination_title);
            txtDate.setText(tripItem.date);
            txtEstimate.setText(tripItem.estimates_format);

            checkboxMorning.setChecked((tripItem.time_range & 1) != 0);
            checkboxAfternoon.setChecked((tripItem.time_range & 2) != 0);
            checkboxNight.setChecked((tripItem.time_range & 4) != 0);

            txtTooltip.setText(tripItem.is_roundtrip ? "Booking for Round Trip" : "Booking for Single Trip");

            try {
                Picasso.with(getContext()).load(tripItem.driver.profile_picture).fit().centerCrop().into(img);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ratingDriver.setRating(tripItem.driver.rating.floatValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCreditCards(OfferItem offer) {

        getActivityViewModel().getCreditCard().observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    List<CardItem> mList = webResponseResource.data.body;
                    for (int a = 0; a < mList.size(); a++) {
                        CardItem cardItem = mList.get(a);
                        if (cardItem.is_default) {
                            defaultCard = "*** **** " + cardItem.last_digits;
                            if (offer != null)
                                onOfferAccepted(offer);
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

    @Override
    public void onOfferAccepted(OfferItem offer) {

        if (TextUtils.isEmpty(defaultCard)) {
            getCreditCards(offer);
            return;
        }

        float trip_fee = Float.parseFloat(offer.price);
        float transaction_fee = (trip_fee > 5 ? AppStore.getInstance().getBootUpData().transaction_fee : AppStore.getInstance().getBootUpData().transaction_fee_local);
        float total_cost = transaction_fee + trip_fee;

        StringBuilder message = new StringBuilder();


        message.append("\n\nConfirm Transcation Details:")
                .append("\nTrip Fee: ")
                .append("$" + trip_fee)
                .append("\nTransaction Fee: ")
                .append("$" + transaction_fee)
                .append("\nTotal Cost: ")
                .append("$" + total_cost)
                .append("\nCredit Card Number: ")
                .append(defaultCard)
                .append("\n\nNote: Transaction fees are assessed as follows: One-way = $" + transaction_fee + " Round-Trip = $" + transaction_fee * 2)
                .append("\n\nCHARGE CARD?");

        new SeatUsDialog.Builder(getContext(), R.style.BounceDialog)
                .setHeaderImg(R.drawable.dialog_header_offer_accepted)
                .setTitleImg(R.drawable.dialog_title_accept_offer)
//                .setMessage("You will be charged $" + trip_fee + " + $" + AppStore.getInstance().getBootUpData().transaction_fee + " (Transaction Fee). Are you sure you want to accept this offer?")
                .setMessage(message.toString())
                .setMessageBold()
                .setFieldInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .setFieldDrawable(R.drawable.icon_price_dark)
                .setFieldHint("Promo Code (Optional)")
                .setPositiveButton("Yes", dialog -> {
                    offerDialog = dialog;
                    actionAcceptOffer(offer, dialog.getField().getText().toString());
                })
                .setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void actionAcceptOffer(OfferItem offer, String promo) {
        Float price = Float.valueOf(offer.price);
        Integer bags = 0;
        try {
            bags = Integer.parseInt(txtBags.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("_token", getUserItem().token);
        map.put("trip_id", trip_id);
        map.put("bags", bags);

        if (getUserItem().getCurrentInterface()) {
            map.put("driver_id", other_id);
            if (!TextUtils.isEmpty(promo))
                map.put("promo_code", promo);
        } else {
            map.put("passenger_id", other_id);
        }
        map.put("price", price.toString());

        getActivityViewModel().acceptOffer(UserItem.ROLE_PASSENGER, map).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    offerDialog.startProgress();
                    break;
                case success:
                    offerDialog.dismiss();
                    hideLoader();
                    DialogHelper.showAlertDialog(getContext(), webResponseResource.data);
                    getFragmentActivity().actionBack();
                    break;
                case error:
                    offerDialog.dismiss();
                    if (webResponseResource.data != null && !TextUtils.isEmpty(webResponseResource.data.message))
                        DialogHelper.showAlertDialog(getContext(), webResponseResource.data.message);
                    break;
                default:
                    offerDialog.dismiss();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    @Override
    public void onBagsChanged(Integer bags) {
        if (bags != null)
            txtBags.setText(bags.toString());
    }
}
