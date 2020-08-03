package com.seatus.BaseClasses;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seatus.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rah on 07-Dec-17.
 */

public abstract class BaseStatesFragment extends BaseFragment {

    @BindView(R.id.layout_connection_error)
    LinearLayout layoutConnectionError;
    @BindView(R.id.layout_loading)
    LinearLayout layoutLoading;
    @BindView(R.id.btn_retry)
    TextView btnRetry;

    ViewStub layoutContent;


    @Override
    protected int getLayout() {
        return R.layout.fragment_states;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayout(), container, false);
        layoutContent = view.findViewById(R.id.view_stub);
        layoutContent.setLayoutResource(getStubLayout());
        layoutContent.inflate();
        unbinder = ButterKnife.bind(this, view);
        btnRetry.setOnClickListener(v -> onRetryClicked());
        return view;
    }

    protected abstract void onRetryClicked();

    protected abstract int getStubLayout();


    public void setContentType(ContentType type) {
        switch (type) {
            case error:
                layoutContent.setVisibility(View.GONE);
                layoutConnectionError.setVisibility(View.VISIBLE);
                layoutLoading.setVisibility(View.GONE);
                break;
            case loading:
                layoutContent.setVisibility(View.GONE);
                layoutConnectionError.setVisibility(View.GONE);
                layoutLoading.setVisibility(View.VISIBLE);
                break;
            case content:
                layoutContent.setVisibility(View.VISIBLE);
                layoutConnectionError.setVisibility(View.GONE);
                layoutLoading.setVisibility(View.GONE);
                break;
        }
    }


    public enum ContentType {
        error,
        loading,
        content
    }

}
