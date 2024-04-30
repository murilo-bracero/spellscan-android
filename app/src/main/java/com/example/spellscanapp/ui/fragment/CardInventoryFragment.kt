package com.example.spellscanapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.FragmentCardInventoryBinding
import com.example.spellscanapp.db.entity.CardEntity
import com.example.spellscanapp.repository.AuthStateRepository
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.service.CardService
import com.example.spellscanapp.ui.LoginAdapterActivity
import com.example.spellscanapp.ui.adapter.CardListAdapter
import com.example.spellscanapp.ui.fragment.component.SwipableListFragment
import com.example.spellscanapp.ui.viewmodel.CardServiceViewModel
import com.example.spellscanapp.ui.viewmodel.InventoryViewModel
import com.spellscan.inventoryservice.InventoryResponse
import kotlinx.coroutines.launch

class CardInventoryFragment : Fragment() {

    private val authService: AuthService by lazy {
        val repo = AuthStateRepository()
        AuthService(repo)
    }

    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()
    private val inventoryViewModel: InventoryViewModel by activityViewModels()

    private lateinit var cardService: CardService
    private lateinit var binding: FragmentCardInventoryBinding

    private var inventoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardService = CardService.newInstance()

        arguments?.let {
            inventoryId = it.getString(ARG_INVENTORY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardInventoryBinding.inflate(layoutInflater, container, false)

        binding.listMenu.setOnClickListener {
            showPopup(it)
        }

        lifecycleScope.launch {
            if (inventoryId == null) {
                Log.d("CardInventoryFragment", "inventoryId is null")
                return@launch
            }

            authService.applyAccessToken(requireContext(), {
                launch {
                    val inventory = inventoryViewModel.findInventoryById(it, inventoryId!!)

                    if(savedInstanceState == null) {
                        renderInventory(inventory)
                    }
                }
            }, {
                val intent = Intent(requireContext(), LoginAdapterActivity::class.java)
                startActivity(intent)
            })
        }

        return binding.root
    }

    private suspend fun renderInventory(inventory: InventoryResponse?) {
        if (inventory == null) {
            Log.d("CardInventoryFragment", "inventory is null: inventoryId=$inventoryId")
            return
        }

        val dataset = inventory.cardIdsList
            .map { cardServiceViewModel.findById(it)!! }

        val adapter = CardListAdapter(dataset)

        childFragmentManager.beginTransaction()
            .replace(R.id.local_list_fragment_container, SwipableListFragment(adapter, {
                lifecycleScope.launch {
                    deleteCard(dataset[it])
                }
            }, {
                lifecycleScope.launch {
                    deleteCard(dataset[it])
                }
            }))
            .commit()
    }

    private suspend fun deleteCard(cardEntity: CardEntity) {
        cardServiceViewModel.delete(cardEntity)
        forceUpdate()
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(requireContext(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.card_list_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.clear_list_action -> {
                    return@setOnMenuItemClickListener true
                }

                R.id.logout_action -> {
                    authService.logout(requireContext())
                    return@setOnMenuItemClickListener true
                }

                else -> false
            }
        }
        popup.setForceShowIcon(true)
        popup.show()
    }

    private fun forceUpdate() {
        binding.localListFragmentContainer.getFragment<SwipableListFragment>().forceUpdate()
    }

    companion object {

        private const val ARG_INVENTORY_ID = "inventoryId"

        @JvmStatic
        fun newInstance(inventoryId: String?) =
            CardInventoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_INVENTORY_ID, inventoryId)
                }
            }
    }
}