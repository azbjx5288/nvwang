


package com.neinei.cong.ui;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.bean.MobileBean;
import com.neinei.cong.bean.Pay2Bean;
import com.neinei.cong.bean.PayBean;
import com.neinei.cong.bean.UserInfoBean;
import com.neinei.cong.bean.VipBean;
import com.neinei.cong.utils.LoginUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Response;

public class VipActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private TextView textView, tvWx;

    private String s;
    private ImageView imgZb, imgAv, img_xiaoshuo;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        initView();
        findViewById(R.id.img_back).setOnClickListener(view -> finish());
        TextView tvTitle = findViewById(R.id.tv_title);

        tvTitle.setText("会员续费");
    }


    protected void initView() {

        editText = findViewById(R.id.et_kami);
        textView = findViewById(R.id.text);
//        imgZb = findViewById(R.id.img_zhibo);
        imgAv = findViewById(R.id.img_av);
        imgAv = findViewById(R.id.img_av);
//        img_xiaoshuo = findViewById(R.id.img_xiaoshuo);
//        findViewById(R.id.ll_zhibo).setOnClickListener(this);
//        findViewById(R.id.ll_av).setOnClickListener(this);
//        findViewById(R.id.ll_xiaoshuo).setOnClickListener(this);

        textView.setText(AppContext.text + "");
        findViewById(R.id.img_nianka).setOnClickListener(this);
        findViewById(R.id.img_yueka).setOnClickListener(this);
        findViewById(R.id.img_jika).setOnClickListener(this);
        findViewById(R.id.img_zhongshen).setOnClickListener(this);

        findViewById(R.id.btn_jihuo).setOnClickListener(this);

        findViewById(R.id.tv_kefu).setOnClickListener(this);

        tvWx = findViewById(R.id.tv_wx);
        tvWx.setOnClickListener(this);
        if (TextUtils.isEmpty(AppConfig.QQ)) {
            tvWx.setText("暂未设置联系方式");
        } else {
            tvWx.setText(AppConfig.QQ);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_yueka:
                    ka(AppConfig.YUE);

                break;
            case R.id.img_jika:
                    ka(AppConfig.JI);


                break;
            case R.id.img_nianka:
                    ka(AppConfig.YEAR);

                break;

            case R.id.img_zhongshen:
                    ka(AppConfig.FOREVER);

                break;

            case R.id.tv_kefu:

                break;

            case R.id.btn_jihuo:
                kami();
                break;
            case R.id.tv_wx:
                copy();
                break;
//            case R.id.ll_zhibo:
//                type = 1;
//                imgZb.setImageResource(R.mipmap.pay_select_true);
//                imgAv.setImageResource(R.mipmap.pay_select_false);
//                img_xiaoshuo.setImageResource(R.mipmap.pay_select_false);
//                break;
//            case R.id.ll_av:
//                type = 2;
//                imgAv.setImageResource(R.mipmap.pay_select_true);
//                imgZb.setImageResource(R.mipmap.pay_select_false);
//                img_xiaoshuo.setImageResource(R.mipmap.pay_select_false);
//                break;
//            case R.id.ll_xiaoshuo:
//                type = 3;
//                imgAv.setImageResource(R.mipmap.pay_select_false);
//                imgZb.setImageResource(R.mipmap.pay_select_false);
//                img_xiaoshuo.setImageResource(R.mipmap.pay_select_true);
//                break;
        }
    }

    private void copy() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(AppConfig.QQ);
        ToastUtils.showShort("复制成功");
    }


    private void ka(String url) {
        if (TextUtils.isEmpty(url)) {
            wxDialog();

        } else {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void wxDialog() {
        Dialog dialog = new Dialog(this, R.style.wx_dialog);
        View view = View.inflate(this, R.layout.dialog_commom, null);
        TextView tvMessage = view.findViewById(R.id.tv_message);
        tvMessage.setText("请联系客服购买卡密\n" + AppConfig.QQ);
        view.findViewById(R.id.tv_close).setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(view);
        dialog.show();
    }


    private void kami() {
        s = editText.getText().toString();
        if (TextUtils.isEmpty(s)) {
            ToastUtils.showShort("请输入卡密");
            return;
        }
//        if (type==0){
//            ToastUtils.showShort("请选择卡密类型");
//            return;
//        }
        RetrofitClient.getInstance().createApi().kami("Charge.exchange", AppContext.getInstance().getLoginUid(), s, type)
                .compose(RxUtils.io_main())
                .subscribe(new Observer<VipBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VipBean vipBean) {
                        if (vipBean.getData().getCode() == 0) {
                            ToastUtils.showShort("续费成功");
                            isMember();
                            finish();
                        } else {
                            ToastUtils.showShort(vipBean.getData().getMsg());
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

    private void isMember() {
        RetrofitClient.getInstance().createApi().getBaseUserInfo("User.getBaseInfo", AppContext.getInstance().getToken(), AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<UserInfoBean>() {
                    @Override
                    protected void onHandleSuccess(List<UserInfoBean> list) {
                        if (list.size() > 0) {
                            UserInfoBean bean = list.get(0);
                            AppConfig.IS_MEMBER = (bean.is_member == 1);
                        }
                    }
                });
    }


    private void getOrder(String change_type, String money, String type) {
        RetrofitClient.getInstance().createApi().getOrder("Charge.getAliOrder",
                AppContext.getInstance().getLoginUid(), change_type, money, type, "111")
                .compose(RxUtils.io_main())
                .subscribe(new Observer<PayBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PayBean payBean) {
                        try {
                            String pay_url = payBean.getData().getInfo().getPay_url();
                            if (!TextUtils.isEmpty(pay_url)) {
                                Uri uri = Uri.parse(pay_url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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

    private void selectPay(String type, String money) {

        final CharSequence[] charSequences = {"支付宝", "微信"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("选择支付方式")
                .setItems(charSequences, (dialog, which) -> {
                    Log.i("abc", "" + which);
                    getOrder(type, money, (which + 1) + "");

                }).show();
    }

}
