package com.seatus.Fragments.Driver;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Holders.PassengerRatingHolder;
import com.seatus.Models.NotificationListingItem;
import com.seatus.Models.RateItem;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by saqib on 2/8/2018.
 */

public class PassengerRatingFragment extends BaseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private EfficientRecyclerAdapter<NotificationListingItem> mAdp;
    private ArrayList<UserItem> mList = new ArrayList<>();
    private TripItem tripItem;

    public static PassengerRatingFragment newInstance(TripItem tripItem, ArrayList<UserItem> list) {
        PassengerRatingFragment fragment = new PassengerRatingFragment();
        fragment.tripItem = tripItem;
        fragment.mList.addAll(list);
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_passenger_rating;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("rate passengers");
    }

    @Override
    public void inits() {
        StaticMethods.initVerticalRecycler(getContext(), true, recyclerview);

        mAdp = new EfficientRecyclerAdapter(R.layout.item_passenger_rating, PassengerRatingHolder.class, mList);
        recyclerview.setAdapter(mAdp);
    }

    @Override
    public void setEvents() {

    }

    public void ratePassenger(int pos) {
        UserItem userItem = mList.get(pos);
        DialogHelper.showRateDialog(getContext(), userItem.getFull_name(), tripItem.date, (rate, comment, dialog) -> {
            userItem.rating = Double.valueOf(rate);
            userItem.feedback = comment;
            userItem.is_rated = true;
            mAdp.notifyDataSetChanged();
        });
    }

    private void ratePassengers() {
        JSONArray jsonArray = new JSONArray();

        for (int a = 0; a < mList.size(); a++) {
            UserItem user = mList.get(a);
            if (user.rating == 0 || user.rating == null) {
                makeSnackbar("Please rate " + user.getFull_name());
                return;
            } else {
                RateItem rateItem = new RateItem();
                rateItem.setUser_id(user.user_id);
                rateItem.setTrip_id(tripItem.trip_id);
                rateItem.setRating(user.rating);
                rateItem.setFeedback(user.feedback);
                jsonArray.put(rateItem.toJsonObject());
            }
        }

        getActivityViewModel().ratePassengers(jsonArray.toString()).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    getUserItem().has_pending_ratings = false;
                    PreferencesManager.putObject(AppConstants.KEY_USER, getUserItem());
                    getFragmentActivity().actionBack();
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    @OnClick(R.id.btn_save)
    public void onViewClicked() {
        ratePassengers();
    }
}
