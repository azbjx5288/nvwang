package com.neinei.cong.ui.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.blankj.utilcode.util.ToastUtils;
import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseFragment;
import com.neinei.cong.bean.UserBean;
import com.neinei.cong.bean.UserInfoBean;
import com.neinei.cong.ui.ReadingActivity;
import com.neinei.cong.ui.VipActivity;
import com.neinei.cong.utils.PayUtils;
import com.neinei.cong.widget.LineControlView;
import com.neinei.cong.ui.activity.ActivityShare;
import com.neinei.cong.ui.activity.Activity_shoucang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observer;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private android.widget.TextView mTvName;
    private android.widget.TextView mTvId;
    private android.widget.TextView tvVipTime;
    private android.widget.TextView tvAvVipTime;
    private android.support.v7.widget.Toolbar mToolbar;
    private LineControlView tvDiamond;
    private LineControlView qingyaoma;

    private android.widget.TextView ZuanShiNum;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initView(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        mTvName = view.findViewById(R.id.tv_name);
        tvVipTime = view.findViewById(R.id.tv_vip_time);
        tvAvVipTime = view.findViewById(R.id.tv_av_vip_time);
        mTvId = view.findViewById(R.id.tv_id);
//        StatusBarUtil.getInstance().setPaddingSmart(getActivity(), mToolbar);

        tvDiamond = view.findViewById(R.id.zuanshi);
        tvDiamond.setOnClickListener(this);
        view.findViewById(R.id.kaitonghuiyuan).setOnClickListener(this);
        view.findViewById(R.id.avhuiyuan).setOnClickListener(this);
        view.findViewById(R.id.xinshou).setOnClickListener(this);
//        view.findViewById(R.id.yigoushipin).setOnClickListener(this);
//        view.findViewById(R.id.shoucang).setOnClickListener(this);
        view.findViewById(R.id.kefu).setOnClickListener(this);

        qingyaoma = view.findViewById(R.id.yaoqingma);
        view.findViewById(R.id.yaoqingma).setOnClickListener(this);

        ZuanShiNum = view.findViewById(R.id.zuannum);
        initGridver(view);



    }

    /**************************************/
    private GridView gridView;
    //Gridview列标题名
    private String[] mtitles = new String[]{
//            "钻石充值",
            "直播充值",
            "视频充值",
            "我的收藏",
            "邀请码",
            "联系客服",


            "分享推广"
    };
    //GridView图片资源
    private int[] mdrawad = new int[]{
//        R.mipmap.zs_icon,
            R.mipmap.zhy_icon,
            R.mipmap.shy_icon,
            R.mipmap.shoucang_icon,
            R.mipmap.yq_icon,
            R.mipmap.kf_icon,


            R.mipmap.fenx
    };
    private List<Map<String,Object>> mlist = new ArrayList<>();
    private void initGridver(View view){
        gridView = view.findViewById(R.id.mgridview);
        for (int i=0;i<mdrawad.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("t",mtitles[i]);
            map.put("r",mdrawad[i]);
            mlist.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getContext(),
                mlist,
                R.layout.item_mgrid_itemview,
                new String[]{"t","r"},
                new int[]{R.id.titles,R.id.image}
        );
        gridView.setAdapter(simpleAdapter);

        gridView.setOnItemClickListener(new GridViewItemListener());
    }
    class GridViewItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
//                case 0://钻石充值
//                    if (AppConfig.is_cam == 1) {
//                        startActivity(new Intent(getActivity(), VipActivity.class));
//                    } else {
////                        PayUtils.payDialog(getActivity(), R.mipmap.zb_pay_bg, "直播会员", "开通会员小姐姐任你选", 1, AppContext.zbChargeList);
//                        Toast.makeText(getContext(),"钻石只能通过会员获得！",Toast.LENGTH_SHORT).show();
//                    }
//                    break;
                case 0://开通会员
                    if (AppConfig.is_cam == 1) {
                        startActivity(new Intent(getActivity(), VipActivity.class));
                    } else {
                        PayUtils.payDialog(getActivity(), R.mipmap.zb_pay_bg, "直播会员", "开通会员小姐姐任你选", 1, AppContext.zbChargeList);
                    }
                    break;
                case 1://AV会员
                    if (AppConfig.is_cam == 1) {
                        startActivity(new Intent(getActivity(), VipActivity.class));
                    } else {
                        PayUtils.payDialog(getActivity(), R.mipmap.av_pay_bg, "视频会员", "开通会员观看完整视频", 2, AppContext.avChargeList);
                    }
                    break;
                case 2://新手必看
                    startActivity(new Intent(getActivity(), Activity_shoucang.class));
                    break;
                case 3:
                    copyy();
                    break;
                case 4://客服
                    copy();
                    break;
                case 5://客服
                    startActivity(new Intent(getActivity(), ActivityShare.class));
                    break;
