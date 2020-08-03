package com.seatus.Fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.seatus.Adapters.MyTripViewPagerAdapter;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Fragments.Driver.HomePassengerRatingFragment;
import com.seatus.Fragments.Passenger.HomeDriverRatingFragment;
import com.seatus.Models.PendingRatingItem;
import com.seatus.R;
import com.seatus.Views.TitleBar;
import com.seatus.Views.ZoomOutPageTransformer;

import butterknife.BindView;

/**
 * Created by saqib on 2/8/2018.
 */

public class RateFragment extends BaseFragment {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    MyTripViewPagerAdapter adapter;
    PendingRatingItem item;

    public static RateFragment newInstance(PendingRatingItem item) {
        RateFragment fragment = new RateFragment();
        fragment.item = item;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_rate;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.enableMenu().setTitle("rate");
    }

    @Override
    public void inits() {
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void setEvents() {

    }

    private void setupViewPager() {
        adapter = new MyTripViewPagerAdapter(getChildFragmentManager());

        if (item.driver.size() > 0)
            adapter.addFragment(HomePassengerRatingFragment.newInstance(item.driver), "Passengers");
        if (item.passenger.size() > 0)
            adapter.addFragment(HomeDriverRatingFragment.newInstance(item.passenger), "Drivers");

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(adapter);
    }

    public Fragment getViewpageFragment(int index) {
        return adapter.getItem(index);
    }

    public void removeFragment(Fragment fragment) {
         adapter.removeFragment(fragment);
         viewPager.setAdapter(adapter);
    }

    public int getAdapterSize(){
        return adapter.getCount();
    }

}
