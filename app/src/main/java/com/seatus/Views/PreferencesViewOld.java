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

public class PreferencesViewOld extends LinearLayout {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private EfficientRecyclerAdapter adapter;
    private ArrayList<SearchPreferenceItem> list;

    public PreferencesViewOld(Context context) {
        super(context);
        initUi();
    }

    public PreferencesViewOld(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_preference, this);
        ButterKnife.bind(this);

        StaticMethods.initHorizontalRecycler(getContext(), recyclerview);
        recyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
    }

    public void initData(String prefJson) {

        if (list != null)
            list.clear();

        if (TextUtils.isEmpty(prefJson)) {
            list = AppStore.getInstance().getPrefsList();
            if (list == null) {
                FragmentActivity activity = (FragmentActivity) getContext();
                ViewModelProviders.of(activity).get(ActivityViewModel.class).bootMeUp(getContext()).observe(activity, webResponseResource -> {
                    if (webResponseResource.status == Resource.Status.success)
                        initData(null);
                });
            }
        } else {
            AppStore.getInstance().getPrefsList().clear();
            AppStore.getInstance().getPrefsList().addAll(StaticMethods.getArrayListFromJson(new Gson(), prefJson, SearchPreferenceItem.class));
            list = AppStore.getInstance().getPrefsList();
        }

        if (list != null) {
            if (adapter == null) {
                adapter = new EfficientRecyclerAdapter<>(R.layout.item_post_trip, PostTripHolder.class, list);
                recyclerview.setAdapter(adapter);
            } else
                adapter.notifyDataSetChanged();
        }
    }

    public String getPrefJson() {
        return new Gson().toJson(list);
    }
}