//                case 6://客服
//                    startActivity(new Intent(getActivity(), BuyActivity.class));
//                    break;

            }
        }
    }

    /*********************************************************************/
    @Override
    public void initData() {
        UserBean loginUser = AppContext.getInstance().getLoginUser();
        mTvId.setText("会员号:" + loginUser.id);
        mTvName.setText(loginUser.user_nicename);
        flushUserInfo();
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View v) {
        Log.e("asfsdfdsafasdfadsfs","AppConfig.is_cam"+AppConfig.is_cam);
        switch (v.getId()) {
            case R.id.zuanshi://钻石充值
                if (AppConfig.is_cam == 1) {
                    startActivity(new Intent(getActivity(), VipActivity.class));
                } else {
                    PayUtils.payDialog(getActivity(), R.mipmap.zb_pay_bg, "小说钻石充值", "开通会员观看爽文", 3, AppContext.zsChargeList);
                }
                break;
            case R.id.kaitonghuiyuan://开通会员
                if (AppConfig.is_cam == 1) {
                    startActivity(new Intent(getActivity(), VipActivity.class));
                } else {
                    PayUtils.payDialog(getActivity(), R.mipmap.zb_pay_bg, "直播会员充值", "开通会员小姐姐任你选", 1, AppContext.zbChargeList);
                }
                break;
            case R.id.avhuiyuan://AV会员
                if (AppConfig.is_cam == 1) {
                    startActivity(new Intent(getActivity(), VipActivity.class));
                } else {
                    PayUtils.payDialog(getActivity(), R.mipmap.av_pay_bg, "视频会员充值", "开通会员观看完整视频", 2, AppContext.avChargeList);
                }
                break;
            case R.id.xinshou://新手必看
                startActivity(new Intent(getActivity(), ReadingActivity.class));
                break;
//            case R.id.yigoushipin://已购视频
//                startActivity(new Intent(getActivity(), BuyActivity.class));
//                break;
//            case R.id.shoucang://我的收藏
//                startActivity(new Intent(getActivity(), CollectActivity.class));
//                break;
            case R.id.yaoqingma:
                copyy();
                break;
            case R.id.kefu://客服
                copy();
                break;
        }
    }


    private void copyy() {
        ClipboardManager cm = (ClipboardManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        cm.setText(qingyaoma.getContent());
        ToastUtils.setBgColor(R.color.colorAccent);
        ToastUtils.setMsgColor(R.color.main_color);
        ToastUtils.showShort("复制：" + qingyaoma.getContent());

   }

    @Override
    public void onResume() {
        super.onResume();
        flushUserInfo();
    }

    @Override
    protected void onUserVisible() {
        flushUserInfo();
    }

    private void copy() {
        ClipboardManager cm = (ClipboardManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        if (TextUtils.isEmpty(AppConfig.QQ)) {
            cm.setText("暂未设置");
        } else {
            cm.setText(AppConfig.QQ);
        }
        ToastUtils.setBgColor(R.color.colorAccent);
        ToastUtils.setMsgColor(R.color.main_color);
        ToastUtils.showShort("复制成功：" + AppConfig.QQ);
    }

    private void flushUserInfo() {
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

                            tvVipTime.setText(bean.member_validity);
                            tvAvVipTime.setText(bean.avmember_validity);

                            mTvName.setText("用户名:" + bean.user_nicename);

                            mTvId.setText("ID:" + bean.id);

                            String coin = " 剩余钻石数：" + bean.coin;
                            ZuanShiNum.setText(coin);
                            Log.i("KKK","钻石数》"+bean.coin);
                            ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
                            SpannableString spannableString = new SpannableString(coin);
                            spannableString.setSpan(span, 7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tvDiamond.setContent(spannableString);
                            qingyaoma.setContent(bean.invitation_code);
                            AppConfig.yqm = bean.invitation_code;

                        }
                    }
                });
    }


}
