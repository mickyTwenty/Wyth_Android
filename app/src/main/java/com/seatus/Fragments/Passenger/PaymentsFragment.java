package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.seatus.Adapters.MyTripViewPagerAdapter;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.Help;
import com.seatus.Views.TitleBar;
import com.seatus.Views.ZoomOutPageTransformer;

import butterknife.BindView;

/**
 * Created by saqib on 1/24/2018.
 */

public class PaymentsFragment extends BaseFragment {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    MyTripViewPagerAdapter adapter;

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_payments;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("My Payments").enableMenu().enableInfo(Help.Payments);
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
        if (getUserItem().getUser_type() == UserItem.UserType.driver) {
            adapter.addFragment(new MyPaymentsFragment(), "Payment Info");
            adapter.addFragment(new BankInfoFragment(), "Bank Info");
            adapter.addFragment(new PaymentHistoryFragment(), "Payment History");

        } else {
            adapter.addFragment(new MyPaymentsFragment(), "My Payments");
            adapter.addFragment(new PaymentHistoryFragment(), "Payments History");
        }
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(adapter);
    }

    public Fragment getViewpageFragment(int index) {
        return adapter.getItem(index);
    }
}
