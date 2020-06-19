package com.neinei.cong.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.neinei.cong.R;
import com.neinei.cong.ui.adapter.ShouCangAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity_shoucang extends AppCompatActivity {


    private ImageButton FanBtn;
    private Button QkBtn;
    private ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoucang);

        FanBtn = findViewById(R.id.fanhui);
        QkBtn = findViewById(R.id.qk);
        listView = findViewById(R.id.listView);

        FanBtn.setOnClickListener(new FanHuiClick());
        QkBtn.setOnClickListener(new QingKongClick());
        init();
    }

    //初始化获取收藏数据
    private List<Map<String,String>> list = new ArrayList<>();
    private ShouCangAdapter adapter;
    private void  init(){
        adapter = new ShouCangAdapter(list,getApplicationContext());
        listView.setAdapter(adapter);
    }



    class FanHuiClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            finish();
        }
    }

    class QingKongClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //清空
        }
    }
}
