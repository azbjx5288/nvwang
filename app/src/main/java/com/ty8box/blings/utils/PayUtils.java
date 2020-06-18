package com.neinei.cong.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.bean.PayDialogBean;
import com.neinei.cong.ui.adapter.DiamondAdapter;
import com.neinei.cong.ui.adapter.DiamondPayAdapter;

import java.util.ArrayList;
import java.util.List;

public class PayUtils {

    static int payType = 2;
    static String id = "";

    public static void payDialog(Activity activity, int imgResouse, String title, String msg, int channl_type, List<PayDialogBean> list) {
        if(list.size() == 0){
            id = "1";
        }else{
            id = list.get(0).getId();
        }


        View view = View.inflate(activity, R.layout.dialog_diamond, null);
        RecyclerView recycler = view.findViewById(R.id.recycler);

        ImageView image = view.findViewById(R.id.img_bg);
        image.setImageResource(imgResouse);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(title);

        TextView des = view.findViewById(R.id.des);
        if (TextUtils.isEmpty(msg)) {
            des.setVisibility(View.GONE);
        } else {
            des.setText(msg);
        }
        ImageView img_weixin = view.findViewById(R.id.img_weixin);
        ImageView img_zfb = view.findViewById(R.id.img_zfb);

        //列表容器适配器(废弃)
//        DiamondPayAdapter adapter = new DiamondPayAdapter(R.layout.item_pay_dialog, list);
//        recycler.setLayoutManager(new GridLayoutManager(activity,2));
//        recycler.setAdapter(adapter);

        LinearLayout linear = view.findViewById(R.id.line1);

        List<View> listview = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            View view1 = LayoutInflater.from(activity).inflate(R.layout.item_money,null);
            linear.addView(view1);
            listview.add(view1);
            TextView money = view1.findViewById(R.id.money);
            TextView times = view1.findViewById(R.id.time);
            TextView titleS = view1.findViewById(R.id.title);
            TextView tix = view1.findViewById(R.id.tix);
            tix.setText((i+1)*10+("%用户选择"));

            view1.setTag(list.get(i).getId());
            times.setText(list.get(i).getMonth()+"天");
            money.setText(list.get(i).getGive());
            titleS.setText(list.get(i).getName());
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i=0;i<listview.size();i++){
                        View view2 = listview.get(i);


                        view2.setBackground(null);
                    }
                    id = (String) v.getTag();
                    Log.i("AAA","ID"+id);
                    v.setBackground(activity.getResources().getDrawable(R.drawable.item_pay_item_sok_true));
                }
            });
        }



        Dialog dialog = new Dialog(activity, R.style.wx_dialog);
        dialog.setContentView(view);
        dialog.show();
        //列表容器item选中事件
//        adapter.setOnItemClickListener((a, view1, position) -> {
//            adapter.select(position);
//            if(list.size() == 0){
//                id = "1";
//            }else{
//                id = list.get(position).getId();
//            }
//        });

        view.findViewById(R.id.layout_wx).setOnClickListener(v -> {
            payType = 2;
            img_zfb.setImageResource(R.mipmap.select_false);
            img_weixin.setImageResource(R.mipmap.select_true);
        });

        view.findViewById(R.id.layout_zfb).setOnClickListener(v -> {
            payType = 1;
            img_zfb.setImageResource(R.mipmap.select_true);
            img_weixin.setImageResource(R.mipmap.select_false);
        });

        view.findViewById(R.id.tv_buy).setOnClickListener(v -> buy(dialog, activity, channl_type + "", payType, id));
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
    }

}
