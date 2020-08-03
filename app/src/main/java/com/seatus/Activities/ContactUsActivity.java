package com.seatus.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.seatus.BaseClasses.BaseActivity;
import com.seatus.R;
import com.seatus.Utils.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohail on 6/14/2018.
 */

public class ContactUsActivity extends BaseActivity {

    private static final String PARAM_LINK = "param_link";
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.btn_close)
    ImageView btnClose;

    public static void launchInstance(Context context, String link) {
        Intent intent = new Intent(context, ContactUsActivity.class);
        intent.putExtra(PARAM_LINK, link);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);

        String url = generateUrl();
        if (TextUtils.isEmpty(url)) {
            makeToast("Invalid URL");
            finish();
        } else
            webview.loadUrl(url);

        webview.getSettings().setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
                webview.setVisibility(View.VISIBLE);
            }
        });

    }

    private String generateUrl() {
        try {
            String link = getIntent().getStringExtra(PARAM_LINK);
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @OnClick(R.id.btn_close)
    public void onViewClicked() {
        finish();
    }
}
