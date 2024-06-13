package com.example.spellscanapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.FragmentCardInventoryBinding
import com.example.spellscanapp.exception.ExpiredTokenException
import com.example.spellscanapp.model.Card
import com.example.spellscanapp.model.Inventory
import com.example.spellscanapp.repository.AuthStateRepository
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.service.CardService
import com.example.spellscanapp.ui.LoginAdapterActivity
import com.example.spellscanapp.ui.adapter.CardListAdapter
import com.example.spellscanapp.ui.fragment.component.SwipableListFragment
import com.example.spellscanapp.ui.viewmodel.CardServiceViewModel
import com.example.spellscanapp.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        if(exception is ExpiredTokenException) {
            Log.d(TAG, "Handling ExpiredTokenException")
            val intent = Intent(requireContext(), LoginAdapterActivity::class.java)
            return@CoroutineExceptionHandler startActivity(intent)
        }

        Log.d(TAG, "Handling Unchecked Exception", exception)
        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
    }

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

        lifecycleScope.launch(coroutineExceptionHandler) {
            if (inventoryId == null) {
                Log.d(TAG, "inventoryId is null")
                return@launch
            }

            authService.applyAccessToken(requireContext()) {
                launch {
                    val inventory = inventoryViewModel.findInventoryById(it, inventoryId!!)

                    if (savedInstanceState == null) {
                        renderInventory(inventory)
                    }
                }
            }
        }

        return binding.root
    }

    private suspend fun renderInventory(inventory: Inventory?) {
        if (inventory == null) {
            Log.d(TAG, "inventory is null: inventoryId=$inventoryId")
            return
        }

        val dataset = inventory.cardIds
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

    private suspend fun deleteCard(card: Card) {
        cardServiceViewModel.delete(card)
        forceUpdate()
    }

    private fun forceUpdate() {
        binding.localListFragmentContainer.getFragment<SwipableListFragment>().forceUpdate()
    }

    companion object {

        private const val TAG = "CardInventoryFragment"
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