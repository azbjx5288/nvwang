package com.ty8box.blings.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.neinei.cong.R;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ActivityUtils.startActivity(com.neinei.cong.ui.SplashActivity.class);
        finish();
    }




}
