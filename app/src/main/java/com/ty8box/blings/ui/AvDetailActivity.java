package com.neinei.cong.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.BaseObjObserver;
import com.neinei.cong.api.http.HttpResult;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.bean.AvVideoListBean;
import com.neinei.cong.bean.DiamondAdBean;
import com.neinei.cong.bean.UserInfoBean;
import com.neinei.cong.bean.VideoDetailBean;
import com.neinei.cong.module.live.LiveChannel2Activity;
import com.neinei.cong.module.video.VideoPlayActivity;
import com.neinei.cong.module.video.VideoUtils;
import com.neinei.cong.ui.adapter.AvDetailAdapter;
import com.neinei.cong.ui.adapter.VideoDetailAdapter;
import com.neinei.cong.utils.GlideUtils;
import com.neinei.cong.utils.LoginUtils;
import com.neinei.cong.utils.PayUtils;
import com.ty8box.blings.ui.activity.ActivityVideoType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class AvDetailActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            super.onDestroy();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private LinearLayout layoutTips;
    private ImageView videoImg;
    private ImageView imgAd;
    private TextView tvNum;
    private TextView tvTitle;
    private TextView tvWatch;
    private TextView tvCount;
    private RecyclerView recyclerView;

    private String id;
    private List<VideoDetailBean.ListBean> list = new ArrayList<>();
    private AvDetailAdapter adapter;
    private DiamondAdBean adBean;
    private boolean vip = false;
    private VideoDetailBean detailBean;
    private ImageButton fanhui;
    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        String title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        if (title.length() > 8) {
            title = title.substring(0, 8);
        }
        setToolbar(title, true);
        fanhui = findViewById(R.id.fanhui);
        layoutTips = findViewById(R.id.layout_tips);
        videoImg = findViewById(R.id.img_diamond);
        imgAd = findViewById(R.id.img_ad);
        tvNum = findViewById(R.id.tv_view);
        tvTitle = findViewById(R.id.tv_diamond_title);
        tvCount = findViewById(R.id.tv_diamond);
        tvWatch = findViewById(R.id.tv_watch);
        tvCount.setVisibility(View.GONE);
        tvNum.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recycler);
        layoutTips.setVisibility(View.GONE);
        imgAd.setVisibility(View.GONE);
        tvWatch.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();


            }
        });

        adapter = new AvDetailAdapter(R.layout.item_av_detail, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        ad();
        videoImg.setOnClickListener(v -> toActivity());
        findViewById(R.id.close).setOnClickListener(v -> layoutTips.setVisibility(View.GONE));
        imgAd.setOnClickListener(v -> toBrower());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 如果  VideoUtils.tag == 1 就弹出提示充值的面板
        if(VideoUtils.tag == 1){
            PayUtils.payDialog(AvDetailActivity.this, R.mipmap.av_pay_bg, "AV区", "开通会员观看完整视频", 2, AppContext.avChargeList);
            VideoUtils.tag = 0;
        }

//        isMember();
        video();
//        getFreeNum();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, AvDetailActivity.class);
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("id", list.get(position).getId());
        startActivity(intent);
    }

    private void video() {
        RetrofitClient.getInstance().createApi().videoDetail("Home.Videodetails", AppContext.getInstance().getLoginUid(), id)
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super HttpResult<VideoDetailBean>>) new BaseObjObserver<VideoDetailBean>() {
                    @Override
                    protected void onHandleSuccess(VideoDetailBean bean) {
                        if (bean==null) return;
                        detailBean = bean;
//                        GlideUtils.glide(AvDetailActivity.this, bean.getDetails().getVideo_img(), videoImg);
                        GlideUtils.glide(mContext, bean.getDetails().getVideo_img(), videoImg);
                        tvNum.setText(bean.getDetails().getWatch_num() + "");
                        tvTitle.setText(bean.getDetails().getTitle());
                        tvCount.setText(bean.getDetails().getCoin() + "");
                        tvWatch.setText(bean.getDetails().getWatch_num() + "");
                        list.clear();
                        if (bean.getList() == null || bean.getList().size() == 0) return;

                        list.addAll(bean.getList());
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void ad() {
        RetrofitClient.getInstance().createApi().diamondAv("Home.avneiye")
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super HttpResult<DiamondAdBean>>) new BaseObjObserver<DiamondAdBean>() {
                    @Override
                    protected void onHandleSuccess(DiamondAdBean bean) {
                        imgAd.setVisibility(View.VISIBLE);
                        adBean = bean;
                        GlideUtils.glide(AvDetailActivity.this, bean.getThumb(), imgAd);
                    }
                });
    }

    private void toBrower() {
        if (adBean == null) return;
        Uri uri = Uri.parse(adBean.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
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
                                layoutTips.setVisibility(View.VISIBLE);
                            } else {
                                vip = false;
                                layoutTips.setVisibility(View.GONE);
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


    private void toActivity() {
//        if (vip) {
//            Toast.makeText(this,"vip1="+vip,Toast.LENGTH_SHORT);
//            startActivity();
//        } else {
//            if (!AppConfig.AVMEMBER) {
//                if (AppConfig.is_cam == 1) {
//                    Toast.makeText(this,"vip2="+vip,Toast.LENGTH_SHORT);
//                    LoginUtils.vipDialog(AvDetailActivity.this);
//                } else {
//                    Toast.makeText(this,"vip3="+vip,Toast.LENGTH_SHORT);
//                    PayUtils.payDialog(AvDetailActivity.this, R.mipmap.av_pay_bg, "AV区", "vip="+vip, 2, AppContext.avChargeList);
//                }
//                return;
//            }
//            Toast.makeText(this,"vip4="+vip,Toast.LENGTH_SHORT);
            startActivity();
//        }
    }

    private void startActivity() {
        if (detailBean==null) return;
        Bundle bundle = new Bundle();
        bundle.putSerializable("title", detailBean.getDetails().getTitle());
        bundle.putSerializable("url", detailBean.getDetails().getVideo_url());
        bundle.putSerializable("type", "1");
        bundle.putSerializable("id", detailBean.getDetails().getId());
        ActivityUtils.startActivity(bundle, VideoPlayActivity.class);
    }

    private void isMember() {
            RetrofitClient.getInstance().createApi().getBaseUserInfo("User.getBaseInfo", AppContext.getInstance().getToken(), AppContext.getInstance().getLoginUid())
                    .compose(RxUtils.io_main())
                    .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<UserInfoBean>>) new BaseListObserver<UserInfoBean>() {
                        @Override
                        protected void onHandleSuccess(List<UserInfoBean> list) {
                            if (list.size() > 0) {
                                UserInfoBean bean = list.get(0);
                                AppConfig.IS_MEMBER = (bean.is_member == 1);
                            }
                        }
                    });
    }


}
