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

@SuppressLint("NotifyDataSetChanged")
class CardCheckListAdapter(
    private val cardDatasetViewModel: CardDatasetViewModel,
    lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<CardCheckListAdapter.ViewHolder>() {

    init {
        cardDatasetViewModel.cardLiveData.observe(lifecycleOwner) {
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

        return ViewHolder(view)
    }

    override fun getItemCount() = cardDatasetViewModel.cardLiveData.value!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardList = cardDatasetViewModel.cardLiveData.value!!

        holder.cardName.text = cardList[position].name
        holder.cardType.text = cardList[position].type
        holder.cardSet.text = cardList[position].set

        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = cardList[position].isChecked
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            cardDatasetViewModel.setIsChecked(position, isChecked)
        }
    }
}