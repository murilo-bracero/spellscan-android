package com.example.spellscan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spellscan.R
import com.example.spellscan.databinding.ActivityCardListBinding
import com.example.spellscan.databinding.ActivityMainBinding
import com.example.spellscan.logger.TAG
import com.example.spellscan.model.toCardRow
import com.example.spellscan.repository.LocalCardRepository
import com.example.spellscan.ui.adapter.CardListAdapter

class CardListActivity : AppCompatActivity() {
    private val localCardRepository = LocalCardRepository.getInstance()

    private lateinit var binding: ActivityCardListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardListView.layoutManager = LinearLayoutManager(this)
        binding.cardListView.adapter = CardListAdapter(
            ArrayList(localCardRepository
                .getAll()
                .map { it.toCardRow() })
        )

        binding.deleteRowButton.setOnClickListener {
            (binding.cardListView.adapter as CardListAdapter).getCheckedCards()
                .map {
                    localCardRepository.removeByProps(it.name, it.type, it.set)
                    return@map it
                }

            (binding.cardListView.adapter as CardListAdapter).removeCheckedCards()
        }
    }
}