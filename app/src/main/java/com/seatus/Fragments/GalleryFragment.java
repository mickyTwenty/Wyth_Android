package com.seatus.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.seatus.BaseClasses.BaseFragment;
import com.seatus.Models.DocumentItem;
import com.seatus.R;
import com.seatus.Views.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

/**
 * Created by rohail on 31-Oct-17.
 */

public class GalleryFragment extends BaseFragment {

    @BindView(R.id.slider)
    BannerSlider slider;

    List<Banner> list = new ArrayList<>();

    public static GalleryFragment newInstance(ArrayList<DocumentItem> driver_documents) {
        GalleryFragment frag = new GalleryFragment();
        for (DocumentItem item : driver_documents)
            frag.list.add(new RemoteBanner(item.absolute_url));
        return frag;
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        slider.setBanners(list);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_gallery;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.setTitle("documents").enableBack();
    }

    @Override
    public void inits() {

    }

    @Override
    public void setEvents() {

    }
}
