package com.example.spellscan.ui.fragment

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
import com.example.spellscan.R
import com.example.spellscan.databinding.FragmentCardCheckListBinding
import com.example.spellscan.logger.TAG
import com.example.spellscan.model.newCard
import com.example.spellscan.service.CardService
import com.example.spellscan.ui.fragment.component.LocalCardListFragment
import com.example.spellscan.ui.viewmodel.CardDatasetViewModel
import com.example.spellscan.ui.viewmodel.CardServiceViewModel
import kotlinx.coroutines.launch

class CardCheckListFragment : Fragment() {

    private val cardDatasetViewModel: CardDatasetViewModel by activityViewModels()
    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()

    private lateinit var cardService: CardService
    private lateinit var binding: FragmentCardCheckListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardService = CardService.newInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        binding = FragmentCardCheckListBinding.inflate(layoutInflater, container, false)

        if(savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.local_list_fragment_container, LocalCardListFragment ({
                    removeCardByIndex(it)
                }, {
                    searchCardByIndex(it)
                    removeCardByIndex(it)
                }))
                .commit()
        }

        binding.removeSelectedButton.visibility = View.GONE

        cardDatasetViewModel.fetchAll()

        binding.listMenu.setOnClickListener {
            showPopup(it)
        }

        binding.removeSelectedButton.setOnClickListener {
            removeChecked()
        }

        binding.cancelSelectionButton.setOnClickListener {
            cancelSelection()
        }

        cardDatasetViewModel.checkedLiveData.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.removeSelectedButton.visibility = View.VISIBLE
                binding.cancelSelectionButton.visibility = View.VISIBLE
                val buttonText = getString(R.string.remove_all_number_selected)
                binding.removeSelectedButton.text =
                    buttonText.replace("\$number", "${cardDatasetViewModel.getCheckedCards().size}")
            } else {
                binding.removeSelectedButton.visibility = View.GONE
                binding.cancelSelectionButton.visibility = View.GONE
            }
        }

        return binding.root
    }

    private fun searchCardByIndex(index: Int) {
        val card = cardDatasetViewModel.findByIndex(index)

        if (card == null) {
            Log.w(TAG, "card could not be found")
            return
        }

        lifecycleScope.launch {
            val found = cardServiceViewModel.search(card)
            Log.i(TAG, "Card found: card=$card, found=$found")
        }
    }

    private fun removeCardByIndex(index: Int) {
        cardDatasetViewModel.removeByIndex(index)
        forceUpdate()
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(requireContext(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.card_list_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.clear_list_action -> {
                    clearAll()
                    return@setOnMenuItemClickListener true
                }

                R.id.find_all_action -> {
                    findAll()
                    return@setOnMenuItemClickListener true
                }

                else -> false
            }
        }
        popup.setForceShowIcon(true)
        popup.show()
    }

    private fun clearAll() {
        cardDatasetViewModel.reset()
    }

    private fun findAll() {
        cardDatasetViewModel.getCheckedCards()
            .map {
                lifecycleScope.launch {
                    val card = cardService.find(newCard(it.name, it.type, it.set))
                    //TODO: implement after find flow
                    Log.i(TAG, "${card.id} - ${card.name} - ${card.type} - ${card.set}")
                }
            }
    }

    private fun removeChecked() {
        cardDatasetViewModel.removeChecked()
        forceUpdate()
    }

    private fun cancelSelection() {
        cardDatasetViewModel.clearSelected()
        forceUpdate()
    }

    private fun forceUpdate() {
        binding.localListFragmentContainer.getFragment<LocalCardListFragment>().forceUpdate()
    }
}