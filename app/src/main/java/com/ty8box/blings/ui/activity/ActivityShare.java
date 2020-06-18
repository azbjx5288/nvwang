package com.ty8box.blings.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.neinei.cong.AppConfig;
import com.neinei.cong.R;
import com.ty8box.blings.PlatFormUtil;

public class ActivityShare extends AppCompatActivity {


    public static void shareQQ(Context mContext, String content) {
        if (PlatFormUtil.isInstallApp(mContext,PlatFormUtil.PACKAGE_MOBILE_QQ)) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, content);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "您需要安装QQ客户端", Toast.LENGTH_LONG).show();
        }
    }

    public void shareWechatFriend(Context context, String content) {
        if (PlatFormUtil.isInstallApp(getApplicationContext(),PlatFormUtil.PACKAGE_WE_CHAT)) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra("android.intent.extra.TEXT", content);
//            intent.putExtra("sms_body", content);
            intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }



    private ImageView imageView;
    private Button FxBtn;
    private String url;
    private ImageButton btnimg;
    private ImageButton qqbtn;
    private ImageButton wxbtn;
    private TextView yqm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fenxiang);
        imageView = findViewById(R.id.image);
        qqbtn = findViewById(R.id.qq);
        wxbtn = findViewById(R.id.wx);
        FxBtn = findViewById(R.id.fxbtn);
        btnimg = findViewById(R.id.fanhui);
        yqm = findViewById(R.id.yqm);
        yqm.setText("邀请码："+AppConfig.yqm);


        url = AppConfig.MAIN_URL+"/public/fenxiang/appf/er.png";
        Glide.with(getApplicationContext()).load(url).into(imageView);
        btnimg.setOnClickListener(new FanHui());
        FxBtn.setOnClickListener(new FXBtnClick());
        qqbtn.setOnClickListener(new QQFxClick());
        wxbtn.setOnClickListener(new WXFxClick());
    }
    class FanHui implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            finish();
        }
    }
    class FXBtnClick implements View.OnClickListener{
        private String fxurl = AppConfig.MAIN_URL+"public/fenxiang/index.html?invite_code="+AppConfig.yqm;
        @Override
        public void onClick(View v) {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("A",fxurl);
            cm.setPrimaryClip(clipData);
            Toast.makeText(getApplicationContext(),"复制成功",Toast.LENGTH_SHORT).show();
        }
    }
    class QQFxClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            shareQQ(getApplicationContext(),"这里是QQ推广语");
        }
    }
    class WXFxClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            shareWechatFriend(getApplicationContext(),"这里是微信推广语");
        }
    }




}
