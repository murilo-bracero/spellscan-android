package com.example.spellscanapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscanapp.R
import com.example.spellscanapp.ui.CardDetailActivity
import com.example.spellscanapp.ui.CardListActivity
import com.google.android.material.card.MaterialCardView
import com.spellscan.inventoryservice.InventoryResponse

@SuppressLint("NotifyDataSetChanged")
class InventoryListAdapter(
    private val liveDataSet: LiveData<List<InventoryResponse>>,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {

    init {
        liveDataSet.observe(lifecycleOwner) {
            Log.d(TAG, "updated live data set with value: inventory=$it")
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clickableCard: MaterialCardView

        val inventoryName: TextView
        val totalCards: TextView

        init {
            clickableCard = view.findViewById(R.id.inventory_clickable_card)

            inventoryName = view.findViewById(R.id.inventory_name)
            totalCards = view.findViewById(R.id.total_cards_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_grid_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = liveDataSet.value?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataset = liveDataSet.value ?: emptyList()
        val inventory = dataset[position]

        holder.clickableCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, CardListActivity::class.java)
            intent.putExtra(CardListActivity.INVENTORY_ID_KEY, inventory.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.inventoryName.text = inventory.name
        holder.totalCards.text = holder.totalCards.text.toString()
            .replace("{totalCards}", inventory.cardIdsCount.toString())
    }

    companion object {
        private const val TAG = "InventoryListAdapter"
    }
}