package com.example.administrator.ebook;

import java.util.ArrayList;
import java.util.List;


import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;

    private ViewPager viewPager;

    /**
     * Tab显示内容TextView
     */
    private TextView TabDownload,TabBook;

    /**
     * Tab的那个引导线
     */
    private ImageView mTabLine;

    /**
     * Fragment
     */
    private FragmentBook mBookFg;
    private FragmentDownload mDownloadFg;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    private LinearLayout.LayoutParams lineParam ;

    private DrawerLayout drawerLayout;

    private Button bt_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initTextView();
        initViewPager();


    }

    private void init(){

        mTabLine = (ImageView)findViewById(R.id.tab_line);
        viewPager = (ViewPager)findViewById(R.id.viewPaper);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        bt_me = (Button)findViewById(R.id.bt_me);

        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;

        lineParam =  (LinearLayout.LayoutParams) mTabLine
                .getLayoutParams();
        lineParam.leftMargin = screenWidth/2;
        mTabLine.setLayoutParams(lineParam);
    }

    public void initViewPager(){
        mBookFg = new FragmentBook();
        mDownloadFg = new FragmentDownload();
        mFragmentList.add(mDownloadFg);
        mFragmentList.add(mBookFg);

        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        viewPager.setAdapter(mFragmentAdapter);
        currentIndex = 1;
        viewPager.setCurrentItem(1);
        TabBook.setTextColor(getResources().getColor(R.color.gray_shen));

        bt_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    public void initTextView(){
        TabBook = (TextView)findViewById(R.id.tab_book);
        TabDownload = (TextView)findViewById(R.id.tab_download);
        TabDownload.setOnClickListener(new MyOnClickListener(0));
        TabBook.setOnClickListener(new MyOnClickListener(1));
    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }

            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,int offsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();

                switch (position) {
                    case 0:
                        if(currentIndex == 1){
                            //translationX屏幕正中央为0
                            ObjectAnimator animator = ObjectAnimator.ofFloat(mTabLine, "translationX", 0, -lineParam.width);
                            animator.setDuration(500);
                            animator.start();
                        }
                        TabDownload.setTextColor(getResources().getColor(R.color.gray_shen));
                        break;
                    case 1:
                        if(currentIndex == 0){
                            ObjectAnimator animator = ObjectAnimator.ofFloat(mTabLine, "translationX", -lineParam.width, 0);
                            animator.setDuration(500);
                            animator.start();
                        }
                        TabBook.setTextColor(getResources().getColor(R.color.gray_shen));
                        break;
                }
                currentIndex = position;
            }

    }

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }
    /**
     * 重置颜色
     */
    private void resetTextView() {
        TabBook.setTextColor(getResources().getColor(R.color.gray_qian));
        TabDownload.setTextColor(getResources().getColor(R.color.gray_qian));
    }

}
