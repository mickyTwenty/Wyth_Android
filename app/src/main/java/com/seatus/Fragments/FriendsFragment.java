package com.seatus.Fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.seatus.Activities.MainActivity;
import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Holders.FriendListItemHolder;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.database.AppDatabase;
import com.seatus.ViewModels.Resource;
import com.seatus.Views.TitleBar;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by rohail on 27-Oct-17.
 */

public class FriendsFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    EfficientRecyclerAdapter mAdp;
    ArrayList<UserItem> mList = new ArrayList<>();

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

        getActivityViewModel().getFriendsDataBothSources().observe(this, userRessourse -> {
            switch (userRessourse.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    if (userRessourse.data.size() > 0) {
                        mList.clear();
                        mList.addAll(userRessourse.data);
                        mAdp.notifyDataSetChanged();
                    } else
                        makeSnackbar("No Friends Found");
                    hideLoader();
                    break;
                case error:
                    hideLoader();
                    break;
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_friends;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("friends list").enableBack().enableRightButton(R.drawable.actionbar_sync, view1 -> {
            showLoader();
            ((MainActivity) getContext()).initateFriendSyncProcess(false, () -> {
                hideLoader();
                getActivityViewModel().refreshFriendDataRoom();
            });
        });
    }

    @Override
    public void inits() {
        StaticMethods.initVerticalRecycler(getContext(), recyclerView);
        mAdp = new EfficientRecyclerAdapter(R.layout.item_friends_list, FriendListItemHolder.class, mList);
        recyclerView.setAdapter(mAdp);
    }

    @Override
    public void setEvents() {

    }
}
