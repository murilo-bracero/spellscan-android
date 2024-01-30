package com.example.spellscan.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import com.example.spellscan.R
import com.example.spellscan.db.entity.CardEntity

@SuppressLint("NotifyDataSetChanged")
class InventoryListAdapter(
    private val liveDataSet: LiveData<List<CardEntity>>,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {

    init {
        liveDataSet.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardName: TextView
        val cardSet: TextView
        val cardLang: TextView

        val cardImage: ImageView

        init {
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

    override fun getItemCount(): Int = liveDataSet.value?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataset = liveDataSet.value ?: emptyList()

        holder.cardName.text = dataset[position].name
        holder.cardLang.text = dataset[position].lang
        holder.cardSet.text = dataset[position].set

        // Apply image here
        val imageLoader = holder.cardImage.context.imageLoader
        val request = ImageRequest.Builder(holder.cardImage.context)
            .data(dataset[position].imageUrl)
            .target(holder.cardImage)
            .build()
        imageLoader.enqueue(request)
    }
}