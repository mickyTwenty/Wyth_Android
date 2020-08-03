package com.seatus.Fragments.Driver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.seatus.Adapters.DriverOffersAdapter;
import com.seatus.Adapters.PassengersOffersAdapter;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.BaseClasses.BaseOfferFragment;
import com.seatus.Fragments.OtherProfileFragment;
import com.seatus.Interfaces.DriverOfferAcceptInterface;
import com.seatus.Interfaces.OnOfferAcceptInterface;
import com.seatus.Models.NotifObject;
import com.seatus.Models.OfferItem;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Views.TitleBar;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saqib on 12/7/2017.
 */

public class DriverOfferFragment extends BaseOfferFragment implements DriverOfferAcceptInterface {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.edit_msg)
    EditText editMsg;
    @BindView(R.id.circleImageView)
    CircleImageView img;
    @BindView(R.id.rating)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.txt_name)
    TextView txtDriverName;
    @BindView(R.id.txt_bags)
    TextView txtBags;


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

    @BindView(R.id.txt_tooltip)
    TextView txtTooltip;


    private DriverOffersAdapter offerAdapter;
    private FirestoreRecyclerOptions<OfferItem> offersOption;

    public static Fragment newInstance(String trip_id, String other_id) {
        DriverOfferFragment fragment = new DriverOfferFragment();
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
        return R.layout.fragment_driver_offer;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("offers").enableBack();
    }

    @Override
    public void inits() {

        Query chatQuery = FirebaseFirestore.getInstance()
                .collection(AppConstants.STORE_COLLECTION_GROUPS)
                .document(trip_id)
                .collection(StaticAppMethods.generateOfferId(getUserItem().user_id, other_id))
                .orderBy("timestamp");

        offersOption = new FirestoreRecyclerOptions.Builder<OfferItem>()
                .setQuery(chatQuery, OfferItem.class)
                .build();

        offerAdapter = new DriverOffersAdapter(getContext(), getUserItem(), offersOption, this);

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

                case NotifObject.ACTION_ACCEPTED_PASSENGER:
                    DialogHelper.showAlertDialog(getContext(), notifObject.data_message);
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

    @OnClick({R.id.btn_toggle_details, R.id.btn_send})
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
            case R.id.btn_send:
                actionSend();
                break;
        }
    }

    private void actionSend() {

        Integer bags = 0;
        try {
            bags = Integer.parseInt(txtBags.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

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
            map.put("bags", bags);
            if (getUserItem().getCurrentInterface()) {

                map.put("driver_id", other_id);
            } else {
                map.put("passenger_id", other_id);
            }

            map.put("price", price.toString());

            editMsg.setText("");
            getActivityViewModel().makeOffer(UserItem.ROLE_DRIVER, map).observe(this, webResponseResource -> {
                switch (webResponseResource.status) {
                    case loading:
                        showLoader();
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

        getActivityViewModel().getOfferDetails(UserItem.ROLE_DRIVER, trip_id, map).observe(this, webResponseResource -> {
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
            txtDriverName.setText(tripItem.passenger.getFull_name());

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
                Picasso.with(getContext()).load(tripItem.passenger.profile_picture).fit().centerCrop().into(img);
                img.setOnClickListener(v -> getFragmentActivity().replaceFragmentWithBackstack(OtherProfileFragment.newInstance(tripItem.passenger.user_id)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            ratingBar.setRating(tripItem.passenger.rating.floatValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOfferAccepted(OfferItem offer) {

        Float price = Float.valueOf(offer.price);
        Map<String, Object> map = new HashMap<>();
        map.put("_token", getUserItem().token);
        map.put("trip_id", trip_id);
        if (getUserItem().getCurrentInterface())
            map.put("driver_id", other_id);
        else {
            map.put("passenger_id", other_id);
            map.put("bags", offer.bags);
        }
        map.put("price", price.toString());

        getActivityViewModel().acceptOffer(UserItem.ROLE_DRIVER, map).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    DialogHelper.showAlertDialog(getContext(), webResponseResource.data);
                    getFragmentActivity().actionBack();
                    break;
                default:
                    hideLoader();
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
