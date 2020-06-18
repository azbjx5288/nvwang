package com.neinei.cong.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.neinei.cong.R;
import com.neinei.cong.utils.StatusBarUtil;

//
//                          _oo0oo_
//                         o8888888o
//                          88" . "88
//                          (| -_- |)
//                          0\  =  /0
//                      ___/`---'\___
//                      .' \\|     |// '.
//                   / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//                  |   | \\\  -  /// |   |
//                  | \_|  ''\---/''  |_/ |
//                  \  .-\__  '-'  ___/-. /
//               ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//          \  \ `_.   \_ __\ /__ _/   .-` /  /
//=====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永无BUG
public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    public Toolbar mToolbar;
    private TextView titleView;
    protected Context mContext;
    protected View rootView;

    protected abstract boolean hasActionBar();

    protected abstract Object getIdOrView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mContext = this;
        StatusBarUtil.getInstance().immersive(this);
        View view;
        if (getIdOrView() instanceof Integer) {
            view = View.inflate(this, (Integer) getIdOrView(), null);
        } else if (getIdOrView() instanceof View) {
            view = (View) getIdOrView();
        } else {
            throw new ClassCastException("getIdOrView only be int or View");
        }
        if (hasActionBar()) {
            LinearLayout root = (LinearLayout) View.inflate(this, R.layout.layout_root, null);
            root.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mToolbar = root.findViewById(R.id.toolbar);
            mToolbar.setBackgroundResource(R.mipmap.toorbar_bg);
            mToolbar.setTitle("");
            titleView = root.findViewById(R.id.toolbar_title);
            titleView.setTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(mToolbar);
//            StatusBarUtil.getInstance().setPaddingSmart(this, mToolbar);
            setContentView(root);
        } else {
            setContentView(view);
        }
        initView();
        initData();
        setListener();
    }



    protected void setListener() {
    }

    protected void initData() {
    }

    protected void initView() {
    }

    public void setToolbar(String title) {
        setToolbar(title, false);
    }

    public Toolbar getToolbar() {
        if (hasActionBar()) {
            return mToolbar;
        } else {
            throw new NullPointerException("you should set hasActionbar = true");
        }
    }

    public void setToolbar(String title, boolean needBack) {
        setToolbar(title, needBack, 0);
    }


    @SuppressLint("RestrictedApi")
    public void setToolbar(String title, boolean needBack, @DrawableRes int icon) {
        if (!hasActionBar()) {
            return;
        }
        if (needBack) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            if (icon == 0) {
                mToolbar.setNavigationIcon(R.mipmap.icon_back);
            } else {
                mToolbar.setNavigationIcon(icon);
            }
            mToolbar.setNavigationOnClickListener(v -> finish());
        }
        titleView.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
