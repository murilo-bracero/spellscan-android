package com.example.spellscan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spellscan.R
import com.example.spellscan.databinding.FragmentInventoryBinding
import com.example.spellscan.ui.adapter.CardCheckListAdapter
import com.example.spellscan.ui.adapter.InventoryListAdapter
import com.example.spellscan.ui.viewmodel.CardDatasetViewModel
import com.example.spellscan.ui.viewmodel.CardServiceViewModel

class InventoryFragment : Fragment() {

    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()
    private lateinit var binding: FragmentInventoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInventoryBinding.inflate(layoutInflater, container, false)

        val dataset = cardServiceViewModel.findAll()

        binding.inventoryRecyclerView.layoutManager = LinearLayoutManager(context)
        
        val inventoryListAdapter = InventoryListAdapter(dataset)
        binding.inventoryRecyclerView.adapter = inventoryListAdapter

        return binding.root
    }
}