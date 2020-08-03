package com.seatus.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
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

public class HelpActivity extends BaseActivity {

    private static final String PARAM_ROLE = "param_role";
    private static final String PARAM_LINK = "param_link";
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.btn_close)
    ImageView btnClose;

    public static void launchInstance(Context context, String role, String link) {
        Intent intent = new Intent(context, HelpActivity.class);
        intent.putExtra(PARAM_ROLE, role);
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
            makeToast("Invalid Help URL");
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
            String role = getIntent().getStringExtra(PARAM_ROLE);
            String link = getIntent().getStringExtra(PARAM_LINK);
            String helpUrl = new StringBuilder().append(AppConstants.ServerUrl + "help/").append(role).append("#").append(link).toString();
            Log.e("HelpUrl", helpUrl);
            return helpUrl;
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
