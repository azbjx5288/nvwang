package com.neinei.cong.ui;

import android.app.ProgressDialog;
import android.webkit.WebView;

import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseObjObserver;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.bean.BookBean;
import com.neinei.cong.bean.NovieceBean;
import com.neinei.cong.utils.StatusBarUtil;

public class ReadingActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_reading;
    }

    @Override
    protected void initView() {
        webView = findViewById(R.id.webview);
        setToolbar("新手必看",true);
        noviece();
    }

    // 绘制HTML
    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }


    private void noviece(){
        RetrofitClient.getInstance().createApi().noviece("Home.noviece")
                .compose(RxUtils.io_main())
                .subscribe(new BaseObjObserver<NovieceBean>() {
                    @Override
                    protected void onHandleSuccess(NovieceBean bean) {
                        webView.loadData(getHtmlData(bean.getContent()), "text/html; charset=utf-8", "utf-8");
                    }
                });
    }

}
