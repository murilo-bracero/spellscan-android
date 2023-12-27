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
import com.example.spellscan.databinding.FragmentCardListBinding
import com.example.spellscan.logger.TAG
import com.example.spellscan.model.newCard
import com.example.spellscan.service.CardService
import com.example.spellscan.ui.viewmodel.CardDatasetViewModel
import kotlinx.coroutines.launch

class CardListFragment : Fragment() {

    private val cardDatasetViewModel: CardDatasetViewModel by activityViewModels()

    private lateinit var cardService: CardService
    private lateinit var binding: FragmentCardListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardService = CardService.newInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardListBinding.inflate(layoutInflater, container, false)

        binding.removeSelectedButton.visibility = View.GONE

        cardDatasetViewModel.fetchAll()

        binding.listMenu.setOnClickListener {
            showPopup(it)
        }

        binding.removeSelectedButton.setOnClickListener {
            clearSelected()
        }

        cardDatasetViewModel.checkedLiveData.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.removeSelectedButton.visibility = View.VISIBLE
                val buttonText = getString(R.string.remove_all_number_selected)
                binding.removeSelectedButton.text = buttonText.replace("\$number", "${cardDatasetViewModel.getCheckedCards().size}")
            } else {
                binding.removeSelectedButton.visibility = View.GONE
            }
        }
        return binding.root
    }

    private fun showPopup(v: View){
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

    private fun clearSelected() {
        cardDatasetViewModel.clearSelected()
        binding.swipableListFragmentContainer.getFragment<SwipableListFragment>()
            .forceUpdate()
    }
}