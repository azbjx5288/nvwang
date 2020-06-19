package com.neinei.cong.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.neinei.cong.R;
import com.neinei.cong.api.http.BaseListObserver;
import com.neinei.cong.api.http.RetrofitClient;
import com.neinei.cong.api.http.RxUtils;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.bean.VideoBean;
import com.neinei.cong.bean.VideoListBean;
import com.neinei.cong.bean.YunVideoBean;
import com.neinei.cong.module.video.VideoListActivity;
import com.neinei.cong.module.video.VideoPlayActivity;
import com.neinei.cong.ui.adapter.VideoAdapter;
import com.neinei.cong.utils.DialogHelp;
import com.neinei.cong.utils.MemberUtil;
import com.neinei.cong.utils.StatusBarUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private RelativeLayout layout;
    private ListView listView;
    private TextView tvTitle;
    private TextView tvRight;
    private VideoAdapter adapter;
    private List<VideoListBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
        loadData();
    }

    protected void initView() {

        layout = findViewById(R.id.layout);
        listView = findViewById(R.id.listView);
        listView = findViewById(R.id.listView);
        tvTitle = findViewById(R.id.tv_title);
        tvRight = findViewById(R.id.tv_right);
        tvRight.setVisibility(View.VISIBLE);
        findViewById(R.id.img_back).setOnClickListener(this);
        tvRight.setOnClickListener(this);

        tvTitle.setText("精彩视频");
        adapter = new VideoAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_right:
                loadData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        VideoListBean bean = list.get(i);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", bean);
        ActivityUtils.startActivity(bundle, VideoPlayActivity.class);
    }

    protected void loadData() {
        RetrofitClient.getInstance().createApi().tiyan("Home.getVideo").compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<VideoListBean>() {
                    @Override
                    protected void onHandleSuccess(List<VideoListBean> lists) {
                        if (lists == null || lists.size() == 0) return;
                        list.clear();
                        list.addAll(lists);
                        adapter.notifyDataSetChanged();
                    }
                });
    }


}
