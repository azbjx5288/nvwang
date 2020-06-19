package com.neinei.cong.module.video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.HttpArray;
import com.neinei.cong.api.http.HttpResult;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.bean.PayDialogBean;
import com.neinei.cong.bean.UserInfoBean;
import com.neinei.cong.bean.VideoListBean;
import com.neinei.cong.ui.AvDetailActivity;
import com.neinei.cong.ui.VideoDetailActivity;
import com.neinei.cong.utils.LoginUtils;
import com.neinei.cong.utils.PayUtils;
import com.neinei.cong.utils.PayUtils2;
import com.neinei.cong.widget.XzwzzPlayer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class VideoPlayActivity extends BaseActivity {

    private String type, title, url;
    private String[] textSplit;
    private String[] gifSplit;
    private TextView tips,tips2;
    private ImageView gif;
    private String id;
    private boolean vip = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                    toActivity();
            }
        }
    };

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_video_play;
    }


    @Override
    protected void initView() {
        super.initView();
        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");
        title = bundle.getString("title");
        url = bundle.getString("url");
        id = bundle.getString("id");
        VideoListBean bean = (VideoListBean) bundle.getSerializable("data");
        jzVideoPlayerStandard = findViewById(R.id.videoplayer);
        gif = findViewById(R.id.gif);
        tips = findViewById(R.id.tips);
        tips2 = findViewById(R.id.tips2);
        jzVideoPlayerStandard.fullscreenButton.setVisibility(View.INVISIBLE);
        jzVideoPlayerStandard.setUp(url, JZVideoPlayerStandard.NORMAL_ORIENTATION, title);
        jzVideoPlayerStandard.thumbImageView.setVisibility(View.GONE);
        jzVideoPlayerStandard.startVideo();

        initAd();
        jzVideoPlayerStandard.backButton.setOnClickListener(v -> finish());
        jzVideoPlayerStandard.titleTextView.setOnClickListener(v -> finish());
     //   tips.setOnClickListener(v -> toBrower(textSplit[1]));
        gif.setOnClickListener(v -> toBrower(gifSplit[1]));

        TimeCount timecount = new TimeCount(15000, 1000);
        timecount.start();
    }

    private void initAd() {
        try {
            if (type.equals("av")) {
                textSplit = AppContext.textAdBean.getAv_ad().replaceAll("，", ",").split(",");
                tips.setText(textSplit[0]);
                gifSplit = AppContext.textAdBean.getAv_gif().replaceAll("，", ",").split(",");
                Glide.with(this).load(gifSplit[0]).into(gif);
            } else {
                textSplit = AppContext.textAdBean.getCoin_ad().replaceAll("，", ",").split(",");
                tips.setText(textSplit[0]);

                gifSplit = AppContext.textAdBean.getCoin_gif().replaceAll("，", ",").split(",");
                Glide.with(this).load(gifSplit[0]).into(gif);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        addViewNum();
    }

    @SuppressLint("CheckResult")
    private void addViewNum() {
        RetrofitClient.getInstance().createApi().addViewNum("Home.addvideonum", id,type)
                .compose(RxUtils.io_main())
                .subscribe(httpResult -> {
                });
    }

    private XzwzzPlayer jzVideoPlayerStandard;

    @Override
    protected void initData() {
        getFreeNum();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMember();
        getFreeNum();
    }



    @Override
    public void onBackPressed() {
        if (jzVideoPlayerStandard.currentScreen == JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL) {
            super.onBackPressed();
            return;
        }
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    private void toActivity() {
        if (!vip) {
            if (!AppConfig.AVMEMBER) {
                if(!((BaseActivity) this).isFinishing()) {
                    if (AppConfig.is_cam != 1) {
                        VideoUtils.tag = 1;
                        this.finish();
                       // PayUtils2.payDialog(VideoPlayActivity.this, R.mipmap.av_pay_bg, "视频区", "微信支付报异常，请放心正常支付", 2, AppContext.avChargeList);
                    } else {
                        PayUtils2.payDialog(VideoPlayActivity.this, R.mipmap.av_pay_bg, "您还不是会员,开通会员获得更多体验", "请开通会员", 2, AppContext.avChargeList);
                    }
                }
            }
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    private void toBrower(String url) {
        if (!url.contains("http")) return;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    private void getFreeNum() {
        RetrofitClient.getInstance().createApi().getfreenum("Home.getfreenum", AppContext.getInstance().getLoginUid(), "1")
                .compose(RxUtils.io_main())
                .subscribe(new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(HttpResult bean) {
                        if (bean.ret == 200) {
                            if (bean.data.code == 0) {
                                vip = true;
                            } else {
                                vip = false;
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void isMember() {
        RetrofitClient.getInstance().createApi().getBaseUserInfo("User.getBaseInfo", AppContext.getInstance().getToken(), AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super HttpArray<UserInfoBean>>) new BaseListObserver<UserInfoBean>() {
                    @Override
                    protected void onHandleSuccess(List<UserInfoBean> list) {
                        if (list.size() > 0) {
                            UserInfoBean bean = list.get(0);
                            AppConfig.IS_MEMBER = (bean.is_member == 1);
                        }
                    }
                });
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            if (!vip) {
                if (!AppConfig.AVMEMBER) {
                    if (AppConfig.is_cam != 1) {
                        if(tips2.getVisibility() == View.GONE){
                            tips2.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            tips2.setText("体验还有("+millisUntilFinished / 1000 +") 秒后结束");
        }

        @Override
        public void onFinish() {
            tips2.setVisibility(View.GONE);
            mHandler.sendEmptyMessage(100);
        }
    }

}
