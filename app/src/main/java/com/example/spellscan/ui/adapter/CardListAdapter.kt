package com.example.spellscan.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscan.R
import com.example.spellscan.model.CardRow

@SuppressLint("NotifyDataSetChanged")
class CardListAdapter(private val liveDataset: LiveData<MutableList<CardRow>>, lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<CardListAdapter.ViewHolder>() {

    init {
        liveDataset.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

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
            liveDataset.value!![holder.adapterPosition].isChecked = isChecked
        }

        return holder
    }

    override fun getItemCount() = liveDataset.value!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardName.text = liveDataset.value!![position].name
        holder.cardType.text = liveDataset.value!![position].type
        holder.cardSet.text = liveDataset.value!![position].set
    }
}