package com.neinei.cong.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.CacheConfig;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import com.bumptech.glide.Glide;
import com.neinei.cong.AppConfig;
import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.request.OKRequest;
import com.neinei.cong.ui.adapter.MVoidAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class MainPlay extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
        aliPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aliPlayer.stop();
        aliPlayer.release();

    }


    private SurfaceView surface;
    private SeekBar seekBar;
    private TextView newtime;
    private TextView numtime;
    private TextView fen;
    private ImageButton quanBtn;
    private ImageButton startBtn;
    private ProgressBar progressBar;
    private RelativeLayout relative;
    private LinearLayout control;
    private ImageView imageView;
    private GridView gridView;
    private LinearLayout toulinear;
    private ImageButton Fanhui;
    private TextView TvTitle;
    private TextView times;
    private ImageButton startbtn1;
    private ImageView imageGif;
    private TextView tvTips;


    private String gifAd;
    private String textAd;
    private String[] gifSplit;
    private String[] textSplit;

    private String title;
    private String url;
    private String ids;
    private String tid;

    private int xnbtnheight;

    private List<Map<String,String>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_video);

        fen = findViewById(R.id.fen);
        tvTips = findViewById(R.id.tvTips);
        times = findViewById(R.id.times);
        imageView = findViewById(R.id.image);
        imageGif = findViewById(R.id.imageGif);
        toulinear = findViewById(R.id.toubar);
        startbtn1 = findViewById(R.id.startbtn1);
        TvTitle = findViewById(R.id.title);
        gridView = findViewById(R.id.gridview);
        Fanhui = findViewById(R.id.fanhui);
        surface = findViewById(R.id.surface);
        seekBar = findViewById(R.id.seekbar);
        newtime = findViewById(R.id.newtime);
        numtime = findViewById(R.id.numtime);
        quanBtn = findViewById(R.id.quanBtn);
        startBtn = findViewById(R.id.startBtn);
        progressBar = findViewById(R.id.progressBar);
        relative = findViewById(R.id.relative);
        control = findViewById(R.id.control);

        startBtn.setEnabled(false);
        startbtn1.setEnabled(false);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        TvTitle.setText(title);
        ids = intent.getStringExtra("id");
        tid = intent.getStringExtra("tid");
        Log.i("OLP",ids);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);////设置固定状态栏常驻，覆盖app布局
//            getWindow().setStatusBarColor(Color.parseColor("#80000000"));//设置状态栏颜色
//        }

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            View view = getWindow().getDecorView();
            int uioptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uioptions);
        }
        Resources resources = getResources();
        int ss = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        xnbtnheight = resources.getDimensionPixelSize(ss);
        init();
        InitListener();
        UrlPlayStart(url);


        MVoidAdapter adapter = new MVoidAdapter(list,getApplicationContext());
        gridView.setAdapter(adapter);
        new OKRequest().getneiye(imageView,getApplicationContext());
        Log.i("UUU","开始请求");
        new OKRequest().TypeRequest(tid,1+"",adapter,list,gridView);
        if (AppContext.textAdBean!=null){
            textAd =  AppContext.textAdBean.getZb_ad();
            gifAd = AppContext.textAdBean.getZb_gif();
            textSplit = textAd.replaceAll("，", ",").split(",");
            gifSplit = gifAd.replaceAll("，", ",").split(",");
            Glide.with(getApplicationContext()).load(gifSplit[0]).into(imageGif);
            tvTips.setText(textSplit[0]);
        }

    }



    //初始播放器
    private int widtht;
    private int height;
    private AliPlayer aliPlayer;
    private UrlSource urlSource;
    private CacheConfig cacheConfig;
    private PlayerConfig playerConfig;

    private void init() {
        widtht = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        aliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());
        cacheConfig = new CacheConfig();
        cacheConfig.mEnable = true;
        cacheConfig.mMaxDurationS = 1000;
        cacheConfig.mDir = getFilesDir().getAbsolutePath();
        cacheConfig.mMaxSizeMB = 300;
        aliPlayer.setCacheConfig(cacheConfig);
        ////////////////////////////////////////
        playerConfig = aliPlayer.getConfig();
        playerConfig.mMaxDelayTime = 50000;
        playerConfig.mMaxBufferDuration = 60000;
        playerConfig.mHighBufferDuration = 3000;
        playerConfig.mStartBufferDuration = 5000;
        aliPlayer.setConfig(playerConfig);

    }

    //初始化监听器
    private void InitListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBardrag());
        startBtn.setOnClickListener(new StartBtnClick());
        startbtn1.setOnClickListener(new PlayStartBtnClick());
        quanBtn.setOnClickListener(new QuanClick());
        control.setOnTouchListener(new Control());

        surface.setOnTouchListener(new PlayTouch());
        surface.getHolder().addCallback(new SurfaceBinding());


        aliPlayer.setOnCompletionListener(new PlayCompletion());
        aliPlayer.setOnErrorListener(new PlayError());
        aliPlayer.setOnPreparedListener(new PlayPrepared());
        aliPlayer.setOnRenderingStartListener(new PlayRendering());
        aliPlayer.setOnSeekCompleteListener(new PlaySeekComplete());
        aliPlayer.setOnStateChangedListener(new PlayStateChanged());
        aliPlayer.setOnInfoListener(new PlayInfoListener());

        Fanhui.setOnClickListener(new Fanhui());
        gridView.setOnItemClickListener(new GridviewitemClick());


    }



    //开始暂停按钮
    class PlayStartBtnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            playstate();
        }
    }


    //返回键
    class Fanhui implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int cg = getResources().getConfiguration().orientation;
            if(cg!=Configuration.ORIENTATION_LANDSCAPE){
                finish();
            }else {
                shu();
            }
        }
    }

    //GridView 选中事件
    class GridviewitemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String til = (String) view.getTag(R.id.mti);
            String url = (String) view.getTag(R.id.vdl);
            TvTitle.setText(til);
            aliPlayer.stop();
            aliPlayer.reset();
            UrlPlayStart(url);
            startBtn.setEnabled(false);
            startbtn1.setEnabled(false);
            Toast.makeText(getApplicationContext(),"稍等正在为您切换",Toast.LENGTH_LONG).show();
        }
    }

    //执行播放函数
    private void UrlPlayStart(String url) {
        urlSource = new UrlSource();
        urlSource.setUri(url);
        aliPlayer.setDataSource(urlSource);
        aliPlayer.prepare();
    }

    //播放器按钮状态改变b
    private void playstate() {
        if (startState) {
            startBtn.setImageDrawable(getResources().getDrawable(R.mipmap.stop));
            startbtn1.setImageDrawable(getResources().getDrawable(R.mipmap.stop));
            aliPlayer.start();
            startState = false;
        } else {
            startBtn.setImageDrawable(getResources().getDrawable(R.mipmap.start));
            startbtn1.setImageDrawable(getResources().getDrawable(R.mipmap.start));
            aliPlayer.pause();
            startState = true;
        }
    }

    //状态栏隐藏显示
    private void HideStausbar(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params);

           }
