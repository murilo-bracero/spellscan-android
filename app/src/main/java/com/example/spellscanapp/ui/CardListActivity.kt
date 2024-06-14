package com.example.spellscanapp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.commit
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.ActivityCardListBinding
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.ui.fragment.CardInventoryFragment

class CardListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardListBinding

    private val authService: AuthService by lazy {
        AuthService(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardListBinding.inflate(layoutInflater)

        val inventoryId = intent.getStringExtra(INVENTORY_ID_KEY)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(binding.cardInventoryFragmentContainerView.id, CardInventoryFragment.newInstance(inventoryId))
            }
        }

        setContentView(binding.root)
        setSupportActionBar(binding.cardInventoryToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.card_list_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.options_menu_item -> {
                showPopup(findViewById(R.id.options_menu_item))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.card_list_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.clear_list_action -> {
                    return@setOnMenuItemClickListener true
                }

                R.id.logout_action -> {
                    authService.logout()
                    return@setOnMenuItemClickListener true
                }

                else -> false
            }
        }
        popup.setForceShowIcon(true)
        popup.show()
    }

    companion object {
        const val INVENTORY_ID_KEY = "inventory_id"
    }
}