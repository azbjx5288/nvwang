package com.neinei.cong.module.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.source.UrlSource;
import com.bumptech.glide.Glide;
import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class LiveNewPlay extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aliPlayer.stop();
        aliPlayer.release();
        if(timestate){
//            count.stop();
        }

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_play_new);
        getData();

        init();

        initlistener();

    }

    private String url;
    private String name;
    private String nums;
    private String imag;
    private String uids;
    private void getData(){
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        name = intent.getStringExtra("name");
        nums = intent.getStringExtra("nums");
        imag = intent.getStringExtra("imag");
        uids = intent.getStringExtra("uids");


    }

    private SurfaceView surfaceView;
    private ImageView imageView;
    private ImageView imageGif;
    private TextView livetitle;
    private TextView livenum;
    private TextView tvTips;
    private AliPlayer aliPlayer;
    private SurfaceHolder holder;
    private ImageButton scbtn;
    private ImageButton gbbtn;
    private TextView times;
    private String gifAd;
    private String textAd;
    private String[] gifSplit;
    private String[] textSplit;
    private void init(){
        imageGif = findViewById(R.id.imagegif);
        tvTips = findViewById(R.id.tv_tips);
        scbtn = findViewById(R.id.sc);
        gbbtn = findViewById(R.id.gb);
        times = findViewById(R.id.times);
        surfaceView = findViewById(R.id.surface);
        imageView = findViewById(R.id.image);
        livetitle = findViewById(R.id.livetitle);
        livenum = findViewById(R.id.num);
        aliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());
        aliPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
        holder = surfaceView.getHolder();
        UrlPlayStart(url);


        if (AppContext.textAdBean!=null){
            textAd =  AppContext.textAdBean.getZb_ad();
            gifAd = AppContext.textAdBean.getZb_gif();
            textSplit = textAd.replaceAll("，", ",").split(",");
            gifSplit = gifAd.replaceAll("，", ",").split(",");
            Glide.with(getApplicationContext()).load(gifSplit[0]).into(imageGif);
            tvTips.setText(textSplit[0]);
        }



        Glide.with(this).load(imag).into(imageView);
        livetitle.setText(name);
        livenum.setText(nums);
    }


    private void initlistener() {
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                aliPlayer.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                aliPlayer.redraw();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                aliPlayer.setDisplay(null);
            }
        });

        aliPlayer.setOnPreparedListener(new AliPlayPrepared());
        aliPlayer.setOnErrorListener(new AliPlayError());
        aliPlayer.setOnRenderingStartListener(new AliPlayRendering());
        aliPlayer.setOnStateChangedListener(new AliPlayState());

        scbtn.setOnClickListener(new ScClick());
        gbbtn.setOnClickListener(new GbClick());
    }

    //执行播放函数
    private UrlSource urlSource;
    private void UrlPlayStart(String url) {
        urlSource = new UrlSource();
        urlSource.setUri(url);
        aliPlayer.setDataSource(urlSource);
        aliPlayer.prepare();
    }

    //准备完毕
    class AliPlayPrepared implements IPlayer.OnPreparedListener{
        @Override
        public void onPrepared() {
            aliPlayer.start();
            StartTime();
        }
    }
    //错误事件
    class AliPlayError implements IPlayer.OnErrorListener{
        @Override
        public void onError(ErrorInfo errorInfo) {

        }
    }
    //首帧事件
    class AliPlayRendering implements IPlayer.OnRenderingStartListener{

        @Override
        public void onRenderingStart() {

        }
    }
    //状态
    class AliPlayState implements IPlayer.OnStateChangedListener{

        @Override
        public void onStateChanged(int i) {
            switch (i) {
                case 1:
                    //缓冲
                    break;
                case 3:
                    //播放
                    break;
                case 4:
                    //暂停
                    break;
                case 7:
                    //错误
                    Toast.makeText(getApplicationContext(), "主播暂不在线", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                default:

                    break;
            }
        }
    }
    //收藏点击
    class ScClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            ImageButton button = view.findViewById(R.id.sc);
            button.setImageDrawable(getResources().getDrawable(R.mipmap.sc_true));

        }
    }
    //关闭点击
    class GbClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            finish();
        }
    }
    //执行判断
    private  com.neinei.cong.utils.CountDownUtils count;
    private boolean timestate = false;
    private void StartTime(){
        timestate = true;
        if (!TextUtils.isEmpty(AppConfig.free_time)&&!AppConfig.IS_MEMBER){
            times.setVisibility(View.VISIBLE);
            count = new com.neinei.cong.utils.CountDownUtils(times,"剩余观看时间：%s",Integer.valueOf(AppConfig.free_time));
            count.start();
            count.setCountdownListener(new com.neinei.cong.utils.CountDownUtils.CountdownListener() {
                @Override
                public void onStartCount() {

                }

                @Override
                public void onFinishCount() {
                    times.setVisibility(View.GONE);
                }

                @Override
                public void onUpdateCount(int currentRemainingSeconds) {
                    times.setText("剩余观看时间："+currentRemainingSeconds+"s");
                }
            });
            startDownTime();
        }
    }

    private void startDownTime() {
        Observable.timer(Integer.valueOf(AppConfig.free_time), TimeUnit.SECONDS)
                .compose(com.neinei.cong.api.http.RxUtils.io_main())
                .subscribe(aLong -> isMember());
    }
    private void isMember() {
        setResult(RESULT_OK);
        finish();

    }

}
