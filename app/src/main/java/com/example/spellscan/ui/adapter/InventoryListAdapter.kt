package com.example.spellscan.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscan.R
import com.example.spellscan.ui.viewmodel.CardDatasetViewModel
import com.example.spellscan.ui.viewmodel.CardServiceViewModel
import com.spellscan.proto.CardResponse

@SuppressLint("NotifyDataSetChanged")
class InventoryListAdapter(
    private val dataSet: List<CardResponse>
) : RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardName: TextView
        val cardSet: TextView
        val cardLang: TextView

        init {
            // Define click listener for the ViewHolder's View.
            cardName = view.findViewById(R.id.card_row_name)
            cardSet = view.findViewById(R.id.card_row_set)
            cardLang = view.findViewById(R.id.card_row_lang)
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_row_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.cardName.text = dataSet[position].name
        holder.cardLang.text = dataSet[position].lang
        holder.cardSet.text = dataSet[position].set

        // Apply image here
    }
}