package com.example.spellscanapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.FragmentCardListBinding
import com.example.spellscanapp.exception.ExpiredTokenException
import com.example.spellscanapp.model.Inventory
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.ui.LoginActivity
import com.example.spellscanapp.ui.adapter.CardListAdapter
import com.example.spellscanapp.ui.fragment.CardDetailFragment.Companion.ARG_CARD_ID
import com.example.spellscanapp.ui.fragment.CardDetailFragment.Companion.ARG_HAS_CARD_FACES
import com.example.spellscanapp.ui.fragment.component.SwipableListFragment
import com.example.spellscanapp.ui.viewmodel.CardServiceViewModel
import com.example.spellscanapp.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class CardListFragment : Fragment() {

    private lateinit var binding: FragmentCardListBinding

    private val authService: AuthService by lazy {
        AuthService(requireContext())
    }

    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()
    private val inventoryViewModel: InventoryViewModel by activityViewModels()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        if (exception is ExpiredTokenException) {
            Log.d(TAG, "Handling ExpiredTokenException")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            return@CoroutineExceptionHandler startActivity(intent)
        }

        Log.d(TAG, "Handling Unchecked Exception", exception)
        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private var inventoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            inventoryId = it.getString(ARG_INVENTORY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardListBinding.inflate(inflater, container, false)

        lifecycleScope.launch(coroutineExceptionHandler) {
            if (inventoryId == null) {
                Log.d(TAG, "inventoryId is null")
                return@launch
            }

            authService.applyAccessToken {
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

        val adapter = CardListAdapter(dataset) {
            val navController = findNavController()
            navController.navigate(R.id.cardDetailFragment, Bundle().apply {
                putString(ARG_CARD_ID, it.id)
                putBoolean(ARG_HAS_CARD_FACES, it.cardFaces.isNotEmpty())
            })
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.local_list_fragment_container, SwipableListFragment(adapter, {}, {}))
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        if (!authService.isAuthorized()) {
            navController.navigate(R.id.signInFragment)
        }

        binding.cardInventoryToolbar.inflateMenu(R.menu.card_list_toolbar_menu)

        binding.cardInventoryToolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.options_menu_item) {
                showPopup(binding.root.findViewById(R.id.options_menu_item))
                return@setOnMenuItemClickListener true
            }

            return@setOnMenuItemClickListener false
        }
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
        const val TAG = "CardListFragment"
        const val ARG_INVENTORY_ID = "inventoryId"
    }
}