package com.seatus.Fragments.Passenger;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Holders.MyPaymentsHolder;
import com.seatus.Models.CardItem;
import com.seatus.R;
import com.seatus.Utils.DateTimeHelper;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.Help;
import com.seatus.Utils.StaticMethods;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saqib on 1/24/2018.
 */

public class MyPaymentsFragment extends BaseFragment {
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private EfficientRecyclerAdapter<CardItem> mAdp;
    public ArrayList<CardItem> mList = new ArrayList<>();

    public boolean isRedirectable = false;


    public static MyPaymentsFragment newInstance(boolean isRedirectable) {
        MyPaymentsFragment fragment = new MyPaymentsFragment();
        fragment.isRedirectable = isRedirectable;
        return fragment;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_my_payments;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        if (isRedirectable)
            titleBar.setTitle("Add Card").enableBack();
        else
            titleBar.enableInfo(Help.Payments);
    }

    @Override
    public void inits() {
        StaticMethods.initVerticalRecycler(getContext(), recyclerView);
        mAdp = new EfficientRecyclerAdapter(R.layout.item_my_payments, MyPaymentsHolder.class, mList);
        recyclerView.setAdapter(mAdp);

        getCreditCards();
        refreshLayout.setOnRefreshListener(() -> getCreditCards());
    }

    @Override
    public void setEvents() {

    }

    @OnClick(R.id.txt_add_card)
    public void onViewClicked() {
        DialogHelper.addCardDialog(getContext(), (dialog, card, token) -> {
            addCreditCard(token.getId(), card.getLast4());
        });
    }

    public void removeCardDialog(int position) {
        new AlertDialog.Builder(getContext(), R.style.CustomDialog)
                .setTitle("Remove Card")
                .setMessage("Are you sure you want to remove card ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> removeCard(position))
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private void addCreditCard(String card_token, String last_digits) {
        getActivityViewModel().addCreditCard(card_token, last_digits).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    if (webResponseResource.data.body != null) {
                        mList.add(webResponseResource.data.body);
                        mAdp.notifyDataSetChanged();
                    } else
                        makeSnackbar(webResponseResource.data.message);
                    if (isRedirectable)
                        getFragmentActivity().actionBack();
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    public void removeCard(int position) {
        getActivityViewModel().removeCard(mList.get(position).id).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    if (webResponseResource.data.body != null) {
                        mList.remove(position);
                        mAdp.notifyDataSetChanged();
                    }
                    makeSnackbar(webResponseResource.data.message);
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    public void setDefaultCard(int position) {
        getActivityViewModel().setDefaultCard(mList.get(position).id).observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    if (webResponseResource.data.body != null) {

                        for (int a = 0; a < mList.size(); a++)
                            mList.get(a).is_default = false;

                        mList.get(position).is_default = true;
                        mAdp.notifyDataSetChanged();

                    }
                    makeSnackbar(webResponseResource.data.message);
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);

                    mList.get(position).is_default = false;
                    mAdp.notifyDataSetChanged();
                    break;
            }
        });
    }

    private void getCreditCards() {

        getActivityViewModel().getCreditCard().observe(this, webResponseResource -> {
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
                    if (mList.size() > 0)
                        mList.get(mList.size() - 1).isLast = true;
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
