package com.example.spellscan.ui.fragment

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscan.R
import com.example.spellscan.databinding.FragmentSwipableListBinding
import com.example.spellscan.repository.LocalCardRepository
import com.example.spellscan.ui.adapter.CardListAdapter
import com.example.spellscan.ui.viewmodel.CardDatasetViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 * Use the [SwipableListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SwipableListFragment : Fragment() {
    private val cardDatasetViewModel: CardDatasetViewModel by activityViewModels()

    private val localCardRepository = LocalCardRepository.getInstance()

    private lateinit var binding: FragmentSwipableListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSwipableListBinding.inflate(inflater, container, false)

        binding.cardListView.layoutManager = LinearLayoutManager(context)

        val cardListAdapter = CardListAdapter(cardDatasetViewModel.cardLiveData, this)
        binding.cardListView.adapter = cardListAdapter

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

        val deleteIcon = ResourcesCompat.getDrawable(resources, R.drawable.delete_icon, null)

        val swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {
                    val pos = viewHolder.adapterPosition
                    cardDatasetViewModel.removeByIndex(pos)?.let {
                        localCardRepository.deleteById(it.id)
                        cardListAdapter.notifyItemRemoved(pos)
                    }
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
                //1. Background color based upon direction swiped

                val color = when {
                    abs(dX) == 0f -> Color.WHITE
                    abs(dX) < width / 3 -> Color.GRAY
                    dX > width / 3 -> Color.RED
                    else -> Color.GREEN
                }

                val background = ColorDrawable(color)
                background.setBounds(
                    viewHolder.itemView.left,
                    viewHolder.itemView.top,
                    viewHolder.itemView.right,
                    viewHolder.itemView.bottom
                )
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

                //3. Drawing icon based upon direction swiped
                if (dX > 0) deleteIcon.draw(c) //else archiveIcon.draw(canvas)

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

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), resources.displayMetrics
        ).roundToInt()
}