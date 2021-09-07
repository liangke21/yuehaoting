package com.example.yuehaoting.base.pageView;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Objects;
import java.util.Timer;
import net.lucode.hackware.magicindicator.MagicIndicator;

/**
 * 作者: LiangKe 时间: 2021/9/7 1:38 描述:
 */
public class ViewPager2Helper {
  public static void bind(final MagicIndicator magicIndicator, ViewPager2 viewPager, FragmentManager childFragmentManager) {
   // viewPager.addOnAttachStateChangeListener();
    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
      @SuppressLint("LongLogTag")
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        Log.v("OnPageChangeCallback()onPageScrolled ",String.valueOf(position) );

      if (childFragmentManager.getFragments().size()>position){
        Fragment fragment = childFragmentManager.getFragments().get(position);
        if (fragment.getView() !=null){
          Fragment fragment1 = childFragmentManager.getFragments().get(position);
          updatePagerHeightForChild(Objects.requireNonNull(fragment1.getView()),viewPager);
        }

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
        viewPager.setUserInputEnabled(position != 0);


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
              Log.v("OnPageChangeCallback()updatePagerHeightForChild ",String.valueOf( view.getMeasuredHeight() ));
              (pager.getLayoutParams()).height = view.getMeasuredHeight();


            }
          }
        });
      }

    });
  }
}