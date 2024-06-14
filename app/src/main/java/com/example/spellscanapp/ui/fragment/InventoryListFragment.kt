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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spellscanapp.databinding.FragmentInventoryListBinding
import com.example.spellscanapp.exception.ExpiredTokenException
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.ui.CardListActivity
import com.example.spellscanapp.ui.LoginActivity
import com.example.spellscanapp.ui.adapter.InventoryListAdapter
import com.example.spellscanapp.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class InventoryListFragment : Fragment() {

    private val inventoryViewModel: InventoryViewModel by activityViewModels()

    private val authService: AuthService by lazy {
        AuthService(requireContext())
    }

    private lateinit var binding: FragmentInventoryListBinding

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        if (exception is ExpiredTokenException) {
            Log.d(TAG, "Handling ExpiredTokenException")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            return@CoroutineExceptionHandler startActivity(intent)
        }

        Log.d(TAG, "Handling Unchecked Exception", exception)
        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInventoryListBinding.inflate(inflater, container, false)

        binding.inventoryGridView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch(coroutineExceptionHandler) {
            authService.applyAccessToken {
                launch {
                    val dataset = inventoryViewModel.loadInventories(it)
                    binding.inventoryGridView.adapter = InventoryListAdapter(dataset) {
                        val intent = Intent(requireContext(), CardListActivity::class.java)
                        intent.putExtra(CardListActivity.INVENTORY_ID_KEY, it.id)
                        startActivity(intent)
                    }
                }
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "InventoryListFragment"
    }
}