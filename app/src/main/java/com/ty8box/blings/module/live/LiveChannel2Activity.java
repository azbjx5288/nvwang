package com.neinei.cong.module.live;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.bean.ChannelDataBean;
import com.neinei.cong.bean.MobileBean;
import com.neinei.cong.bean.UserInfoBean;
import com.neinei.cong.bean.ZhuBoBean;
import com.neinei.cong.module.AbsModuleActivity;
import com.neinei.cong.module.live.adapter.LiveChannelAdapter;
import com.neinei.cong.ui.PayActivity;
import com.neinei.cong.ui.VipActivity;
import com.neinei.cong.ui.login.LoginActivity;
import com.neinei.cong.utils.DialogHelp;
import com.neinei.cong.utils.LoginUtils;
import com.neinei.cong.utils.MemberUtil;
import com.neinei.cong.utils.PayUtils;
import com.neinei.cong.utils.SharePrefUtil;
import com.neinei.cong.utils.StatusBarUtil;
import com.neinei.cong.widget.ViewStatusManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class LiveChannel2Activity extends AbsModuleActivity {
    private String id, logo;
    private ProgressDialog mProgressDialog;
    private View headView;
    private ImageView imgLogo;
    private TextView tvNum, tvName;
    private String title1;
    private Random random;



    @Override
    protected void initView() {
        super.initView();
        Bundle extras = getIntent().getExtras();
        title1 = extras.getString("plamform");
        logo = extras.getString("logo");
        setToolbar(title1);
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(v -> finish());
        id = extras.getString("id");
        mViewStatusManager.setStatus(ViewStatusManager.ViewStatus.loading);

        headView = View.inflate(this, R.layout.view_channel_head, null);
        imgLogo = headView.findViewById(R.id.img_channel_logo);
        tvNum = headView.findViewById(R.id.tv_num);
        tvName = headView.findViewById(R.id.tv_name);
        tvName.setText(title1);
        Glide.with(this).load(logo).into(imgLogo);

        mAdapter.addHeaderView(headView);

    }

    @Override
    protected void setRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildPosition(view);
                if (position == 0) return;
                int offest = SizeUtils.dp2px(5f);
                if (position % 2 == 0) {
                    outRect.set(offest, offest, offest / 2, 0);
                } else if (position % 2 == 1) {
                    outRect.set(offest / 2, offest, offest, 0);
                }
            }
        });
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new LiveChannelAdapter();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void loadData() {
        random = new Random();
        String url = SharePrefUtil.getString("dataurl", "");
        if (TextUtils.isEmpty(url)) return;
        RetrofitClient.getInstance().createApi().getChannelData2(url + id)
                .compose(RxUtils.io_main())
                .subscribe(new Observer<ZhuBoBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ZhuBoBean zhuBoBean) {
                        if (zhuBoBean.getZhubo() != null && zhuBoBean.getZhubo().size() > 0) {

                            List<ChannelDataBean.DataBean> list = new ArrayList<>();

                            for (int i = 0; i < zhuBoBean.getZhubo().size(); i++) {
                                ChannelDataBean.DataBean bean = new ChannelDataBean.DataBean();
                                ZhuBoBean.ZhuboBean zhuboBean = zhuBoBean.getZhubo().get(i);
                                bean.setBigpic(zhuboBean.getImg());
                                bean.setName(zhuboBean.getTitle());
                                bean.setUrl(zhuboBean.getAddress());
                                bean.setNum(random.nextInt(1000) + 100 + "");

                                list.add(bean);
                            }
                            mAdapter.setNewData(list);
                            mViewStatusManager.setStatus(ViewStatusManager.ViewStatus.success);
                            tvNum.setText(list.size() + "");
                        } else {
                            mViewStatusManager.setStatus(ViewStatusManager.ViewStatus.empty);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onComplete() {
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                });


    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        ChannelDataBean.DataBean item = (ChannelDataBean.DataBean) adapter.getItem(position);
        goRoomV1(item);
    }

    private void goRoomV1(ChannelDataBean.DataBean item) {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("data", item);
//        ActivityUtils.startActivity(bundle, LivePlayActivity.class);
        String url = item.getUrl();
        String name = item.getName();
        String nums = item.getNum();
        String imag = item.getBigpic();
        long uids = item.getUid();

        Intent intent = new Intent();
        intent.setAction("newlive");
        intent.putExtra("url",url);
        intent.putExtra("name",name);
        intent.putExtra("nums",nums);
        intent.putExtra("imag",imag);
        intent.putExtra("uids",uids);
        startActivityForResult(intent,1);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("AAAA",requestCode+"");
        Log.i("AAAA",resultCode+"");
        if (resultCode==RESULT_OK){
            if (requestCode==1){
                if (AppConfig.is_cam == 1) {
//                    LoginUtils.vipDialog(LiveChannel2Activity.this);
                } else {
                    PayUtils.payDialog(this, R.mipmap.zb_pay_bg, "直播区", "开通会员小姐姐任你选", 1, AppContext.zbChargeList);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMember();
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
