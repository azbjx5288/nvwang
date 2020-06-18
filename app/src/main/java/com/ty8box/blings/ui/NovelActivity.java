package com.neinei.cong.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.neinei.cong.AppContext;
import com.neinei.cong.R;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.ui.adapter.ViewPagerAdapter;
import com.neinei.cong.ui.fragment.NovelFragment;

import java.util.ArrayList;
import java.util.List;

public class NovelActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    TabLayout mTabLayout;
    ViewPager mViewPager;
    TextView tvTitle;

    private ViewPagerAdapter mAdapter;
    private List<String> mTitles = new ArrayList<>();
    private List<Fragment> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);
        initView();
    }

    public void initView() {
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewpager);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("原创小说");

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setTabViewPager();
    }

    /**
     * 设置Tablayout
     */
    private void setTabViewPager() {

        if (AppContext.novelTermList == null) return;

        for (int i = 0; i < AppContext.novelTermList.size(); i++) {
            mTitles.add(AppContext.novelTermList.get(i).getName());
            creatFragment(AppContext.novelTermList.get(i).getTerm_id());
        }

        mAdapter = new ViewPagerAdapter(this.getSupportFragmentManager(), this, mList, mTitles);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.text_black), ContextCompat.getColor(this, R.color.main_color));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.main_color));
        mTabLayout.setOnTabSelectedListener(this);

    }

    private void creatFragment(String i) {
        NovelFragment f = new NovelFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", i);
        f.setArguments(bundle);
        mList.add(f);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            if (mTabLayout.getTabAt(i) == tab) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
