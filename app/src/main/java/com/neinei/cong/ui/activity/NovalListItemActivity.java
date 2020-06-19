package com.neinei.cong.ui.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.bean.AdListBean;
import com.neinei.cong.bean.BookBean;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class NovalListItemActivity extends com.neinei.cong.base.BaseActivity implements BaseQuickAdapter.OnItemClickListener, OnBannerClickListener {

    TextView toolbar_title;
    RecyclerView recyclerView;
    Banner mBanner;
    int postion;
    com.neinei.cong.module.book.BookAdapter adapter;
    List<com.neinei.cong.bean.BookBean> data = new ArrayList<>();
    private List<String> bannerList = new ArrayList<>();
    private List<AdListBean> adListBeans;
    private ProgressDialog dialog;
    String id;

    private ImageButton fan;
    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.noval_item;
    }

    @Override
    protected void initView() {
        super.initView();
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        setToolbar(title,true);
        postion = intent.getIntExtra("postion",0);
        toolbar_title = findViewById(R.id.toolbar_title);
        mBanner = findViewById(R.id.banner);
        fan = findViewById(R.id.fanhui);
        recyclerView = findViewById(R.id.recycler);
        toolbar_title.setText(title);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new com.neinei.cong.module.book.BookAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中");
        dialog.show();
        id = AppContext.novelClassify.get(postion).getTerm_id();
        fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void initData() {
        super.initData();
        getBanner();
        mBanner.setOnBannerClickListener(this);
    }


    private void getBanner() {
        com.neinei.cong.api.http.RetrofitClient.getInstance().createApi().adsList("Home.coin_adsList")
                .compose(com.neinei.cong.api.http.RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<AdListBean>>) new com.neinei.cong.api.http.BaseListObserver<AdListBean>() {
                    @Override
                    protected void onHandleSuccess(List<AdListBean> list) {
                        adListBeans = list;
                        if (list == null || list.size() == 0) return;
                        bannerList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            bannerList.add(list.get(i).getThumb());
                        }
                        setBanner();
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        com.neinei.cong.api.http.RetrofitClient.getInstance().createApi().getBookLists(id, AppContext.getInstance().getLoginUid()).compose(com.neinei.cong.api.http.RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<BookBean>>) new com.neinei.cong.api.http.BaseListObserver<BookBean>() {
                    @Override
                    protected void onHandleSuccess(List<BookBean> list) {
                        if (list == null || list.size() == 0) return;
                        data.clear();
                        data.addAll(list);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        com.neinei.cong.bean.BookBean item = (com.neinei.cong.bean.BookBean) adapter.getItem(position);
        if (item.is_buy == 0 && item.coin != 0) {
            dialog(item);
        } else {
            getData(item);
        }
    }

    private void dialog(com.neinei.cong.bean.BookBean item) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("需要付费" + item.coin + "钻石,是否购买？")
                .setPositiveButton("确定", (dialog, which) -> getData(item)).setNegativeButton("取消", null).show();
    }

    private void getData(com.neinei.cong.bean.BookBean item) {
        dialog.show();
        com.neinei.cong.api.http.RetrofitClient.getInstance().createApi().getNoveldetails("Home.novelDetails", item.id, AppContext.getInstance().getLoginUid())
                .compose(com.neinei.cong.api.http.RxUtils.io_main())
                .subscribe(new Observer<com.neinei.cong.bean.BooksBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(com.neinei.cong.bean.BooksBean booksBean) {
                        if (booksBean.getData().getCode() == 0) {
                            Bundle bundle = new Bundle();
                            bundle.putString("content", booksBean.getData().getInfo().getPost_content());
                            bundle.putString("name", item.post_title);
                            ActivityUtils.startActivity(bundle, com.neinei.cong.module.book.BookDetailActivity.class);
                        } else {
                            ToastUtils.showShort(booksBean.getData().getMsg());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        ToastUtils.showShort("钻石不足，请充值");
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void OnBannerClick(int position) {
        if (TextUtils.isEmpty(adListBeans.get(position - 1).getUrl()) || !adListBeans.get(position - 1).getUrl().startsWith("http"))
            return;
        Uri uri = Uri.parse(adListBeans.get(position - 1).getUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    private void setBanner() {
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        mBanner.setImageLoader(new com.neinei.cong.utils.MyImageLoader());
        mBanner.setImages(bannerList);
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.isAutoPlay(true);
        mBanner.setViewPagerIsScroll(true);
        mBanner.setDelayTime(3000);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.start();
    }
}
