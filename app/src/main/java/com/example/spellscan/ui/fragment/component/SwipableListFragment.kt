package com.example.spellscan.ui.fragment.component

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscan.R
import com.example.spellscan.builder.SimpleCallbackBuilder
import com.example.spellscan.databinding.FragmentSwipableListBinding
import com.example.spellscan.logger.TAG
import com.google.android.material.color.MaterialColors
import kotlin.math.abs
import kotlin.math.roundToInt

class SwipableListFragment(
    private val adapter: RecyclerView.Adapter<*>,
    private val onCardDeleted: (Int) -> Unit,
    private val onCardSearch: (Int) -> Unit
) : Fragment() {
    private lateinit var binding: FragmentSwipableListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSwipableListBinding.inflate(inflater, container, false)

        binding.cardListView.layoutManager = LinearLayoutManager(context)

        binding.cardListView.adapter = adapter

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

        val deleteIcon = ResourcesCompat.getDrawable(
            resources, R.drawable.delete_icon,
            context?.theme
        )

        val searchIcon = ResourcesCompat.getDrawable(
            resources, R.drawable.search_icon,
            context?.theme
        )

        val callback = SimpleCallbackBuilder(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)
            .onSwipedStart { viewHolder ->
                onCardDeleted(viewHolder.adapterPosition)
            }
            .onSwipedEnd { viewHolder ->
                onCardSearch(viewHolder.adapterPosition)
            }
            .onChildDraw { c, viewHolder, dX ->
                onChildDraw(c, viewHolder, dX, deleteIcon, searchIcon, width)
            }
            .build()

        val swipeHelper = ItemTouchHelper(callback)

        swipeHelper.attachToRecyclerView(binding.cardListView)

        return binding.root
    }

    private fun onChildDraw(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        deleteIcon: Drawable?,
        searchIcon: Drawable?,
        width: Int
    ) {
        drawTileBackground(c, viewHolder, dX, width)

        if (deleteIcon == null || searchIcon == null) {
            Log.w(TAG, "deleteIcon or searchIcon is null")
            return
        }

        drawTileIcons(viewHolder, deleteIcon, searchIcon, width)

        drawIconByDirection(c, dX, deleteIcon, searchIcon)
    }

    private fun drawTileBackground(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        width: Int
    ) {
        val color = when {
            dX > 0 -> getMaterialColor(R.attr.delete_background_color, Color.RED)
            dX < 0 -> getMaterialColor(R.attr.search_background_color, Color.BLUE)
            else -> Color.TRANSPARENT
        }

        ColorDrawable(color).also {
            it.setBounds(
                viewHolder.itemView.left,
                viewHolder.itemView.top,
                viewHolder.itemView.right,
                viewHolder.itemView.bottom
            )
            it.alpha = adjustOpacityOnX(dX, width)
            it.draw(c)
        }
    }

    private fun drawTileIcons(
        viewHolder: RecyclerView.ViewHolder,
        deleteIcon: Drawable,
        searchIcon: Drawable,
        width: Int
    ) {
        val textMargin = 20.dp
        deleteIcon.bounds = Rect(
            textMargin,
            viewHolder.itemView.top + textMargin + 8.dp,
            textMargin + deleteIcon.intrinsicWidth,
            viewHolder.itemView.top + deleteIcon.intrinsicHeight
                    + textMargin + 8.dp
        )

        searchIcon.bounds = Rect(
            width - textMargin - searchIcon.intrinsicWidth,
            viewHolder.itemView.top + textMargin + 8.dp,
            width - textMargin,
            viewHolder.itemView.top + searchIcon.intrinsicHeight
                    + textMargin + 8.dp
        )

        updateIconColor(deleteIcon, searchIcon)
    }

    private fun updateIconColor(deleteIcon: Drawable, searchIcon: Drawable) {
        getMaterialColor(com.google.android.material.R.attr.colorOnPrimary, Color.WHITE).also {
            deleteIcon.colorFilter = BlendModeColorFilter(it, BlendMode.SRC_ATOP)
            searchIcon.colorFilter = BlendModeColorFilter(it, BlendMode.SRC_ATOP)
        }
    }

    private fun drawIconByDirection(
        c: Canvas,
        dX: Float,
        deleteIcon: Drawable,
        searchIcon: Drawable
    ) {
        if (dX > 0) deleteIcon.draw(c) else searchIcon.draw(c)
    }

    private fun getMaterialColor(
        @AttrRes colorAttributeResId: Int,
        @ColorInt defaultValue: Int
    ): Int = MaterialColors.getColor(
        requireContext(),
        colorAttributeResId,
        defaultValue
    )

    private fun adjustOpacityOnX(x: Float, width: Int): Int {
        if (x == 0f) return 0

        val opacity = (255 * abs(x) / width).roundToInt() + 95

        return if (opacity > 255) 255 else opacity
    }

    @SuppressLint("NotifyDataSetChanged")
    fun forceUpdate() {
        binding.cardListView.adapter?.notifyDataSetChanged()
    }

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), resources.displayMetrics
        ).roundToInt()
}