package com.neinei.cong.module.book;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.umeng.commonsdk.debug.E;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseObjObserver;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.bean.BookBean;
import com.neinei.cong.utils.StatusBarUtil;

import java.io.IOException;
import java.io.InputStream;

public class BookDetailActivity extends AppCompatActivity {

    private android.webkit.WebView mWebview;
    private String content;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        initView();
        findViewById(R.id.img_back).setOnClickListener(view -> finish());
    }


    protected void initView() {
        try {
            mWebview = findViewById(R.id.webview);
            mWebview.setBackgroundColor(Color.parseColor("#1F282D"));
            WebSettings settings = mWebview.getSettings();
            settings.setTextZoom(70);
            tvTitle = findViewById(R.id.tv_title);
//            StatusBarUtil.getInstance().setPaddingSmart(this, mWebview);
            Bundle extras = getIntent().getExtras();
            String name = extras.getString("name");
            tvTitle.setText(name);

            content = extras.getString("content");

            String linkCss = "<style type=\"text/css\"> " +
                    "img {" +
                    "width:100%;" +
                    "height:auto;" +
                    "}" +
                    "</style>";
            String html = "\n<html><header>" + linkCss + "</header><body style=\"black;background:#ffffff\">" + content + "</body></html>";
            mWebview.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        } catch (Exception e) {

        }
        mWebview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


                view.loadUrl("javascript:!function(){" +
                        "s=document.createElement('style');s.innerHTML="
                        + "\"@font-face{font-family:MyCustomFont;src:url('****/myfont.ttf');}*"
                        +"{font-family:MyCustomFont !important;}body{color:#000000}\";"
                        + "document.getElementsByTagName('head')[0].appendChild(s);" +
                        "document.getElementsByTagName('body')[0].style.fontFamily = \"MyCustomFont\";}()"
                );

            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse response = super.shouldInterceptRequest(view, url);

                if (url != null && url.contains("myfont.ttf")) {
                    String assertPath ="fonts/myfont.ttf";
//                    String assertPath = url.substring(url.indexOf("**injection**/") + "**injection**/".length(), url.length());
                    try {
                        response = new WebResourceResponse("application/x-font-ttf","UTF8", getAssets().open(assertPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return response;
            }

        });

    }


}
