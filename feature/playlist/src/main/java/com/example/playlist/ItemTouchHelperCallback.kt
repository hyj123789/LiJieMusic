package com.example.playlist

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(private val adapter: SongAdapter) : ItemTouchHelper.Callback() {
    //设置支持拖拽和滑动的方向
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START
        return makeMovementFlags(dragFlags,swipeFlags)
    }
    //拖拽回调
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.moveItem(viewHolder.bindingAdapterPosition,target.bindingAdapterPosition)
        return true
    }
    //滑动回调
    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int
    ) {
        adapter.removeItem(viewHolder.bindingAdapterPosition)
    }
    override fun isLongPressDragEnabled(): Boolean = true
    override fun isItemViewSwipeEnabled(): Boolean = true
}