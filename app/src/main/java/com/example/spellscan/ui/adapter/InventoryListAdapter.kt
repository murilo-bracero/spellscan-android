package com.example.spellscan.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import com.example.spellscan.R
import com.example.spellscan.db.entity.CardEntity
import com.spellscan.proto.CardResponse

@SuppressLint("NotifyDataSetChanged")
class InventoryListAdapter(
    private val dataSet: List<CardEntity>
) : RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardName: TextView
        val cardSet: TextView
        val cardLang: TextView

        val cardImage: ImageView

        init {
            // Define click listener for the ViewHolder's View.
            cardName = view.findViewById(R.id.card_row_name)
            cardSet = view.findViewById(R.id.card_row_set)
            cardLang = view.findViewById(R.id.card_row_lang)

            cardImage = view.findViewById(R.id.card_row_image)
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
        val imageLoader = holder.cardImage.context.imageLoader
        val request = ImageRequest.Builder(holder.cardImage.context)
            .data(dataSet[position].imageUrl)
            .target(holder.cardImage)
            .build()
        imageLoader.enqueue(request)
    }
}