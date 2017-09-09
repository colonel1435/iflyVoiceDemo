package com.example.iflyvoicedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.iflyvoicedemo.adapter.FragmentPageAdapter;
import com.example.iflyvoicedemo.fragment.HistoryFragment;
import com.iflytek.cloud.SpeechConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import github.chenupt.springindicator.SpringIndicator;

public class HistotyActivity extends AppCompatActivity {

    @BindView(R.id.iv_back_history)
    ImageView ivBack;
    @BindView(R.id.vp_container_history)
    ViewPager mViewpager;
    @BindView(R.id.history_indicator)
    SpringIndicator historyIndicator;
    private Context mContext;
    private List<String> mTitle;
    private List<Fragment> mPages;
    private FragmentPageAdapter mPageAdapter;
    private MyPageChangeListener mPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histoty);
        ButterKnife.bind(this);
        mContext = this;

        initData();
        initView();
    }

    private void initView() {
//        toolbar.setTitle(getString(R.string.chart_statistic));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HistoryFragment offlinePage = HistoryFragment.newInstance("0", SpeechConstant.TYPE_LOCAL);
        mPages.add(offlinePage);

        HistoryFragment onlinePage = HistoryFragment.newInstance("1", SpeechConstant.TYPE_CLOUD);
        mPages.add(onlinePage);

        FragmentManager fm = getSupportFragmentManager();
        mPageAdapter = new FragmentPageAdapter(fm, mPages, mTitle);
        mViewpager.setAdapter(mPageAdapter);
        mPageChangeListener = new MyPageChangeListener();
        mViewpager.addOnPageChangeListener(mPageChangeListener);

        historyIndicator.setViewPager(mViewpager);
    }

    private void initData() {
        mTitle = new ArrayList<>();
        mTitle.add(getString(R.string.title_offline));
        mTitle.add(getString(R.string.title_online));

        mPages = new ArrayList<>();
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
