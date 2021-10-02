package com.example.yuehaoting.base.lib_search_history.utils;

/**
 * 作者: LiangKe 时间: 2021/10/1 10:01 描述:
 */
public interface OnFoldChangedListener {

  /**
   * 折叠状态时时回调
   *
   * @param canFold      是否可以折叠，true为可以折叠，false为不可以折叠
   * @param fold         当前折叠状态，true为折叠，false为未折叠
   * @param index        当前显示的view索引数量
   * @param surplusWidth 折叠状态下 剩余空间
   */
  void onFoldChange(boolean canFold, boolean fold, int index, int surplusWidth);

}
