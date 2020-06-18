package com.neinei.cong.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.BaseObjObserver;
import com.neinei.cong.api.http.HttpResult;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseFragment;
import com.neinei.cong.bean.BookBean;
import com.neinei.cong.bean.BooksBean;
import com.neinei.cong.module.book.BookAdapter;
import com.neinei.cong.module.book.BookDetailActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NovelFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    BookAdapter adapter;
    List<BookBean> data = new ArrayList<>();
    String id;
    private ProgressDialog dialog;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_novel;
    }

    @Override
    public void initView(View view) {

        id = getArguments().getString("id");
        LogUtils.e("term_id：" + id);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        adapter = new BookAdapter(data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("加载中");

    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        RetrofitClient.getInstance().createApi().getBookLists(id, AppContext.getInstance().getLoginUid()).compose(RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<BookBean>>) new BaseListObserver<BookBean>() {
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
        RetrofitClient.getInstance().createApi().getNoveldetails("Home.novelDetails", item.id, AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
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
                            ActivityUtils.startActivity(bundle, BookDetailActivity.class);
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
