package com.example.spellscan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spellscan.databinding.FragmentInventoryBinding
import com.example.spellscan.ui.adapter.InventoryListAdapter
import com.example.spellscan.ui.viewmodel.CardServiceViewModel
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {

    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()
    private lateinit var binding: FragmentInventoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInventoryBinding.inflate(layoutInflater, container, false)

        lifecycleScope.launch {
            val dataset = cardServiceViewModel.findAll()

            binding.inventoryRecyclerView.layoutManager = LinearLayoutManager(context)

            val inventoryListAdapter = InventoryListAdapter(dataset)
            binding.inventoryRecyclerView.adapter = inventoryListAdapter
        }

        return binding.root
    }
}