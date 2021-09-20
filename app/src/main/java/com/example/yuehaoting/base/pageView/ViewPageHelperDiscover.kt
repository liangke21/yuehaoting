package com.example.yuehaoting.base.pageView

import android.content.Intent
import androidx.viewpager.widget.ViewPager
import net.lucode.hackware.magicindicator.MagicIndicator

/**
 * 作者: LiangKe
 * 时间: 2021/9/20 15:42
 * 描述:
 */
object ViewPageHelperDiscover {

    fun bind( magicIndicator: MagicIndicator,viewpager: ViewPager,intent: Intent){
        viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                magicIndicator.onPageSelected(position)
                intent.putExtra("discover", position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                magicIndicator.onPageScrollStateChanged(state)
            }
        })
    }
}