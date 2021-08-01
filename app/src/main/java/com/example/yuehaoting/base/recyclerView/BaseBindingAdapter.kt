package com.example.yuehaoting.base.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.yuehaoting.base.log.LogT
import com.example.yuehaoting.databinding.FragmentHifini2AdapterABinding


/**
 * 作者: 天使
 * 时间: 2021/7/15 13:55
 * 描述:
 */
abstract class BaseBindingAdapter<M,  B : ViewBinding?>(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mContext: Context = context

    //没有数据显示的布局
     private lateinit var bindingA: FragmentHifini2AdapterABinding

    private var items: ObservableArrayList<M>? = null
    private val itemsChangeCallback: ListChangedCallback<M>? = null
    private var mOnItemClickListener: OnItemClickListener<M>? = null

    inner class ViewHolderA(view: View) : RecyclerView.ViewHolder(view)
    inner class ViewHolderB(view: View) : RecyclerView.ViewHolder(view)
    inner class ViewHolderC(view: View) : RecyclerView.ViewHolder(view)


    protected abstract fun onBindItem(binding: B, bean: M, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
      return when (viewType) {
          typeFooterView -> {
             if (getFooterViewLayoutResId()==0){
                  val layoutInflater = LayoutInflater.from(context)
                  bindingA = FragmentHifini2AdapterABinding.inflate(layoutInflater, parent, false)

                 return ViewHolderA(bindingA.root)
              }

                  val viewDataBinding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getFooterViewLayoutResId(), parent, false)
                  return ViewHolderA(viewDataBinding.root)

          }
          typeHeaderView -> {
              if (getHeaderViewLayoutResId() != 0) {
                  val viewDataBinding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getHeaderViewLayoutResId(), parent, false)
                  return ViewHolderB(viewDataBinding.root)
              }
              ViewHolderB(LayoutInflater.from(mContext).inflate(getHeaderViewLayoutResId(), parent, false))
          }
          else -> {
              val binding: B = DataBindingUtil.inflate(LayoutInflater.from(mContext), getLayoutResId(), parent, false)
              ViewHolderC(binding!!.root)
          }
      }
    }

    override fun onBindViewHolder(holder:RecyclerView.ViewHolder, position: Int) {
        var mPosition=position
        try {
            if (holder is BaseBindingAdapter<*, *>.ViewHolderC) {
                if (getHeaderViewLayoutResId() != 0 && position > 0) {
                   --mPosition
                }
                val bean = items!![mPosition]
                val binding: B? = DataBindingUtil.getBinding(holder.itemView)
                if (binding != null) {
                    if (mOnItemClickListener != null) {
                        binding.root.setOnClickListener { mOnItemClickListener!!.onItemClick(bean, holder.adapterPosition) }
                    }
                }
                onBindItem(binding!!, bean, mPosition)
            } else if (holder is BaseBindingAdapter<*, *>.ViewHolderA) {
                if (isLoadingMore) {
                bindingA.tvFragment2AdapterA.text = "正在加载..."
                } else {
                    bindingA.tvFragment2AdapterA.text = "暂无更多数据"
                }
            }

        }catch (e:Exception){

        }
    }

    override fun getItemCount(): Int {
        var count = items!!.size

        /*如果有HeaderView,这数量+1*/

        /*如果有HeaderView,这数量+1*/
        if (getHeaderViewLayoutResId() != 0) {
            count++
        }
        /*如果有FooterView,数量+1*/
        /*如果有FooterView,数量+1*/
        return if (isShowFooterView) {
            ++count
        } else {
            count
        }
    }
    private val isShowFooterView = false
    /**
     * 是否正在 加载更多
     */
    private val isLoadingMore = false
    /**
     * 判断viewType 是不是FooterView
     */
    private val typeFooterView = 1

    /**
     * 判断viewType 是不是HeaderView
     */
    private val typeHeaderView = 2

    /**
     * 使用FootView，默认为0，表示没有，如果有，则重写该方法，返回R.layout.xxx
     */
    @LayoutRes
    protected open fun getFooterViewLayoutResId(): Int {
        return 0
    }

    /**
     * 使用HeaderView，默认为0，表示没有，如果有，则重写该方法，返回R.layout.xxx
     */
    @LayoutRes
    protected open fun getHeaderViewLayoutResId(): Int {
        return 0
    }


    @LayoutRes
    protected abstract fun getLayoutResId(): Int


    /**
     * 额外监听数据变化，这样就可以直接更新数据，
     * 而不用每次都调用notifyDataSetChanged()等方式更新数据
     *
     *
     * 我只对插入和移除做了监听处理，其他方式没有处理
     * 小伙伴可以根据自己的具体需要做额外处理
     */
   inner class ListChangedCallback<M> : OnListChangedCallback<ObservableArrayList<M?>>() {
        override fun onChanged(newItems: ObservableArrayList<M?>) {
            LogT.i("onChanged:$newItems")
        }

        override fun onItemRangeChanged(newItems: ObservableArrayList<M?>, positionStart: Int, itemCount: Int) {
            LogT.i("onItemRangeChanged:$positionStart--$itemCount,$newItems")
        }

        override fun onItemRangeInserted(newItems: ObservableArrayList<M?>, positionStart: Int, itemCount: Int) {
//            LogT.i("onItemRangeInserted:" + positionStart + "--" + itemCount + "," + newItems.toString());
            /*当有头部信息的时候，由于整体position偏移了，所以需要往后挪一位*/
            var mPositionStart = positionStart
            if (getHeaderViewLayoutResId() != 0) {
                mPositionStart++
            }
            notifyItemRangeInserted(mPositionStart, itemCount)
        }

        override fun onItemRangeMoved(newItems: ObservableArrayList<M?>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            LogT.i("onItemRangeMoved:$fromPosition--$toPosition,itemCount:$itemCount,$newItems")
        }

        override fun onItemRangeRemoved(newItems: ObservableArrayList<M?>, positionStart: Int, itemCount: Int) {
            var mPositionStart = positionStart
            LogT.i("onItemRangeRemoved:$mPositionStart--$itemCount,$newItems")
            //            if (itemCount > 1) {
//
//                /*当有头部信息的时候，由于整体position偏移了，所以需要往后挪一位*/
            if (getHeaderViewLayoutResId() != 0) {
                mPositionStart++
            }
            notifyItemRangeRemoved(mPositionStart, itemCount)
        }

    }
    interface OnItemClickListener<M> {
        fun onItemClick(bean: M, position: Int)
    }

}