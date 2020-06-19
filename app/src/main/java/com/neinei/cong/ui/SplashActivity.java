package com.neinei.cong.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.bean.ConfigBean;
import com.neinei.cong.bean.MobileBean;
import com.neinei.cong.bean.NovelTermBean;
import com.neinei.cong.bean.PayDialogBean;
import com.neinei.cong.bean.QqBean;
import com.neinei.cong.bean.UserBean;
import com.neinei.cong.glide.GlideApp;
import com.neinei.cong.ui.adapter.DiamondPayAdapter;
import com.neinei.cong.ui.login.LoginActivity;
import com.neinei.cong.utils.LoginUtils;
import com.neinei.cong.utils.SharePrefUtil;

import java.io.IOException;
import java.sql.Time;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

public class SplashActivity extends BaseActivity {

    private Dialog dialog;

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected Object getIdOrView() {
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideApp.with(this).load(R.mipmap.splash)
                .into(imageView);
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {

        getParams(this);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        getNovelTerm();
        getNovel();
        login();
    }

    private void getParams(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            AppConfig.INVITE_CODE    = String.valueOf(appInfo.metaData.get("channel"));
            AppConfig.MAIN_URL = String.valueOf(appInfo.metaData.get("htUrl"));
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }


    private void splah() {
        Log.i("KKK","开始跳转");
//        new Thread(() -> {
//
//            runOnUiThread(() -> {
//                ActivityUtils.startActivity(com.neinei.cong.ui.HomeActivity.class);
//                finish();
//            });
//        }).start();

        Timer timers = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("KKK","执行跳转");
                runOnUiThread(() -> {
                    ActivityUtils.startActivity(com.neinei.cong.ui.HomeActivity.class);
                    finish();
                });
            }
        };
        timers.schedule(timerTask,5000);
    }

    @SuppressLint("CheckResult")
    private void getQq() {
        if (AppContext.getInstance().getLoginUid() == null || AppContext.getInstance().getLoginUid().equals("0"))
            return;
        RetrofitClient.getInstance().createApi().getQq("User.getqq", AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
                .subscribe(qqBean -> {
                    if (qqBean.getData().getCode() == 0) {
                        AppConfig.QQ = qqBean.getData().getInfo().get(0).getQq();
                        AppConfig.YUE = qqBean.getData().getInfo().get(0).getYueka_url();
                        AppConfig.JI = qqBean.getData().getInfo().get(0).getJika_url();
                        AppConfig.YEAR = qqBean.getData().getInfo().get(0).getNianka_url();
                        AppConfig.FOREVER = qqBean.getData().getInfo().get(0).getZhongshenka_url();
                        AppConfig.CODE = qqBean.getData().getInfo().get(0).getInvitation_code();
                    }
                });
    }

    private void getConfig() {
        RetrofitClient.getInstance().createApi().getConfig("Home.getConfig")
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<ConfigBean>>) new BaseListObserver<ConfigBean>() {
                    @Override
                    protected void onHandleSuccess(List<ConfigBean> list)  {
                        if (list.size() > 0) {
                            ConfigBean bean = list.get(0);
                            AppConfig.TICK_NAME = bean.name_votes;
                            AppConfig.CURRENCY_NAME = bean.name_coin;
                            if (!bean.enter_tip_level.isEmpty()) {
                                AppConfig.JOIN_ROOM_ANIMATION_LEVEL = Integer.parseInt(bean.enter_tip_level);
                            } else {
                                AppConfig.JOIN_ROOM_ANIMATION_LEVEL = 0;
                            }
                            AppConfig.ROOM_CHARGE_SWITCH = Integer.parseInt(bean.live_cha_switch);
                            AppConfig.ROOM_PASSWORD_SWITCH = Integer.parseInt(bean.live_pri_switch);
                            AppConfig.ROOM_TIME_SWITCH = Integer.parseInt(bean.live_tim_switch);

                            AppContext.channel_type = bean.channl_type;

                            if (bean.channl_type == 1) {//主要采集器
                                AppContext.channelUrl = bean.channl_url;
                                AppContext.channelDataUrl = bean.zhubo_url;
                            } else {
                                AppContext.channelUrl = bean.channl_url2;
                                AppContext.channelDataUrl = bean.zhubo_url2;
                            }
                            if (!TextUtils.isEmpty(AppContext.channelUrl)) {
                                SharePrefUtil.putString("url", AppContext.channelUrl);
                                SharePrefUtil.putString("dataurl", AppContext.channelDataUrl);
                            }

                            SharePrefUtil.putInt("type", bean.channl_type);
                            AppContext.text = bean.keyword;
                            AppConfig.APP_ANDROID_SHARE = bean.app_android;
                            AppConfig.SHARE_TITLE = bean.share_title;
                            AppConfig.SHARE_DES = bean.share_des;
                            AppConfig.MAINTAIN_SWITCC = Integer.parseInt(bean.maintain_switch);
                            AppConfig.apk_ver = bean.apk_ver;
                            AppConfig.apk_url = bean.apk_url;
                            AppConfig.maintain_tips = bean.maintain_tips;
                            AppConfig.VIDEO_URL = bean.video_url;
                            AppConfig.vip_video_jiexi = bean.vip_video_jiexi;
                            AppConfig.jingcai_jiexi = bean.jingcai_jiexi;
                            AppConfig.video_vip_url = bean.video_vip_url;
                            AppConfig.pay_type = bean.pay_type;
                            AppConfig.yueka = bean.yueka;
                            AppConfig.jika = bean.jika;
                            AppConfig.nianka = bean.nianka;
                            AppConfig.zhongshenka = bean.zhongshenka;
                            AppConfig.free_time = bean.free_time;
                            AppConfig.is_cam = bean.is_cam;
//                            Log.e("asdadasdfasdfawew",bean.toString());
                            SharePrefUtil.putString("video_url", bean.video_url);
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    public  void login() {
        RetrofitClient.getInstance().createApi().getIsVip("Home.mobilecode", LoginUtils.getDeviceId(this), AppConfig.INVITE_CODE).compose(RxUtils.io_main())
                .subscribe(mobileBean -> {
                    if (mobileBean.getRet() == 200) {
                        if (mobileBean.getData().getCode() == 0) {
                            UserBean bean = new UserBean();
                            bean.token = LoginUtils.getDeviceId(this);
                            bean.id = mobileBean.getData().getInfo().getUid();
                            AppContext.getInstance().saveUserInfo(bean);
                            splah();
                            getQq();
                            getConfig();
                        } else {
                            ToastUtils.showLong(mobileBean.getData().getMsg());
                            payDialog();
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void login2() {
        RetrofitClient.getInstance().createApi().getIsVip("Home.mobilecode", LoginUtils.getDeviceId(this), AppConfig.INVITE_CODE)
                .compose(RxUtils.io_main())
                .subscribe(mobileBean -> {
                    if (mobileBean.getRet() == 200) {
                        ToastUtils.showShort("激活中...");
                        if (mobileBean.getData().getCode() == 0) {
                            UserBean bean = new UserBean();
                            bean.token = LoginUtils.getDeviceId(this);
                            bean.id = mobileBean.getData().getInfo().getUid();
                            AppContext.getInstance().saveUserInfo(bean);
                            splah();
                            getQq();
                            getConfig();
                        } else {
                            ToastUtils.showLong(mobileBean.getData().getMsg());
                            payDialog();
                        }
                    }
                });
    }

    //获取小说分类
    private void getNovelTerm() {
        RetrofitClient.getInstance().createApi().novelTerm("Home.novelTerm").compose(RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<NovelTermBean>>) new BaseListObserver<NovelTermBean>() {
                    @Override
                    protected void onHandleSuccess(List<NovelTermBean> list) {
                        if (list == null || list.size() == 0) return;
                        AppContext.novelTermList.clear();
                        AppContext.novelTermList.addAll(list);
                    }
                });
    }

    //获取小说分类
    private void getNovel() {
        RetrofitClient.getInstance().createApi().novelTerm("Home.novelClassify").compose(RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<NovelTermBean>>) new BaseListObserver<NovelTermBean>() {
                    @Override
                    protected void onHandleSuccess(List<NovelTermBean> list) {
                        if (list == null || list.size() == 0) return;
                        AppContext.novelClassify.clear();
                        AppContext.novelClassify.addAll(list);
                    }
                });
    }


    public void payDialog() {
        View view = View.inflate(this, R.layout.dialog_code, null);
        EditText etCode = view.findViewById(R.id.et_code);
        dialog = new Dialog(this, R.style.wx_dialog);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();

        view.findViewById(R.id.tv_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = etCode.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    ToastUtils.showShort("请输入激活码");
                    return;
                }
                dialog.dismiss();
                AppConfig.INVITE_CODE = s;
                login2();
            }
        });

    }


}
