package com.neinei.cong.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.bean.PayDialogBean;
import com.neinei.cong.ui.VipActivity;
import com.neinei.cong.ui.adapter.DiamondAdapter;
import com.neinei.cong.ui.adapter.DiamondPayAdapter;

import java.util.List;

public class PayUtils2 {

    static int payType = 2;
    static String id = "";

    public static void payDialog(Activity activity, int imgResouse, String title, String msg, int channl_type, List<PayDialogBean> list) {




        id = list.get(0).getId();
        View view = View.inflate(activity, R.layout.dialog_diamond3, null);
        RecyclerView recycler = view.findViewById(R.id.recycler_3);
//        LinearLayout ll_onlinePay = view.findViewById(R.id.ll_onlinePay);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
        //设置表格，根据position计算在该position处1列占几格数据
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                return 2;
            }
        });

        ImageView image = view.findViewById(R.id.img_bg3);
        image.setImageResource(imgResouse);

        TextView tvTitle = view.findViewById(R.id.tv_title3);
        tvTitle.setText(title);

        ImageView img_weixin = view.findViewById(R.id.img_weixin3);
        ImageView img_zfb = view.findViewById(R.id.img_zfb3);

        DiamondPayAdapter adapter = new DiamondPayAdapter(R.layout.item_pay_dialog, list);
//        recycler.setLayoutManager(new LinearLayoutManager(activity));
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setAdapter(adapter);
        Dialog dialog = new Dialog(activity, R.style.wx_dialog);
        // 对话层点击返回按钮退出播放
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                activity.finish();
                if (keyCode == KeyEvent.KEYCODE_SEARCH)
                {
                    return true;
                }
                return false; //默认返回 false
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                activity.finish();
            }
        });

        dialog.setContentView(view);
        dialog.show();

        adapter.setOnItemClickListener((a, view1, position) -> {
            adapter.select(position);
            id = list.get(position).getId();
        });

        view.findViewById(R.id.layout_wx3).setOnClickListener(v -> {
            payType = 2;
            img_zfb.setImageResource(R.mipmap.select_false);
            img_weixin.setImageResource(R.mipmap.select_true);
        });

        view.findViewById(R.id.layout_zfb3).setOnClickListener(v -> {
            payType = 1;
            img_zfb.setImageResource(R.mipmap.select_true);
            img_weixin.setImageResource(R.mipmap.select_false);
        });


        if(title.equals("您还不是会员,开通会员获得更多体验")){
            // 卡密支付弹窗
//            ll_onlinePay.setVisibility(View.GONE);
            recycler.setVisibility(View.GONE);
            TextView tv_buy3 = view.findViewById(R.id.tv_buy3);
            tv_buy3.setText("续费");
            tv_buy3.setOnClickListener(v -> buy(activity));
        }else{
            // 在线支付弹窗
            view.findViewById(R.id.tv_buy3).setOnClickListener(v -> buy(dialog, activity, channl_type + "", payType, id));
        }

    }

    @SuppressLint("CheckResult")
    private static void buy(Dialog dialog, Activity activity, String channl_type, int pay_type, String change_id) {


        if (TextUtils.isEmpty(id)) {
            ToastUtils.showShort("请先选择");
            return;
        }

        RetrofitClient.getInstance().createApi().getOrder("Charge.getAliOrder",
                AppContext.getInstance().getLoginUid(), channl_type + "", change_id, pay_type + "", "111")
                .compose(RxUtils.io_main())
                .subscribe(payBean -> {
                    dialog.dismiss();
                    if (payBean.getData().getCode() != 0) {
                        ToastUtils.showShort(payBean.getData().getMsg());
                        return;
                    }
                    try {
                        String pay_url = payBean.getData().getInfo().getPay_url();
                        if (!TextUtils.isEmpty(pay_url)) {
                            Uri uri = Uri.parse(pay_url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            activity.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        activity.finish();
    }

    private static void buy(Activity activity){
        ActivityUtils.startActivity(VipActivity.class);
        activity.finish();
    }
}
