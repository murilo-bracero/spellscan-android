package com.example.spellscanapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.FragmentInventoryListBinding
import com.example.spellscanapp.ui.LoginAdapterActivity
import com.example.spellscanapp.ui.adapter.InventoryListAdapter
import com.example.spellscanapp.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException

class InventoryListFragment : Fragment() {

    private val inventoryViewModel: InventoryViewModel by activityViewModels()

    private lateinit var binding: FragmentInventoryListBinding

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.i("porra", "cacete vagina idosa: $exception")
        val intent = Intent(requireContext(), LoginAdapterActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInventoryListBinding.inflate(inflater, container, false)

        binding.inventoryGridView.layoutManager = GridLayoutManager(context, 3)
        binding.inventoryGridView.adapter = InventoryListAdapter(inventoryViewModel.inventoryDataset, viewLifecycleOwner)

        lifecycleScope.launch(coroutineExceptionHandler) {
            launch {
                inventoryViewModel.loadInventories()
            }
        }

        return binding.root
    }
}