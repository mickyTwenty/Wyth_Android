package com.seatus.Fragments.Driver;

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
import com.seatus.Adapters.SearchRidersFilterableAdapter;
import com.seatus.BaseClasses.BaseLocalFiltersFragment;
import com.seatus.Dialogs.SeatUsDialog;
import com.seatus.Fragments.CreateTripFragment;
import com.seatus.Fragments.InviteFriendFragment;
import com.seatus.Fragments.RideDetailFragment;
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

public class SearchRidersFragment extends BaseLocalFiltersFragment {

    @BindView(R.id.recyclerview_passengers)
    RecyclerView recyclerView;
    @BindView(R.id.note)
    TextView note;

    public ArrayList<TripItem> listSearchResult;
    private SearchRidersFilterableAdapter mAdp;


    @Override
    protected int getLayout() {
        return R.layout.fragment_search_riders;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("Find Riders");
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        if (!isLoaded)
            getData();
    }

    @Override
    public void inits() {
        if (mAdp == null) {
            listSearchResult = new ArrayList<>();
        }
        StaticMethods.initVerticalRecycler(getContext(), true, recyclerView);
        mAdp = new SearchRidersFilterableAdapter(getContext(), getUserItem().getCurrentInterface(), listSearchResult);
        recyclerView.setAdapter(mAdp);
    }

    @Override
    public void setEvents() {
    }

    private void getData() {
        SearchFilterDataItem dataItem = getTempItem();
        LatLng origin = StaticMethods.parseLatLng(dataItem.origin_geo);
        LatLng destination = StaticMethods.parseLatLng(dataItem.destination_geo);

        getActivityViewModel().searchRiders(origin.latitude, origin.longitude, destination.latitude, destination.longitude).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    if (webResponseResource.data.body.size() > 0) {
                        note.setVisibility(View.GONE);
                        listSearchResult.clear();
                        listSearchResult.addAll(webResponseResource.data.body);
                        mAdp.notifyChange(listSearchResult);
                    } else {
                        note.setVisibility(View.VISIBLE);
                        note.setText(webResponseResource.data.message);
                    }
                    hideLoader();
                    break;
                default:
                    hideLoader();
                    note.setVisibility(View.VISIBLE);
                    note.setText("Connection Timeout!");
                    break;
            }
        });
    }

    @OnClick({R.id.btn_post_ride})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_post_ride:
                getFragmentActivity().replaceFragmentWithBackstack(new CreateTripFragment());
                break;
        }
    }
}
