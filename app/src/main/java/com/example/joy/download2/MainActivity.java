package com.example.joy.download2;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.joy.download2.ui.FirstFragment;
import com.example.joy.download2.ui.FourFragment;
import com.example.joy.download2.ui.SencondFragment;
import com.example.joy.download2.ui.ThirdFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> mListTitle = new ArrayList<>();

    private List<Fragment> mListFragments = new ArrayList<>();

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //去掉阴影
//        getSupportActionBar().setElevation(0);

        initData();
        initView();
    }

    private void initData() {
        mListTitle.add(getResources().getString(R.string.string_first));
        mListTitle.add(getResources().getString(R.string.string_second));
        mListTitle.add(getResources().getString(R.string.string_third));
        mListTitle.add(getResources().getString(R.string.string_four));

        mListFragments.add(new FirstFragment());
        mListFragments.add(new SencondFragment());
        mListFragments.add(new ThirdFragment());
        mListFragments.add(new FourFragment());


    }

    private void initView() {
        mTabLayout = findViewById(R.id.mTablayout);
        mViewPager = findViewById(R.id.mViewPager);

        //预加载
        mViewPager.setOffscreenPageLimit(mListFragments.size());

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mListFragments.get(position);
            }

            @Override
            public int getCount() {
                return mListFragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mListTitle.get(position);
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
    }


}
