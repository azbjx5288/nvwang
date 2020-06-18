package com.neinei.cong.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.neinei.cong.bean.ConfigBean;
import com.neinei.cong.bean.DiamondAdBean;
import com.neinei.cong.bean.MoviesLinkListBean;
import com.neinei.cong.bean.VideoDetailBean;
import com.neinei.cong.module.video.VideoPlayActivity;
import com.neinei.cong.module.video.VideoUtils;
import com.neinei.cong.ui.adapter.VideoDetailAdapter;
import com.neinei.cong.utils.GlideUtils;
import com.neinei.cong.utils.PayUtils;
import com.neinei.cong.utils.SharePrefUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class VideoDetailActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private LinearLayout layoutTips;
    private ImageView videoImg;
    private ImageView imgAd;
    private TextView tvNum;
    private TextView tvTitle;
    private TextView tvCount;
    private RecyclerView recyclerView;

    private String id;
    private List<VideoDetailBean.ListBean> list = new ArrayList<>();
    private VideoDetailAdapter adapter;
    private DiamondAdBean adBean;
    private boolean vip = false;
    private VideoDetailBean detailBean;

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

        layoutTips = findViewById(R.id.layout_tips);
        videoImg = findViewById(R.id.img_diamond);
        imgAd = findViewById(R.id.img_ad);
        tvNum = findViewById(R.id.tv_view);
        tvTitle = findViewById(R.id.tv_diamond_title);
        tvCount = findViewById(R.id.tv_diamond);
        recyclerView = findViewById(R.id.recycler);
        layoutTips.setVisibility(View.GONE);
        imgAd.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        adapter = new VideoDetailAdapter(R.layout.item_video_detail, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        ad();
        videoImg.setOnClickListener(v -> toActivity());
        findViewById(R.id.close).setOnClickListener(v -> layoutTips.setVisibility(View.GONE));
        imgAd.setOnClickListener(v -> toBrower());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        video();
        getFreeNum();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        getFreeNum();
        Intent intent = new Intent(this, VideoDetailActivity.class);
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("id", list.get(position).getId());
        startActivity(intent);
        finish();
    }

    private void video() {
        RetrofitClient.getInstance().createApi().videoDetail("Home.MoviesLinkDetail", AppContext.getInstance().getLoginUid(), id)
                .compose(RxUtils.io_main())
                .subscribe((Observer<? super HttpResult<VideoDetailBean>>) new BaseObjObserver<VideoDetailBean>() {

                    @Override
                    protected void onHandleSuccess(VideoDetailBean bean) {
                        if (bean==null) return;
                        detailBean = bean;
                        GlideUtils.glide(mContext,bean.getDetails().getImg_url(),videoImg);
                        tvNum.setText(bean.getDetails().getWatch_num() + "");
                        tvTitle.setText(bean.getDetails().getTitle());
                        tvCount.setText(bean.getDetails().getCoin() + "");

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
                        GlideUtils.glide(VideoDetailActivity.this, bean.getThumb(), imgAd);
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
        RetrofitClient.getInstance().createApi().getfreenum("Home.getfreenum", AppContext.getInstance().getLoginUid(), "2")
                .compose(RxUtils.io_main())
                .subscribe(new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        if (httpResult.ret == 200) {
                            if (httpResult.data.code == 0) {
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

        if (vip) {
            startActivity();
        } else {
            if (detailBean==null) return;
            if (detailBean.getDetails().getIs_buy() == 0) {
                noBuyDialog();
                return;
            }
            startActivity();
        }

    }

    private void startActivity() {
        if (detailBean==null) return;
        Bundle bundle = new Bundle();
        bundle.putSerializable("title", detailBean.getDetails().getTitle());
        bundle.putSerializable("url", detailBean.getDetails().getUrl());
        bundle.putSerializable("type", "2");
        bundle.putSerializable("id", detailBean.getDetails().getId());
        ActivityUtils.startActivity(bundle, VideoPlayActivity.class);
    }

    private void noBuyDialog() {
        if (detailBean==null) return;
        new AlertDialog.Builder(this)
                .setMessage("还未购买该视频,是否花费" + detailBean.getDetails().getCoin() + "钻石购买")
                .setPositiveButton("购买", (dialog, which) -> {
                    buy();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .show();
    }

    private void buy() {
        if (detailBean==null) return;
        RetrofitClient.getInstance().createApi().buyVideo("Home.buyvideo", AppContext.getInstance().getLoginUid(), detailBean.getDetails().getId())
                .compose(RxUtils.io_main())
                .subscribe(httpResult -> {
                    if (httpResult.ret == 200) {
                        if (httpResult.data.code == 0) {
                            ToastUtils.showShort("购买成功");
                            startActivity();
                            video();
                        } else {
                            PayUtils.payDialog(VideoDetailActivity.this, R.mipmap.zb_pay_bg, "钻石区", "微信支付报异常，请放心正常支付", 3, AppContext.zsChargeList);
                        }
                    }
                });
    }

}
