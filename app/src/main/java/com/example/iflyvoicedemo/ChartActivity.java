package com.example.iflyvoicedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.iflyvoicedemo.adapter.FragmentPageAdapter;
import com.example.iflyvoicedemo.fragment.ChartFragment;
import com.iflytek.cloud.SpeechConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChartActivity extends AppCompatActivity {
    @BindView(R.id.tl_chart)
    TabLayout mTablayout;
    @BindView(R.id.vp_container)
    ViewPager mViewpager;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private Context mContext;
    private List<String> mTitle;
    private List<Fragment> mPages;
    private FragmentPageAdapter mPageAdapter;
    private MyPageChangeListener mPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        mContext = this;

        initData();
        initView();

    }

    private void initView() {
//        toolbar.setTitle(getString(R.string.chart_statistic));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChartFragment offlinePage = ChartFragment.newInstance("0", SpeechConstant.TYPE_LOCAL);
        mPages.add(offlinePage);

        ChartFragment onlinePage = ChartFragment.newInstance("1", SpeechConstant.TYPE_CLOUD);
        mPages.add(onlinePage);

        FragmentManager fm = getSupportFragmentManager();
        mPageAdapter = new FragmentPageAdapter(fm, mPages, mTitle);
        mViewpager.setAdapter(mPageAdapter);
        mTablayout.setupWithViewPager(mViewpager);
        mPageChangeListener = new MyPageChangeListener();
        mViewpager.addOnPageChangeListener(mPageChangeListener);
    }

    private void initData() {
        mTitle = new ArrayList<>();
        mTitle.add(getString(R.string.title_offline));
        mTitle.add(getString(R.string.title_online));

        mPages = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mViewpager.setCurrentItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
