package com.example.spellscanapp.ui.fragment.component

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.FragmentAddToInventoryBinding
import com.example.spellscanapp.exception.ExpiredTokenException
import com.example.spellscanapp.model.Inventory
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.ui.LoginActivity
import com.example.spellscanapp.ui.adapter.InventoryListAdapter
import com.example.spellscanapp.ui.fragment.CardListFragment.Companion.ARG_INVENTORY_ID
import com.example.spellscanapp.ui.fragment.InventoryListFragment
import com.example.spellscanapp.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


private const val ARG_CARD_ID = "cardId"

class AddToInventoryFragment : Fragment() {

    private val inventoryViewModel: InventoryViewModel by viewModels()

    private var cardId: String? = null

    private var inventories: List<Inventory>? = null

    private val authService: AuthService by lazy {
        AuthService(requireContext())
    }

    private lateinit var binding: FragmentAddToInventoryBinding

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        if (exception is ExpiredTokenException) {
            Log.d(InventoryListFragment.TAG, "Handling ExpiredTokenException")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            return@CoroutineExceptionHandler startActivity(intent)
        }

        Log.d(InventoryListFragment.TAG, "Handling Unchecked Exception", exception)
        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cardId = it.getString(ARG_CARD_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddToInventoryBinding.inflate(inflater, container, false)

        lifecycleScope.launch(coroutineExceptionHandler) {
            authService.applyAccessToken { accessToken ->
                launch {
                    inventories = inventoryViewModel.loadInventories(accessToken)
                }
            }
        }

        binding.saveToInventoryButton.setOnClickListener {
            showPopupWindow(inventories!!, it)
        }

        return binding.root
    }

    private fun showPopupWindow(inventories: List<Inventory>, view: View) {
        //Create a View object yourself through inflater
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.add_to_inventory_popup_window, null)

        //Specify the length and width through constants
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        //Create a window with our parameters
        val popupWindow = PopupWindow(popupView, width, height, true)

        popupWindow.isOutsideTouchable = true
        popupWindow.animationStyle = android.R.style.Animation_Dialog
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0)

        val recyclerView = popupView.findViewById<RecyclerView>(R.id.inventories_select_list)

        Log.d(TAG, "inventories: $inventories")

        val adapter = InventoryListAdapter(inventories) {
            addCardToInventory(cardId!!, it.id, popupWindow)
        }
        recyclerView.layoutManager = GridLayoutManager(popupView.context, 1)
        recyclerView.adapter = adapter
    }

    private fun addCardToInventory(cardId: String, inventoryId: String, popupWindow: PopupWindow) {
        Log.d(TAG, "adding card to inventory: cardId=$cardId, inventoryId=$inventoryId")
        lifecycleScope.launch {
            authService.applyAccessToken { accessToken ->
                lifecycleScope.launch {
                    Log.d(TAG, "adding card to inventory: cardId=$cardId, inventoryId=$inventoryId")
                    inventoryViewModel.addCardToInventory(accessToken, inventoryId, cardId)
                    popupWindow.dismiss()
                    val navController = findNavController()
                    navController.navigate(R.id.cardListFragment, Bundle().apply {
                        putString(ARG_INVENTORY_ID, inventoryId)
                    })
                }
            }
        }
    }

    companion object {

        private const val TAG = "AddToInventoryFragment"

        @JvmStatic
        fun newInstance(cardId: String?): AddToInventoryFragment =
            AddToInventoryFragment().apply {
                arguments = Bundle().apply {
                    putString("cardId", cardId)
                }
            }
    }
}