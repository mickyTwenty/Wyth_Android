package com.seatus.Views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.seatus.Holders.PostTripHolder;
import com.seatus.Holders.PreferenceItemHolder;
import com.seatus.Models.SearchPreferenceItem;
import com.seatus.R;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.StaticMethods;
import com.seatus.ViewModels.ActivityViewModel;
import com.seatus.ViewModels.Resource;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rah on 17-Nov-17.
 */

public class PreferencesView extends LinearLayout {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private EfficientRecyclerAdapter adapter;
    private ArrayList<SearchPreferenceItem> list;

    public PreferencesView(Context context) {
        super(context);
        initUi();
    }

    public PreferencesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_preference_new, this);
        ButterKnife.bind(this);

        StaticMethods.initVerticalRecycler(getContext(), recyclerview);
    }

    public void initData() {

        if (list != null)
            list.clear();

        list = AppStore.getInstance().getPrefsList();
        if (list == null) {
            FragmentActivity activity = (FragmentActivity) getContext();
            ViewModelProviders.of(activity).get(ActivityViewModel.class).bootMeUp(getContext()).observe(activity, webResponseResource -> {
                if (webResponseResource.status == Resource.Status.success)
                    initData();
            });
        }

        if (list != null) {
            if (adapter == null) {
                adapter = new EfficientRecyclerAdapter<>(R.layout.item_post_trip_new, PreferenceItemHolder.class, list);
                recyclerview.setAdapter(adapter);
            } else
                adapter.notifyDataSetChanged();
        }
    }

    public String getPrefJson() {
        return new Gson().toJson(list);
    }
}
