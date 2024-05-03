package com.example.spellscanapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.iterator
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.FragmentInventoryListBinding
import com.example.spellscanapp.exception.ExpiredTokenException
import com.example.spellscanapp.model.dto.FilterDirection
import com.example.spellscanapp.model.dto.InventoryListFilter
import com.example.spellscanapp.repository.AuthStateRepository
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.ui.LoginAdapterActivity
import com.example.spellscanapp.ui.adapter.InventoryListAdapter
import com.example.spellscanapp.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException

class InventoryListFragment : Fragment() {

    private val inventoryViewModel: InventoryViewModel by activityViewModels()

    private val authService = AuthService(AuthStateRepository())

    private val inventoryListFilter: InventoryListFilter = InventoryListFilter()

    private lateinit var binding: FragmentInventoryListBinding

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        if(exception is ExpiredTokenException) {
            Log.d(TAG, "Handling ExpiredTokenException")
            val intent = Intent(requireContext(), LoginAdapterActivity::class.java)
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

        binding.inventoryGridView.layoutManager = GridLayoutManager(context, 2)
        binding.inventoryGridView.adapter = InventoryListAdapter(inventoryViewModel.inventoryDataset, viewLifecycleOwner)

        lifecycleScope.launch(coroutineExceptionHandler) {
            authService.applyAccessToken(requireContext()) {
                launch {
                    inventoryViewModel.loadInventories(it)
                }
            }
        }

        inventoryListFilter.nameDirection.observe(viewLifecycleOwner) {
            inventoryViewModel.sortByName(it)
        }

        return binding.root
    }

    companion object {
        const val TAG = "InventoryListFragment"
    }
}