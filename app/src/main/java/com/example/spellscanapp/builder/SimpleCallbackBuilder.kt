package com.example.spellscanapp.builder

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SimpleCallbackBuilder(dragDirs: Int, swipeDirs: Int) {

    private var callback: Callback

    init {
        callback = Callback(dragDirs, swipeDirs)
    }

    fun onSwipedStart(block: (viewHolder: RecyclerView.ViewHolder) -> Unit): SimpleCallbackBuilder {
        callback.onSwipedStart = block
        return this
    }

    fun onChildDraw(
        block: (
            c: Canvas,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
        ) -> Unit
    ): SimpleCallbackBuilder {
        callback.onChildDraw = block
        return this
    }

    fun build(): ItemTouchHelper.SimpleCallback {
        return callback
    }

    class Callback(
        dragDirs: Int,
        swipeDirs: Int,
    ) : ItemTouchHelper.SimpleCallback(
        dragDirs,
        swipeDirs
    ) {

        var onSwipedStart: ((viewHolder: RecyclerView.ViewHolder) -> Unit)? = null
        var onChildDraw: ((
            c: Canvas,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
        ) -> Unit)? = null

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == ItemTouchHelper.RIGHT) {
                onSwipedStart?.invoke(viewHolder)
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            onChildDraw?.invoke(c, viewHolder, dX)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}