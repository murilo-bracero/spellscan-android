package com.example.spellscan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spellscan.R
import com.example.spellscan.databinding.FragmentCardInventoryBinding
import com.example.spellscan.db.entity.CardEntity
import com.example.spellscan.service.CardService
import com.example.spellscan.ui.adapter.InventoryListAdapter
import com.example.spellscan.ui.fragment.component.SwipableListFragment
import com.example.spellscan.ui.viewmodel.CardInventoryViewModel
import com.example.spellscan.ui.viewmodel.CardServiceViewModel
import kotlinx.coroutines.launch

class CardInventoryFragment : Fragment() {

    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()
    private val cardInventoryViewModel: CardInventoryViewModel by activityViewModels()

    private lateinit var cardService: CardService
    private lateinit var binding: FragmentCardInventoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardService = CardService.newInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardInventoryBinding.inflate(layoutInflater, container, false)

        lifecycleScope.launch {
            val dataset = cardServiceViewModel.findAll()

            cardInventoryViewModel.setCardDataset(dataset)

            val adapter = InventoryListAdapter(cardInventoryViewModel.cardDataset, viewLifecycleOwner)

            if (savedInstanceState == null) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.local_list_fragment_container, SwipableListFragment(adapter, {
                        lifecycleScope.launch {
                            deleteCard(it, dataset[it])
                        }
                    }, {
                        lifecycleScope.launch {
                            deleteCard(it, dataset[it])
                        }
                    }))
                    .commit()
            }
        }

        binding.listMenu.setOnClickListener {
            showPopup(it)
        }

        return binding.root
    }

    private suspend fun deleteCard(index: Int, cardEntity: CardEntity) {
        cardInventoryViewModel.removeByIndex(index)
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

                R.id.find_all_action -> {
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
}