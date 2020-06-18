package com.ty8box.blings.ui.newfragmnet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.bean.AdListBean;
import com.neinei.cong.bean.BookBean;
import com.neinei.cong.bean.BooksBean;
import com.neinei.cong.bean.NovelTermBean;
import com.ty8box.blings.ui.activity.NovalListItemActivity;
import com.ty8box.blings.ui.newadapter.MyGridViewAdapter;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DiamondNorvelFragment extends com.neinei.cong.base.BaseFragment implements OnBannerClickListener, BaseQuickAdapter.OnItemClickListener {

    AppBarLayout appBarLayout;
    private android.support.v7.widget.Toolbar mToolbar;
    private RecyclerView recyclerView;
    private Banner mBanner;
    private List<String> titles;
    private List<NovelTermBean> novelTermBeanList = new ArrayList<>();
    private List<String> bannerList = new ArrayList<>();
    private List<AdListBean> adListBeans;
    com.neinei.cong.view.MyGridView myGridView;

    com.neinei.cong.module.book.BookAdapter adapter;
    List<BookBean> data = new ArrayList<>();
    String id;
    private ProgressDialog dialog;

    @Override
    public int getLayoutId() {
        return R.layout.newfragment_diamond_norvel;
    }

    @Override
    public void initView(View view) {

        recyclerView = view.findViewById(R.id.recycler);
        myGridView = view.findViewById(R.id.my_title_grid_view);
        mBanner = view.findViewById(R.id.banner);
        mToolbar = view.findViewById(R.id.toolbar);
        appBarLayout = view.findViewById(R.id.appbar);
        //添加数据
        novelTermBeanList.clear();
        novelTermBeanList.addAll(AppContext.novelClassify);
    //这是条垃圾代码
//        com.neinei.cong.utils.StatusBarUtil.getInstance().setPaddingSmart(getActivity(), mToolbar);
        titles = new ArrayList<>();
//        for (int i = 0; i < AppContext.novelClassify.size(); i++) {
//            Bundle bundle = new Bundle();
//            bundle.putString("id", AppContext.novelClassify.get(i).getTerm_id());
//            titles.add(AppContext.novelClassify.get(i).getName());
//        }
        myGridView.setAdapter(new MyGridViewAdapter(getContext(), novelTermBeanList));
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NovalListItemActivity.class);
                intent.putExtra("title",novelTermBeanList.get(position).getName());
                intent.putExtra("postion",position);
                startActivity(intent);
            }
        });

        //默认显示第一个分类的小说列表
        id = AppContext.novelClassify.get(0).getTerm_id();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        adapter = new com.neinei.cong.module.book.BookAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("加载中");
    }

    @Override
    public void initData() {
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

    @Override
    public void OnBannerClick(int position) {
        if (TextUtils.isEmpty(adListBeans.get(position - 1).getUrl()) || !adListBeans.get(position - 1).getUrl().startsWith("http"))
            return;
        Uri uri = Uri.parse(adListBeans.get(position - 1).getUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
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
                    }
                });
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BookBean item = (BookBean) adapter.getItem(position);
        if (item.is_buy == 0 && item.coin != 0) {
            dialog(item);
        } else {
            getData(item);
        }
    }

    private void dialog(BookBean item) {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("需要付费" + item.coin + "钻石,是否购买？")
                .setPositiveButton("确定", (dialog, which) -> getData(item)).setNegativeButton("取消", null).show();
    }

    private void getData(BookBean item) {
        dialog.show();
        com.neinei.cong.api.http.RetrofitClient.getInstance().createApi().getNoveldetails("Home.novelDetails", item.id, AppContext.getInstance().getLoginUid())
                .compose(com.neinei.cong.api.http.RxUtils.io_main())
                .subscribe(new Observer<BooksBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BooksBean booksBean) {
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

}
