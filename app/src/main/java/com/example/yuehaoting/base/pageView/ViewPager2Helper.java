package com.example.yuehaoting.base.pageView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import com.example.yuehaoting.R;
import com.example.yuehaoting.mian.fragment1.newSongRecommendationFragment.BaseFragmentNewSongRecommendation;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import net.lucode.hackware.magicindicator.MagicIndicator;

/**
 * 作者: LiangKe 时间: 2021/9/7 1:38 描述:
 */
public class ViewPager2Helper {
  public static void bind(final MagicIndicator magicIndicator, ViewPager2 viewPager2, FragmentManager childFragmentManager, Intent intent) {
   // viewPager.addOnAttachStateChangeListener();
    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
      @SuppressLint("LongLogTag")
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        Log.v("OnPageChangeCallback()onPageScrolled ",String.valueOf(position) );

      if (childFragmentManager.getFragments().size()>position){
        Fragment fragment = childFragmentManager.getFragments().get(position);
       // updatePagerHeightForChild(Objects.requireNonNull(fragment.getView()),viewPager2);
      }
      }
      @SuppressLint("LongLogTag")
      @Override
      public void onPageSelected(int position) {
        magicIndicator.onPageSelected(position);
        Log.v("OnPageChangeCallback()onPageSelected ",String.valueOf(position) );
        /*
        每次到第一页就禁止滚动
         */
        viewPager2.setUserInputEnabled(position != 0);
       intent.putExtra("fragment",position); //intent 传到 MainActivity 判断滑动使用

      }

      @SuppressLint("LongLogTag")
      @Override
      public void onPageScrollStateChanged(int state) {
        magicIndicator.onPageScrollStateChanged(state);
        Log.v("OnPageChangeCallback()onPageScrollStateChanged ",String.valueOf(state) );
      }

      void updatePagerHeightForChild(View view,  ViewPager2 pager) {
        view.post(new Runnable() {
          @SuppressLint("LongLogTag")
          @Override
          public void run() {
            int widthMeasureSpec = MeasureSpec.makeMeasureSpec(view.getWidth(), MeasureSpec.EXACTLY);

            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

            view.measure(widthMeasureSpec,heightMeasureSpec);

            if (pager.getLayoutParams().height != view.getMeasuredHeight()){
              Log.v("OnPageChangeCallback()获取当前fragment的高度 ",String.valueOf( view.getMeasuredHeight() ));

                 pager.getLayoutParams().height = view.getMeasuredHeight();

                 pager.requestLayout();  //把每页通知给父布局

              //todo 布局显示异常
              Log.v("OnPageChangeCallback()当前viewpage高度 ",String.valueOf(  pager.getLayoutParams().height ));

            }
          }
        });
      }

    });
  }
}