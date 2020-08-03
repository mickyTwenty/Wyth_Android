package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.seatus.Adapters.MyTripViewPagerAdapter;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Models.TripItem;
import com.seatus.R;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.Help;
import com.seatus.Views.TitleBar;
import com.seatus.Views.ZoomOutPageTransformer;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by saqib on 11/21/2017.
 */

public class MyTripsFragment extends BaseFragment {
    private static String DRIVER_PAST_TRIPS = "driver/past";
    private static String PASSENGER_PAST_TRIPS = "passenger/past";
    private static String DRIVER_UPCOMING_TRIPS = "driver/upcoming";
    private static String PASSENGER_UPCOMING_TRIPS = "passenger/upcoming";

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    int index = 0;

    public static MyTripsFragment newInstance(int index) {

        MyTripsFragment fragment = new MyTripsFragment();
        fragment.index = index;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_my_trips;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.resetTitleBar().enableMenu().setTitle("My Trips").enableInfo(Help.MyTrips);
    }

    @Override
    public void inits() {
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(index).select();
    }

    @Override
    public void setEvents() {

    }

    private void setupViewPager() {
        MyTripViewPagerAdapter adapter = new MyTripViewPagerAdapter(getChildFragmentManager());
        if(getUserItem().getCurrentInterface()) {
            adapter.addFragment(new OffersListingFragment(), "Pending Offers");
            adapter.addFragment(TripsListingFragment.newInstance(PASSENGER_UPCOMING_TRIPS), "Upcoming Trips");
            adapter.addFragment(TripsListingFragment.newInstance(PASSENGER_PAST_TRIPS), "Past Trips");
        }else {
            adapter.addFragment(new OffersListingFragment(), "Pending Offers");
            adapter.addFragment(TripsListingFragment.newInstance(DRIVER_UPCOMING_TRIPS), "Upcoming Trips");
            adapter.addFragment(TripsListingFragment.newInstance(DRIVER_PAST_TRIPS), "Past Trips");
        }
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(adapter);
    }

    public enum MyTripEnum {
        Offers, PastTrip, UpComingTrip
    }

}
