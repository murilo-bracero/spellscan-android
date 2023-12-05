package com.example.spellscan.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscan.R
import com.example.spellscan.model.Card
import com.example.spellscan.model.CardRow

class CardListAdapter(private val dataset: ArrayList<CardRow>) :
    RecyclerView.Adapter<CardListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox
        val cardName: TextView
        val cardType: TextView
        val cardSet: TextView

        init {
            // Define click listener for the ViewHolder's View.
            checkbox = view.findViewById(R.id.cardRowCheckbox)
            cardName = view.findViewById(R.id.cardNameRowText)
            cardType = view.findViewById(R.id.cardTypeRowText)
            cardSet = view.findViewById(R.id.cardSetRowText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_row_item, parent, false)

        val holder = ViewHolder(view)

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            dataset[holder.adapterPosition].isChecked = isChecked
        }

        return holder
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardName.text = dataset[position].name
        holder.cardType.text = dataset[position].type
        holder.cardSet.text = dataset[position].set
    }

    fun getCheckedCards(): List<CardRow> {
        return dataset.filter { it.isChecked }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeCheckedCards() {
        dataset.removeIf { it.isChecked }
        notifyDataSetChanged()
    }
}