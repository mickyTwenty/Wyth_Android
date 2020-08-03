package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Holders.PaymentHistoryHolder;
import com.seatus.Models.CardItem;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.Help;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by saqib on 1/24/2018.
 */

public class PaymentHistoryFragment extends BaseFragment {
    @BindView(R.id.txt_note)
    TextView note;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private EfficientRecyclerAdapter<CardItem> mAdp;
    public ArrayList<CardItem> mList = new ArrayList<>();

    public static UserItem.UserType userType;

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_payments_history;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.enableInfo(Help.Payments);
    }

    @Override
    public void inits() {

        StaticMethods.initVerticalRecycler(getContext(), true, recyclerview);

        userType = getUserItem().getCurrentInterface() ? UserItem.UserType.normal : UserItem.UserType.driver;
        mAdp = new EfficientRecyclerAdapter(R.layout.item_payments_history, PaymentHistoryHolder.class, mList);
        recyclerview.setAdapter(mAdp);
        getPaymentHistory();

        refreshLayout.setOnRefreshListener(() -> getPaymentHistory());

        if(userType == UserItem.UserType.driver)
            note.setVisibility(View.VISIBLE);
        else note.setVisibility(View.GONE);
    }

    @Override
    public void setEvents() {

    }

    private void getPaymentHistory() {

        getActivityViewModel().getPaymentHistory(getUserItem().getRoleString()).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    if (mList.isEmpty())
                        showLoader();
                    break;
                case success:
                    refreshLayout.setRefreshing(false);
                    hideLoader();
                    mList.clear();
                    mList.addAll(webResponseResource.data.body);
                    mAdp.notifyDataSetChanged();
                    break;
                default:
                    refreshLayout.setRefreshing(false);
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

}
