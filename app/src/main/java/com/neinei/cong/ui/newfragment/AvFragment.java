package com.neinei.cong.ui.newfragment;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.bean.AdListBean;
import com.neinei.cong.bean.AvVideoListBean;
import com.neinei.cong.bean.UserInfoBean;
import com.neinei.cong.request.OKRequest;
import com.neinei.cong.ui.VipActivity;
import com.neinei.cong.ui.fragment.MFragmentViewType;
import com.neinei.cong.ui.newadapter.MyGridViewAdapter;
import com.neinei.cong.utils.PayUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;

public class AvFragment extends com.neinei.cong.base.BaseFragment implements OnBannerClickListener {

    AppBarLayout appBarLayout;
    private android.support.v7.widget.Toolbar mToolbar;
    private Banner mBanner;
    com.neinei.cong.view.MyGridView myGridViewTitle, myGridViewAV;
    private RecyclerView recyclerView;
    private List<String> titles;
    private List<String> bannerList = new ArrayList<>();
    private List<AdListBean> adListBeans;
    private List<AvVideoListBean> list = new ArrayList<>();
    private TextView searchEdi;
    String id;


    @Override
    public int getLayoutId() {
        return R.layout.newfragment_av;
    }

    @Override
    public void initView(View view) {
        searchEdi = view.findViewById(R.id.searchEdi);
        mBanner = view.findViewById(R.id.banner);
        mToolbar = view.findViewById(R.id.toolbar);
        appBarLayout = view.findViewById(R.id.appbar);
        //这是条垃圾代码会让你极度难受 又本大侠铲除了@QQ3245752005
//        com.neinei.cong.utils.StatusBarUtil.getInstance().setPaddingSmart(getActivity(), mToolbar);
        myGridViewTitle = view.findViewById(R.id.my_title_grid_view);
//        myGridViewAV = view.findViewById(R.id.gridView_av);
        recyclerView = view.findViewById(R.id.recycler);

        SpannableString ss = new SpannableString("请输入视频名称或电影名称");//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(13, true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchEdi.setText(new SpannedString(ss));

        //搜索框点击
        searchEdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("search");
                startActivity(intent);
            }
        });
//        fragments = new ArrayList<>();
        titles = new ArrayList<>();
//        for (int i = 0; i < AppContext.novelTermList.size(); i++) {
//            AvItemFragment f = new AvItemFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("id", AppContext.novelTermList.get(i).getTerm_id());
//            f.setArguments(bundle);
//            fragments.add(f);
//            titles.add(AppContext.novelTermList.get(i).getName());
//        }
//        viewPagerAdapter = new com.neinei.cong.ui.adapter.ViewPagerAdapter(getChildFragmentManager(), getActivity(), fragments, titles);

        id = AppContext.novelTermList.get(0).getTerm_id();
        //分类
        myGridViewTitle.setAdapter(new MyGridViewAdapter(getContext(), AppContext.novelTermList));
        myGridViewTitle.setOnItemClickListener(new MyVideoType());
        //        //默认显示的一个列表
        ReqTypeid();
        System.out.println("执行到此");
    }

    //请求分类ID
    private OKRequest okRequest;
    private void ReqTypeid(){
        okRequest = new OKRequest();
        okRequest.RequestTypeID();
        okRequest.setOnRequestListListener(new OkReqTypeListener());
        System.out.println("执行到此");
    }
    class OkReqTypeListener implements OKRequest.OnRequestListListener{
        @Override
        public void OnRequestList(List<Map<String, String>> list) {
            for (int i=0;i<list.size();i++){
                FragmentManager Fm = getChildFragmentManager();
                FragmentTransaction Ft = Fm.beginTransaction();
                MFragmentViewType mf = new MFragmentViewType();
                mf.GridViewinit(list.get(i).get("id"),list.get(i).get("name"));
                Ft.add(R.id.fragmentlayout,mf);
                Ft.commit();
            }
            Log.i("AAA","接收到回调"+list.size());
        }
    }
    /**************************************************/
    //影片类型击事件
    class MyVideoType implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String imgid = (String) view.getTag(R.id.mid);

            Intent intent = new Intent();
            intent.setAction("videotype");
            intent.putExtra("id",imgid);

            startActivityForResult(intent,1);

        }
    }




    @Override
    public void initData() {
        getBanner();
        getAllVideoList();
        mBanner.setOnBannerClickListener(this);
    }

    public void getAllVideoList() {
        com.neinei.cong.api.http.RetrofitClient.getInstance().createApi().videoAllList("Home.VideoAll").compose(com.neinei.cong.api.http.RxUtils.io_main())
                .subscribe((Observer<? super com.neinei.cong.api.http.HttpArray<Object>>) new com.neinei.cong.api.http.BaseListObserver<Object>() {
                    @Override
                    protected void onHandleSuccess(List<Object> avList) {
                        int a = 0;
//                        if (avList == null || avList.size() == 0)
//                        list.clear();
//                        list.addAll(avList);
//                        myGridViewAvAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void getBanner() {
        com.neinei.cong.api.http.RetrofitClient.getInstance().createApi().adsList("Home.av_adsList")
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

        isMember();
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


    private static final int REQUEST_CODE = 0x11;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==REQUEST_CODE){
            if (requestCode==1){
                if (AppConfig.is_cam == 1) {
                    startActivity(new Intent(getActivity(), VipActivity.class));
                } else {
                    PayUtils.payDialog(getActivity(), R.mipmap.zb_pay_bg, "直播区", "开通会员小姐姐任你选", 1, AppContext.zbChargeList);
                }
            }
        }
    }


}
