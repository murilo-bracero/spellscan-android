package com.example.spellscanapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.ActivityCardListBinding
import com.example.spellscanapp.ui.fragment.CardInventoryFragment

class CardListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardListBinding.inflate(layoutInflater)

        val inventoryId = intent.getStringExtra(INVENTORY_ID_KEY)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<CardInventoryFragment>(binding.cardInventoryFragmentContainerView.id)
            }
        }

        setContentView(binding.root)
    }

    companion object {
        const val INVENTORY_ID_KEY = "inventory_id"
    }
}