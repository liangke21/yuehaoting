package com.example.yuehaoting.base.recyclerView.baseAdapter;

import android.view.View;

/**
 * Created by taeja on 16-1-28.
 */

public interface OnItemClickListener {

  void onItemClick(View view, int position);

  void onItemLongClick(View view, int position);
}
