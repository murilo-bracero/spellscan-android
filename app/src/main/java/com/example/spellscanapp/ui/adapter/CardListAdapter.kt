package com.example.spellscanapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.CachePolicy.ENABLED
import coil.request.ImageRequest
import com.example.spellscanapp.R
import com.example.spellscanapp.model.Card
import com.google.android.material.card.MaterialCardView

@SuppressLint("NotifyDataSetChanged")
class CardListAdapter(
    private val dataset: List<Card>,
    private val onCardClick: (Card) -> Unit
) : RecyclerView.Adapter<CardListAdapter.ViewHolder>() {

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
            .inflate(R.layout.card_row_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = dataset[position]

        holder.clickableCard.setOnClickListener {
            onCardClick(card)
        }

        holder.cardName.text = card.name
        holder.cardLang.text = card.lang
        holder.cardSet.text = card.set

        val cardImageUrl = if (card.cardFaces.isNotEmpty())
            card.cardFaces.first().cardImage
        else
            card.imageUrl

        val imageLoader = ImageLoader.Builder(holder.cardImage.context)
            .memoryCachePolicy(ENABLED)
            .diskCachePolicy(ENABLED)
            .build()

        val request = ImageRequest.Builder(holder.cardImage.context)
            .data(cardImageUrl)
            .target(holder.cardImage)
            .build()
        imageLoader.enqueue(request)
    }
}