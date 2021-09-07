package com.example.yuehaoting.base.pageView;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Timer;
import net.lucode.hackware.magicindicator.MagicIndicator;

/**
 * 作者: LiangKe 时间: 2021/9/7 1:38 描述:
 */
public class ViewPager2Helper {
  public static void bind(final MagicIndicator magicIndicator, ViewPager2 viewPager) {
   // viewPager.addOnAttachStateChangeListener();
    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
      @SuppressLint("LongLogTag")
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        Log.v("OnPageChangeCallback()onPageScrolled ",String.valueOf(position) );

      }

      @SuppressLint("LongLogTag")
      @Override
      public void onPageSelected(int position) {
        magicIndicator.onPageSelected(position);
        Log.v("OnPageChangeCallback()onPageSelected ",String.valueOf(position) );
        viewPager.setUserInputEnabled(position != 0);

      }

      @SuppressLint("LongLogTag")
      @Override
      public void onPageScrollStateChanged(int state) {
        magicIndicator.onPageScrollStateChanged(state);
        Log.v("OnPageChangeCallback()onPageScrollStateChanged ",String.valueOf(state) );


      }
    });
  }
}