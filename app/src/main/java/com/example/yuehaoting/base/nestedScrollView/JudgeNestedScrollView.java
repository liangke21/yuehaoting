package com.example.yuehaoting.base.nestedScrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

/**
 * @Created SiberiaDante
 * @Describe：
 * @CreateTime: 2017/12/17
 * @UpDateTime:
 * @Email: 2654828081@qq.com
 * @GitHub: https://github.com/SiberiaDante
 */

public class JudgeNestedScrollView extends NestedScrollView {
    private boolean isNeedScroll = true;
    private float xDistance, yDistance, xLast, yLast;
    private int scaledTouchSlop;

    public JudgeNestedScrollView(Context context) {
        super(context, null);
    }

    public JudgeNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public JudgeNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * <拦截所有触摸屏>
     *   　　　　return true: ViewGroup 将该事件拦截，交给自己的 onTouchEvent 处理。
     *
     * 　　　　return false: 继续传递给子元素的 dispatchTouchEvent 处理。
     *
     * 　　　　return super.dispatherTouchEvent: 事件默认不会被拦截。
     * </拦截所有触摸屏>
     * 实现此方法以拦截所有触摸屏运动事件。 这允许您在事件发送给您的孩子时观看事件，并随时掌握当前手势的所有权。
     * 使用这个函数需要小心，因为它与View.onTouchEvent(MotionEvent)有一个相当复杂的交互，使用它需要以正确的方式实现该方法以及这个方法。 事件将按以下顺序接收：
     * 您将在此处收到 down 事件。
     * down 事件要么由这个视图组的子视图处理，要么交给你自己的 onTouchEvent() 方法来处理； 这意味着您应该实现 onTouchEvent() 以返回 true，因此您将继续看到手势的其余部分（而不是寻找父视图来处理它）。 此外，通过从 onTouchEvent() 返回 true，您将不会在 onInterceptTouchEvent() 中收到任何后续事件，并且所有触摸处理都必须像往常一样在 onTouchEvent() 中进行。
     * 只要您从此函数返回 false，每个后续事件（直到并包括最终事件）将首先在此处传递，然后再传递到目标的 onTouchEvent()。
     * 如果你从这里返回true，您将不会收到任何下列事件：目标视图将收到相同的事件，而是用行动MotionEvent.ACTION_CANCEL ，以及所有事件将被传递到您的onTouchEvent（）方法，并不再出现在这里。
     * 参数：
     * @param ev – 在层次结构中向下调度的运动事件。
     * @return 返回 true 以从孩子那里窃取运动事件，并通过 onTouchEvent() 将它们分派到此 ViewGroup。 当前目标会收到一个 ACTION_CANCEL 事件，这里不会再传递消息
     */
   @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
/*        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                Log.e("SiberiaDante", "xDistance ：" + xDistance + "---yDistance:" + yDistance);
                return !(xDistance >= yDistance || yDistance < scaledTouchSlop) && isNeedScroll;*/

            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN: Log.v("手指按下手势已开始%s", String.valueOf(MotionEvent.ACTION_DOWN));

                case MotionEvent.ACTION_MOVE:{
                    Timber.v("手指按下手势期间发生了变化%s",MotionEvent.ACTION_MOVE);
                }
                case MotionEvent.ACTION_UP:{
                    Timber.v("手指按下手势已完成%s",MotionEvent.ACTION_UP);
                }
            }

       //tODO 触点滑动反馈 交给子类
        return false; /*重点 */

    }

    /*
    该方法用来处理NestedScrollView是否拦截滑动事件
     */
    public void setNeedScroll(boolean isNeedScroll) {
        this.isNeedScroll = isNeedScroll;
    }


    @Override
    public void onNestedScroll(@NonNull @NotNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);



    }

/*    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN: Log.v("手指触碰到%s","");

            case MotionEvent.ACTION_MOVE:{
                Timber.v("手指在移动%s");
            }
            case MotionEvent.ACTION_UP:{
                Timber.v("手指松开屏幕%s");
            }
        }

        return super.onTouchEvent(ev);

    }*/
}
