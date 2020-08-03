package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Fragments.RateFragment;
import com.seatus.Holders.DriverRatingHolder;
import com.seatus.Models.NotificationListingItem;
import com.seatus.Models.PendingRatingItem;
import com.seatus.Models.RateItem;
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
import butterknife.OnClick;

/**
 * Created by saqib on 2/8/2018.
 */

public class HomeDriverRatingFragment extends BaseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private EfficientRecyclerAdapter<NotificationListingItem> mAdp;
    private ArrayList<PendingRatingItem.Data> mList = new ArrayList<>();

    public static HomeDriverRatingFragment newInstance(ArrayList<PendingRatingItem.Data> list) {
        HomeDriverRatingFragment fragment = new HomeDriverRatingFragment();
        fragment.mList.addAll(list);
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home_rating;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
    }

    @Override
    public void inits() {
        StaticMethods.initVerticalRecycler(getContext(), true, recyclerview);

        mAdp = new EfficientRecyclerAdapter(R.layout.item_driver_rating, DriverRatingHolder.class, mList);
        recyclerview.setAdapter(mAdp);
    }

    @Override
    public void setEvents() {

    }

    public void rate(int pos) {
        UserItem userItem = mList.get(pos).user;
        DialogHelper.showRateDialog(getContext(), userItem.getFull_name(), mList.get(pos).trip.date, (rate, comment, dialog) -> {
            userItem.rating = Double.valueOf(rate);
            userItem.feedback = comment;
            userItem.is_rated = true;
            mAdp.notifyDataSetChanged();
        });
    }

    private String getRatedList() {
        JSONArray jsonArray = new JSONArray();

        for (int a = 0; a < mList.size(); a++) {
            UserItem user = mList.get(a).user;
            user.trip_id = mList.get(a).trip.trip_id;

            if (user.rating == 0 || user.rating == null) {
                makeSnackbar("Please rate " + user.getFull_name());
                return null;
            } else {
                RateItem rateItem = new RateItem();
                rateItem.setUser_id(user.user_id);
                rateItem.setTrip_id(mList.get(a).trip.trip_id);
                rateItem.setRating(user.rating);
                rateItem.setFeedback(user.feedback);
                jsonArray.put(rateItem.toJsonObject());
            }
        }
        return jsonArray.toString();
    }

    private void rateDrivers(String list) {

        getActivityViewModel().rateDrivers(list).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    RateFragment rateFragment = (RateFragment) getFragmentActivity().getCurrentFragment();
                    if (rateFragment.getAdapterSize() > 1)
                        rateFragment.removeFragment(this);
                    else {
                        getUserItem().has_pending_ratings = false;
                        PreferencesManager.putObject(AppConstants.KEY_USER, getUserItem());
                        getFragmentActivity().actionBack();
                    }
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
        String list = getRatedList();
        if (list != null)
            rateDrivers(list);
    }
}
