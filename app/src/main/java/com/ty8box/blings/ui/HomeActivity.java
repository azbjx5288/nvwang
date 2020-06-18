package com.neinei.cong.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.bean.PayDialogBean;
import com.neinei.cong.bean.TextAdBean;
import com.neinei.cong.bean.UserInfoBean;
import com.neinei.cong.ui.adapter.ViewPagerAdapter;
import com.neinei.cong.ui.fragment.DiamondFragment;
import com.neinei.cong.ui.fragment.HomeFragment;
import com.neinei.cong.ui.fragment.MineFragment;
import com.neinei.cong.utils.DialogHelp;
import com.neinei.cong.utils.UpdateManager;
import com.neinei.cong.view.AlphaTabView;
import com.neinei.cong.view.AlphaTabsIndicator;
import com.neinei.cong.view.OnTabChangedListner;
import com.neinei.cong.widget.NoScrollViewPager;
import com.ty8box.blings.ui.newfragment.AvFragment;
import com.ty8box.blings.ui.newfragmnet.DiamondNorvelFragment;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;

public class HomeActivity extends BaseActivity implements OnTabChangedListner {

    NoScrollViewPager viewPager;
    AlphaTabsIndicator mAlphaTabsIndicator;
    AlphaTabView tabMine;

    private List<Fragment> mList;
    private ViewPagerAdapter adapter;
    private long firstTime = 0;

    private final static String[] AUTH_BASE_ARR =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA
                    , Manifest.permission.READ_PHONE_STATE};
    private final static int AUTH_BASE_REQUEST_CODE = 1;
    private final static int AUTH_COM_REQUEST_CODE = 2;
    private int current = 0;
    private SimpleDateFormat format;

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected Object getIdOrView() {
        FontInit();
        return R.layout.activity_home;
    }

    //字体
    private void FontInit(){
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/fang.ttf");

        try {
            Field field = Typeface.class.getDeclaredField("MONOSPACE");
            field.setAccessible(true);
            field.set(null,typeface);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void initView() {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        viewPager = findViewById(R.id.view_pager);
        mAlphaTabsIndicator = findViewById(R.id.alphaIndicator);
        tabMine = findViewById(R.id.tab_mine);
    }

    @Override
    public void initData() {
        viewPager = findViewById(R.id.view_pager);
        mAlphaTabsIndicator = findViewById(R.id.alphaIndicator);
        viewPager.setOffscreenPageLimit(4);
        mList = new ArrayList<>();

        mList.add(new HomeFragment());
        mList.add(new DiamondNorvelFragment());
        mList.add(new AvFragment());
        mList.add(new MineFragment());
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), this, mList);
        viewPager.setAdapter(adapter);

        mAlphaTabsIndicator.setViewPager(viewPager);
        mAlphaTabsIndicator.setOnTabChangedListner(this);
        mAlphaTabsIndicator.removeAllBadge();

        String type = getIntent().getStringExtra("type");
        if (!TextUtils.isEmpty(type) && type.equals("message")) {
            if (viewPager == null || mAlphaTabsIndicator == null) {
                return;
            }
            viewPager.setCurrentItem(1);
            mAlphaTabsIndicator.setTabCurrenItem(1);
        }
        init();
        check();
        getChargeRule();
        getAvChargeRule();
        getCoinChargeRule();
        getTextAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMember();
    }

    @Override
    public void onTabSelected(int tabNum) {
        viewPager.setCurrentItem(tabNum);
        current = tabNum;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra("exit", false);
            if (isExit) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstTime > 2000) {
            ToastUtils.showShort("再按一次退出程序");
            firstTime = System.currentTimeMillis();
        } else {
            //退出系统
            RetrofitClient.getInstance().createApi().signOut("Home.signout",AppContext.getInstance().getLoginUid()).compose(RxUtils.io_main())
                    .subscribe(mobileBean -> {
                        if (mobileBean.getRet() == 200) {
                            if (mobileBean.getData().getCode() == 0) {
                                finish();
                                System.exit(0);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUTH_BASE_REQUEST_CODE) {
            for (int ret : grantResults) {
                Log.d("GuideActivity", "ret:" + ret);
                if (ret == 0) {
                    continue;
                } else {
//                    ToastUtils.showShort("缺少导航基本的权限");
                    return;
                }
            }
            init();
        } else if (requestCode == AUTH_COM_REQUEST_CODE) {
            ToastUtils.showShort("初始化完毕");
        }
    }

    private void init() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(AUTH_BASE_ARR, AUTH_BASE_REQUEST_CODE);
                return;
            }
        }
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : AUTH_BASE_ARR) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void check() {
        if (AppConfig.MAINTAIN_SWITCC == 1) {
            String maintain_tips = AppConfig.maintain_tips;
            try {
                DialogHelp.showMainTainDialog(HomeActivity.this, maintain_tips);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new UpdateManager(HomeActivity.this, AppConfig.apk_ver, AppConfig.apk_url, false).checkUpdate();
    }

    //是否为会员
    private void isMember() {
        String loginUid = AppContext.getInstance().getLoginUid();
        if (loginUid.equals("0")) return;
        RetrofitClient.getInstance().createApi().getBaseUserInfo("User.getBaseInfo", AppContext.getInstance().getToken(), AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<UserInfoBean>>) new BaseListObserver<UserInfoBean>() {
                    @Override
                    protected void onHandleSuccess(List<UserInfoBean> list) {
                        if (list.size() > 0) {
                            UserInfoBean bean = list.get(0);
                            AppConfig.IS_MEMBER = (bean.is_member == 1);
                            AppConfig.AVMEMBER = (bean.is_av_member == 1);

                        }
                    }
                });
    }

    private void getChargeRule() {
        RetrofitClient.getInstance().createApi().getChargeRule("Charge.getChargeRule")
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<PayDialogBean>>) new BaseListObserver<PayDialogBean>() {
                    @Override
                    protected void onHandleSuccess(List<PayDialogBean> list) {
                        if (list == null || list.size() == 0) return;
                        AppContext.zbChargeList.clear();
                        AppContext.zbChargeList.addAll(list);
                    }
                });
    }

    private void getAvChargeRule() {
        RetrofitClient.getInstance().createApi().getChargeRule("Charge.getAvChargeRule")
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<PayDialogBean>>) new BaseListObserver<PayDialogBean>() {
                    @Override
                    protected void onHandleSuccess(List<PayDialogBean> list) {
                        if (list == null || list.size() == 0) return;
                        AppContext.avChargeList.clear();
                        AppContext.avChargeList.addAll(list);
                    }
                });
    }

    private void getCoinChargeRule() {
        RetrofitClient.getInstance().createApi().getChargeRule("Charge.getCoinChargeRule")
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<PayDialogBean>>) new BaseListObserver<PayDialogBean>() {
                    @Override
                    protected void onHandleSuccess(List<PayDialogBean> list) {
                        if (list == null || list.size() == 0) return;
                        AppContext.zsChargeList.clear();
                        AppContext.zsChargeList.addAll(list);
                    }
                });
    }

    private void getTextAd() {
        RetrofitClient.getInstance().createApi().textAd("Home.avList")
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<TextAdBean>>) new BaseListObserver<TextAdBean>() {
                    @Override
                    protected void onHandleSuccess(List<TextAdBean> list) {
                        if(list.size()>0){
                            AppContext.textAdBean = list.get(0);
                        }

                    }
                });
    }
}
