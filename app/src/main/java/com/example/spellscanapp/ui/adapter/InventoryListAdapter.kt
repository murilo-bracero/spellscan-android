package com.example.spellscanapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.spellscanapp.R
import com.example.spellscanapp.db.entity.CardEntity
import com.example.spellscanapp.ui.CardDetailActivity
import com.example.spellscanapp.ui.CardDetailActivity.Companion.CARD_ID_INTENT_KEY
import com.example.spellscanapp.ui.CardDetailActivity.Companion.HAS_CARD_FACES_INTENT_KEY
import com.google.android.material.card.MaterialCardView

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
        val clickableCard: MaterialCardView

        val cardName: TextView
        val cardSet: TextView
        val cardLang: TextView

        val cardImage: ImageView

        init {
            clickableCard = view.findViewById(R.id.clickable_card)

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
        val card = dataset[position]

        holder.clickableCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, CardDetailActivity::class.java)
            intent.putExtra(CARD_ID_INTENT_KEY, card.id)
            intent.putExtra(HAS_CARD_FACES_INTENT_KEY, card.hasCardFaces)
            holder.itemView.context.startActivity(intent)
        }

        holder.cardName.text = card.name
        holder.cardLang.text = card.lang
        holder.cardSet.text = card.set

        val cardImageUrl = if (card.cardFaces.isNotEmpty())
            card.cardFaces.first().cardImage
        else
            card.imageUrl

        val imageLoader = holder.cardImage.context.imageLoader
        val request = ImageRequest.Builder(holder.cardImage.context)
            .data(cardImageUrl)
            .target(holder.cardImage)
            .build()
        imageLoader.enqueue(request)
    }
}