//        else {
//            WindowManager.LayoutParams attrs = getWindow().getAttributes();
//            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            getWindow().setAttributes(attrs);
//        }
    }

    //播放屏幕触摸单双击判断
    private void ClickIF() {
        //单双击判断
        if (!Backkey) {
            HideStausbar(true);
            control.setVisibility(View.GONE);
            toulinear.setVisibility(View.GONE);
            startbtn1.setVisibility(View.GONE);
            Backkey = true;//将布尔值设置为true
//            new Timer().schedule( //执行定时器操作 1000号码后将布尔值设置为false
//                    new TimerTask() {
//                        @Override
//                        public void run() {
//                            Backkey = false;
//                        }
//                    }, 1000
//            );
        } else {
            HideStausbar(true);
            control.setVisibility(View.VISIBLE);
            toulinear.setVisibility(View.VISIBLE);
            startbtn1.setVisibility(View.VISIBLE);
            HideStausbar(false);
            Backkey = false;
        }
    }

    /**************************播放器相关监听*****************************/
    //播放器绑定Surface
    class SurfaceBinding implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            aliPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            aliPlayer.redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            aliPlayer.setDisplay(null);
        }
    }

    //播放器完成事件
    class PlayCompletion implements IPlayer.OnCompletionListener {
        @Override
        public void onCompletion() {
            Log.i("AAA", "播放器完成");
        }
    }

    //播放器出错事件
    class PlayError implements IPlayer.OnErrorListener {

        @Override
        public void onError(ErrorInfo errorInfo) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "加载失败请重新打开！", Toast.LENGTH_SHORT).show();
        }
    }

    //播放器准备完毕事件
    class PlayPrepared implements IPlayer.OnPreparedListener {
        @Override
        public void onPrepared() {
            aliPlayer.start();

            long duration = aliPlayer.getDuration();
            seekBar.setMax((int) duration);
            String t = counttime((int) duration);
            newtime.setText(t);
            startBtn.setEnabled(true);
            startbtn1.setEnabled(true);
        }
    }

    //其他信息事件
    class PlayInfoListener implements IPlayer.OnInfoListener {

        @Override
        public void onInfo(InfoBean infoBean) {
            //当前播放进度
            if (infoBean.getCode() == InfoCode.CurrentPosition) {
                long ntime = infoBean.getExtraValue();
                String nt = counttime((int) ntime);
                numtime.setText(nt);
                seekBar.setProgress((int) ntime);
            }
            //缓冲进度
            if (infoBean.getCode() == InfoCode.BufferedPosition) {
                long btime = infoBean.getExtraValue();
                seekBar.setSecondaryProgress((int) btime);
            }
        }
    }

    //首帧渲染完成事件
    class PlayRendering implements IPlayer.OnRenderingStartListener {

        @Override
        public void onRenderingStart() {
            Log.i("AAA", "首帧渲染完毕");
            fen.setVisibility(View.VISIBLE);
            StartTime();
        }
    }

    //拖动结束
    class PlaySeekComplete implements IPlayer.OnSeekCompleteListener {

        @Override
        public void onSeekComplete() {
            Log.i("AAA", "拖动结束");
        }
    }

    //播放器状态改变事件
    class PlayStateChanged implements IPlayer.OnStateChangedListener {
        @Override
        public void onStateChanged(int i) {
            Log.i("AAA", "播放器状态改变" + i);
            switch (i) {
                case 1:
                    progressBar.setVisibility(View.VISIBLE);

                    break;
                case 3:
                    startBtn.setImageDrawable(getResources().getDrawable(R.mipmap.stop));
                    startbtn1.setImageDrawable(getResources().getDrawable(R.mipmap.stop));
                    break;
                case 4:
                    startBtn.setImageDrawable(getResources().getDrawable(R.mipmap.start));
                    startbtn1.setImageDrawable(getResources().getDrawable(R.mipmap.start));
                    break;
                case 7:
                    Toast.makeText(getApplicationContext(), "视频发生错误", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    progressBar.setVisibility(View.GONE);
                    startbtn1.setVisibility(View.GONE);
                    break;
            }
        }
    }





    /****************************控制台相关事件*********************************/
    //开始按钮点击
    private boolean startState = true;

    class StartBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            playstate();
        }
    }

    ///进度条拖动
    private boolean seekbarDrag = true;

    class SeekBardrag implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i("PPP", "拖动中");
            seekbarDrag = false;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i("PPP", "停止拖动");
            seekbarDrag = true;
            aliPlayer.seekTo(seekBar.getProgress());
            Log.i("PPP", "手动拖动结束");
        }
    }

    //时间转换
    private String counttime(int t) {
        int t1 = t / 1000;
        int m1 = t1 / 60;
        int s1 = t1 % 60;
        String m2 = String.valueOf(m1);
        if (m1 < 10) {
            m2 = "0" + m1;
        }
        String s2 = String.valueOf(s1);
        if (s1 < 10) {
            s2 = "0" + s1;
        }

        return m2 + ":" + s2;
    }

    //全屏点击
    private int playheigh;

    class QuanClick implements View.OnClickListener {
        @SuppressLint("SourceLockedOrientationActivity")
        @Override
        public void onClick(View v) {
            int cg = getResources().getConfiguration().orientation;
            if (cg != Configuration.ORIENTATION_LANDSCAPE) {
                //转横
                heng();
//                playheigh = surface.getHeight();
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                ViewGroup.LayoutParams params = relative.getLayoutParams();
//                params.height = widtht + xnbtnheight;
//                relative.setLayoutParams(params);

            } else {
                //转竖
                shu();
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                ViewGroup.LayoutParams params = relative.getLayoutParams();
//                params.height = playheigh;
//                relative.setLayoutParams(params);

            }

        }
    }
    //横竖屏切换
    private void  heng(){
        playheigh = surface.getHeight();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup.LayoutParams params = relative.getLayoutParams();
        params.height = widtht ;
        params.width = height +xnbtnheight;
        relative.setLayoutParams(params);
    }
    private void  shu(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewGroup.LayoutParams params = relative.getLayoutParams();
        params.height = playheigh;
        params.width = widtht;
        relative.setLayoutParams(params);
    }
    //播放屏幕触摸
    private boolean Backkey = false;

    //屏幕点击及滑动
    class PlayTouch implements View.OnTouchListener {
        private float x;
        private float xx;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int ev = event.getAction();
            if (ev == MotionEvent.ACTION_DOWN) {
                x = event.getX();
                ClickIF();
            }
            if(ev==MotionEvent.ACTION_MOVE){
                float mx = event.getX();
                xx = mx - x;
                Log.i("PPP",xx+"滑动中");
            }
            if (ev==MotionEvent.ACTION_UP){

                if(xx<0){//向后
                    if(Math.abs(xx)<100){
                        return true;
                    }
                     int n = (int) (seekBar.getProgress()-(1000*10));
                    if (n<0) {
                        n = 0;
                    }
                     aliPlayer.seekTo(n);
                }
                if(xx>50){//向前
                    if(Math.abs(xx)<100){
                        return true;
                    }
                    if(xx>=seekBar.getMax()){
                        aliPlayer.seekTo(seekBar.getMax());
                    }else {
                        int s = (int) (seekBar.getProgress() + (1000*10));
                        aliPlayer.seekTo(s);
                    }
                }
            }
            Log.i("PPP",ev+"");
            return true;
        }

    }

    //控制台触摸
    class Control implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            return true;
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
