package com.example.spellscanapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscanapp.R
import com.example.spellscanapp.model.Inventory
import com.example.spellscanapp.ui.CardListActivity
import com.google.android.material.card.MaterialCardView

class InventoryListAdapter(
    private val dataset: List<Inventory>,
) : RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {

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

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inventory = dataset[position]

        holder.clickableCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, CardListActivity::class.java)
            intent.putExtra(CardListActivity.INVENTORY_ID_KEY, inventory.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.inventoryName.text = inventory.name
        holder.totalCards.text = holder.totalCards.text.toString().format(inventory.cardIdsCount)
    }

    companion object {
        private const val TAG = "InventoryListAdapter"
    }
}