package com.example.yuehaoting.base.lib_search_history.jd;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.example.yuehaoting.R;
import com.example.yuehaoting.base.lib_search_history.FlowListView;
import com.example.yuehaoting.base.lib_search_history.utils.Utils;
import timber.log.Timber;

/**
 * @author zwl
 * @describe 折叠
 * @date on 2021/8/7
 */
public class JDFoldLayout extends FlowListView  {

  private View upFoldView;
  private View downFoldView;

  public JDFoldLayout(Context context) {
    this(context, null);
  }

  public JDFoldLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public JDFoldLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);


    upFoldView = LayoutInflater.from(context).inflate(R.layout.view_item_fold_up, null);  //展开
    downFoldView = LayoutInflater.from(context).inflate(R.layout.view_item_fold_down, null); //折叠
    upFoldView.setOnClickListener(v -> {
      mFold = false;
      flowAdapter.notifyDataChanged();
    });

    downFoldView.setOnClickListener(v -> {
      mFold = true;
      flowAdapter.notifyDataChanged();
    });
  }

  @Override
  public void onFoldChange(boolean canFold, boolean fold, int index, int surplusWidth) {
    Timber.v("实现接口");

    Timber.v("设置折叠状态回调  %s  %s  %s  %s",canFold, fold, index, surplusWidth);
    if (canFold) {
      Utils.removeFromParent(downFoldView);
      addView(downFoldView);
      if (fold) {
        Utils.removeFromParent(upFoldView);
        int upIndex = index(index, surplusWidth);
        addView(upFoldView, upIndex);
      } else {
        Utils.removeFromParent(downFoldView);
        addView(downFoldView);
      }
    }
  }

  private int index(int index, int surplusWidth) {
    int upIndex = index;
    int upWidth = Utils.getViewWidth(upFoldView);
    //当剩余空间大于等于展开View宽度直接加入index+1
    if (surplusWidth >= upWidth) {
      upIndex = index + 1;
    } else { //找到对应的位置
      for (int i = index; i >= 0; i--) {
        View view = getChildAt(index);
        int viewWidth = Utils.getViewWidth(view);
        upWidth -= viewWidth;
        if (upWidth <= 0) {
          upIndex = i;
          break;
        }
      }
    }
    return upIndex;
  }


}
