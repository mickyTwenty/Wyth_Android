package com.seatus.Views;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seatus.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rohail on 20-Oct-17.
 */

public class TitleBar extends RelativeLayout {

    Context context;

    @BindView(R.id.btn_menu)
    ImageView btnMenu;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.btn_info)
    ImageView btnInfo;
    @BindView(R.id.btn_right)
    ImageView btnRight;

    @BindView(R.id.btn_driver)
    ImageView btnDriver;

    public String sub_link;

    private DrawerLayout drawerLayout;

    public TitleBar(Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_titlebar, this);
        ButterKnife.bind(this);
    }

    public TitleBar setTitle(String title) {
        txtTitle.setText(title);
        return this;
    }

    public TitleBar enableMenu() {
        btnBack.setVisibility(GONE);
        btnMenu.setVisibility(VISIBLE);
        if (drawerLayout != null)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        return this;
    }

    public TitleBar enableBack() {
        btnMenu.setVisibility(GONE);
        btnBack.setVisibility(VISIBLE);
        if (drawerLayout != null)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        return this;
    }

    public TitleBar enableInfo(String sub_link) {
        btnInfo.setVisibility(VISIBLE);
        this.sub_link = sub_link;
        return this;
    }

    public TitleBar enableRightButton(@DrawableRes int drawableRes, View.OnClickListener listener) {
        btnRight.setVisibility(VISIBLE);
        btnRight.setImageResource(drawableRes);
        btnRight.setOnClickListener(listener);
        return this;
    }

    public TitleBar resetTitleBar() {
        btnRight.setVisibility(GONE);
        btnInfo.setVisibility(GONE);
        btnRight.setOnClickListener(null);
        return this;
    }

    public void enableDriver(boolean isDriver) {
        btnDriver.setVisibility(isDriver ? VISIBLE : GONE);
    }

    public void setMenuListener(OnClickListener iface) {
        btnMenu.setOnClickListener(iface);
    }

    public void setInfoListener(OnClickListener iface) {
        btnInfo.setOnClickListener(iface);
    }

    public void setBackListener(OnClickListener iface) {
        btnBack.setOnClickListener(iface);
    }

    public void setDrawer(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }
}
