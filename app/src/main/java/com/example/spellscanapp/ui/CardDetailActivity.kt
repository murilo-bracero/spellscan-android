package com.example.spellscanapp.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.Menu.NONE
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.ActivityCardDetailBinding
import com.example.spellscanapp.repository.AuthStateRepository
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.ui.fragment.DoubleFaceCardDetailFragment
import com.example.spellscanapp.ui.fragment.SingleFaceCardDetailFragment
import com.example.spellscanapp.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class CardDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardDetailBinding.inflate(layoutInflater)

        val cardId = intent.getStringExtra(CARD_ID_INTENT_KEY)
        val hasCardFaces = intent.getBooleanExtra(HAS_CARD_FACES_INTENT_KEY, false)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)

                val fragment: Fragment = if (hasCardFaces)
                    DoubleFaceCardDetailFragment.newInstance(cardId)
                else
                    SingleFaceCardDetailFragment.newInstance(cardId)

                replace(
                    binding.cardDetailFragmentContainer.id,
                    fragment
                )
            }
        }
        setContentView(binding.root)
    }

    companion object {
        const val CARD_ID_INTENT_KEY = "card_id"
        const val HAS_CARD_FACES_INTENT_KEY = "has_card_faces"
    }
}