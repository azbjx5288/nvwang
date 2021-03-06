package com.neinei.cong.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.neinei.cong.AppConfig;
import com.neinei.cong.R;

public class VideoUrlActivity extends AppCompatActivity {

    private WebView webView;
    private String playUrl;
    private ProgressBar pbProgress;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected void initView() {
        tvTitle = findViewById(R.id.tv_title);
        pbProgress = findViewById(R.id.pb_progress);
        webView = findViewById(R.id.webview);
        tvTitle.setText("精彩视频");

        findViewById(R.id.bofang).setVisibility(View.GONE);

        WebSettings localWebSettings = webView.getSettings();
        localWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        localWebSettings.setJavaScriptEnabled(true);
        localWebSettings.setSupportZoom(true);
        localWebSettings.setDefaultTextEncodingName("utf-8");
        localWebSettings.setLoadWithOverviewMode(true);
        localWebSettings.setAppCacheEnabled(true);
        localWebSettings.setDomStorageEnabled(true);
        localWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        localWebSettings.setBuiltInZoomControls(true);
        localWebSettings.setPluginState(WebSettings.PluginState.ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)//图片不显示
            localWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.loadUrl(AppConfig.jingcai_jiexi);
        webView.setWebViewClient(new myWebClient());
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    pbProgress.setVisibility(View.GONE);
                } else {
                    // 加载中
                    pbProgress.setVisibility(View.VISIBLE);
                    pbProgress.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }


        });
    }



    public class myWebClient extends WebViewClient {


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            WebView.HitTestResult hitTestResult = view.getHitTestResult();

            if (!TextUtils.isEmpty(url) && hitTestResult == null) {
                view.loadUrl(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            webView.stopLoading();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            playUrl = url;
            super.onPageFinished(view, url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
