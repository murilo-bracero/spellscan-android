package com.example.spellscanapp.ui.fragment.component

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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscanapp.R
import com.example.spellscanapp.builder.SimpleCallbackBuilder
import com.example.spellscanapp.databinding.FragmentSwipableListBinding
import com.example.spellscanapp.ui.adapter.CardListAdapter
import com.example.spellscanapp.ui.fragment.CardDetailFragment
import com.example.spellscanapp.ui.viewmodel.SwipeableListViewModel
import com.google.android.material.color.MaterialColors
import kotlin.math.abs
import kotlin.math.roundToInt

class SwipeableListFragment : Fragment() {

    private val swipeableListViewModel: SwipeableListViewModel by activityViewModels()

    private lateinit var binding: FragmentSwipableListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSwipableListBinding.inflate(inflater, container, false)

        binding.cardListView.layoutManager = LinearLayoutManager(context)

        swipeableListViewModel.dataset.observe(viewLifecycleOwner) { dataset ->
            binding.cardListView.adapter = CardListAdapter(dataset) {
                val navController = findNavController()
                navController.navigate(R.id.cardDetailFragment, Bundle().apply {
                    putString(CardDetailFragment.ARG_CARD_ID, it.id)
                    putBoolean(CardDetailFragment.ARG_HAS_CARD_FACES, it.cardFaces.isNotEmpty())
                })
            }
        }

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

        val deleteIcon = ResourcesCompat.getDrawable(
            resources, R.drawable.delete_icon,
            context?.theme
        )

        val callback = SimpleCallbackBuilder(0, ItemTouchHelper.RIGHT)
            .onSwipedStart {
//                viewHolder -> onCardDeleted(viewHolder.adapterPosition)
            }
            .onChildDraw { c, viewHolder, dX ->
                onChildDraw(c, viewHolder, dX, deleteIcon, width)
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
        width: Int
    ) {
        drawTileBackground(c, viewHolder, dX, width)

        if (deleteIcon == null) {
            Log.w(TAG, "deleteIcon is null")
            return
        }

        drawTileIcons(viewHolder, deleteIcon)

        drawIconByDirection(c, dX, deleteIcon)
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
        deleteIcon: Drawable
    ) {
        val textMargin = 20.dp
        deleteIcon.bounds = Rect(
            textMargin,
            viewHolder.itemView.top + textMargin + 8.dp,
            textMargin + deleteIcon.intrinsicWidth,
            viewHolder.itemView.top + deleteIcon.intrinsicHeight
                    + textMargin + 8.dp
        )

        updateIconColor(deleteIcon)
    }

    private fun updateIconColor(deleteIcon: Drawable) {
        getMaterialColor(com.google.android.material.R.attr.colorOnPrimary, Color.WHITE).also {
            deleteIcon.colorFilter = BlendModeColorFilter(it, BlendMode.SRC_ATOP)
        }
    }

    private fun drawIconByDirection(
        c: Canvas,
        dX: Float,
        deleteIcon: Drawable
    ) {
        if (dX > 0) deleteIcon.draw(c)
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

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), resources.displayMetrics
        ).roundToInt()

    companion object {
        private const val TAG = "SwipeableListFragment"
    }
}
