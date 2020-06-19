package com.neinei.cong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.request.OKRequest;
import com.neinei.cong.ui.VipActivity;
import com.neinei.cong.ui.adapter.MVoidAdapter;
import com.neinei.cong.utils.PayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityVideoType extends AppCompatActivity {


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("AAAA",requestCode+"");
        Log.i("AAAA",resultCode+"");
        if (resultCode==RESULT_OK){
            if (requestCode==1){
                if (AppConfig.is_cam == 1) {
                    startActivity(new Intent(this, VipActivity.class));
                } else {
                    PayUtils.payDialog(this, R.mipmap.zb_pay_bg, "直播区", "开通会员小姐姐任你选", 1, AppContext.zbChargeList);
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_type);
        init();
        initlistener();
    }

    private String id;
    private String Title;
    private TextView typetitle;
    private GridView gridView;
    private ImageButton fanbtn;
    private OKRequest okRequest;
    private MVoidAdapter adapter;
    private List<Map<String,String>> list = new ArrayList<>();
    private void init(){
        typetitle = findViewById(R.id.typetitle);
        gridView = findViewById(R.id.gridview);
        fanbtn = findViewById(R.id.fanhui);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Title = intent.getStringExtra("title");
        typetitle.setText(Title);

        okRequest = new OKRequest();
        if(id==null){
            id = "53";
        }
        if(Title == null){
            Title = "视频列表";
        }
        adapter = new MVoidAdapter(list,getApplicationContext());
        gridView.setAdapter(adapter);
        okRequest.TypeRequest(id,"1",adapter,list,gridView);
    }

    private void initlistener(){
        fanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gridView.setOnItemClickListener(new GridVieItemClick());
        gridView.setOnScrollListener( new GridViewScrollListener());
    }
    private int peg = 1;
    class GridViewScrollListener implements AbsListView.OnScrollListener {
        private boolean aBoolean = true;
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState==0){
                if(aBoolean){

                    peg= peg+1;
                    okRequest.TypeRequest(id,peg+"",adapter,list,gridView);
                    aBoolean = false;
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int s = totalItemCount - visibleItemCount;
            if(firstVisibleItem>=s){
                aBoolean = true;
            }

        }
    }

    class GridVieItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String ids = (String) view.getTag(R.id.mid);
            String tim = (String) view.getTag(R.id.mti);
            String url = (String) view.getTag(R.id.vdl);
            String tid = (String) view.getTag(R.id.mtg);
            Intent intent = new Intent();
            intent.setAction("play");
            intent.putExtra("id",ids);
            intent.putExtra("title",tim);
            intent.putExtra("url",url);
            intent.putExtra("tid",tid);
//            startActivity(intent);
            startActivityForResult(intent,1);
        }
    }
}
