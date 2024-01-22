package com.example.spellscan.ui.fragment.component

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscan.R
import com.example.spellscan.databinding.FragmentLocalCardListBinding
import com.example.spellscan.logger.TAG
import com.example.spellscan.ui.adapter.CardCheckListAdapter
import com.example.spellscan.ui.viewmodel.CardDatasetViewModel
import com.example.spellscan.ui.viewmodel.CardServiceViewModel
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

class LocalCardListFragment : Fragment() {
    private val cardDatasetViewModel: CardDatasetViewModel by activityViewModels()
    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()

    private lateinit var binding: FragmentLocalCardListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocalCardListBinding.inflate(inflater, container, false)

        binding.cardListView.layoutManager = LinearLayoutManager(context)

        val cardCheckListAdapter = CardCheckListAdapter(cardDatasetViewModel, this)
        binding.cardListView.adapter = cardCheckListAdapter

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

        val swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.RIGHT) {
                    cardDatasetViewModel.removeByIndex(pos)
                    forceUpdate()
                }

                if (direction == ItemTouchHelper.LEFT) {
                    cardDatasetViewModel.findByIndex(pos)
                        ?.let { card ->
                            lifecycleScope.launch {
                                val res = cardServiceViewModel.search(card)
                                Log.i(TAG, "card response: $res")
                                cardServiceViewModel.save(res)
                                cardDatasetViewModel.removeByIndex(pos)
                                forceUpdate()
                            }
                        }
                }
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                //1. Background color based upon direction swiped

                val color = when {
                    dX > 0 -> getMaterialColor(R.attr.delete_background_color, Color.RED)
                    dX < 0 -> getMaterialColor(R.attr.search_background_color, Color.BLUE)
                    else -> Color.TRANSPARENT
                }

                val background = ColorDrawable(color)
                background.setBounds(
                    viewHolder.itemView.left,
                    viewHolder.itemView.top,
                    viewHolder.itemView.right,
                    viewHolder.itemView.bottom
                )
                background.alpha = adjustOpacityOnX(dX, width)
                background.draw(c)

                //2. Printing the icons

                val textMargin = 20.dp
                deleteIcon!!.bounds = Rect(
                    textMargin,
                    viewHolder.itemView.top + textMargin + 8.dp,
                    textMargin + deleteIcon.intrinsicWidth,
                    viewHolder.itemView.top + deleteIcon.intrinsicHeight
                            + textMargin + 8.dp
                )

                searchIcon!!.bounds = Rect(
                    width - textMargin - searchIcon.intrinsicWidth,
                    viewHolder.itemView.top + textMargin + 8.dp,
                    width - textMargin,
                    viewHolder.itemView.top + searchIcon.intrinsicHeight
                            + textMargin + 8.dp
                )

                // change delete icon color
                val iconColor =
                    getMaterialColor(com.google.android.material.R.attr.colorOnPrimary, Color.WHITE)

                deleteIcon.colorFilter = BlendModeColorFilter(iconColor, BlendMode.SRC_ATOP)
                searchIcon.colorFilter = BlendModeColorFilter(iconColor, BlendMode.SRC_ATOP)

                //3. Drawing icon based upon direction swiped
                if (dX > 0) deleteIcon.draw(c) else searchIcon.draw(c)

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

        swipeHelper.attachToRecyclerView(binding.cardListView)

        return binding.root
    }

    private fun getMaterialColor(
        @AttrRes colorAttributeResId: Int,
        @ColorInt defaultValue: Int
    ): Int = MaterialColors.getColor(
        requireContext(),
        colorAttributeResId,
        defaultValue
    )

    fun adjustOpacityOnX(x: Float, width: Int): Int {
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