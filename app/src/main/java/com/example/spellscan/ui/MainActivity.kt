package com.example.spellscan.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.spellscan.R
import com.example.spellscan.databinding.ActivityMainBinding
import com.example.spellscan.provider.PermissionsProvider
import com.example.spellscan.ui.fragment.CardAnalysisFragment
import com.example.spellscan.ui.fragment.CardCheckListFragment
import com.example.spellscan.ui.fragment.InventoryFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var permissionsProvider: PermissionsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionsProvider = PermissionsProvider(this)

        if (permissionsProvider.allPermissionsGranted()) {
            renderCardAnalysisFragment(savedInstanceState)
        } else {
            permissionsProvider.requestAppPermissions()
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.scan_item -> {
                    if (binding.bottomNavigationView.selectedItemId != R.id.scan_item) {
                        renderCardAnalysisFragment(savedInstanceState)
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.check_list_item -> {
                    if (binding.bottomNavigationView.selectedItemId != R.id.check_list_item) {
                        renderCardCheckListFragment(savedInstanceState)
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.list_item -> {
                    if (binding.bottomNavigationView.selectedItemId != R.id.list_item) {
                        renderInventoryFragment(savedInstanceState)
                    }
                    return@setOnItemSelectedListener true
                }

                else -> false
            }
        }
    }

    private fun renderCardAnalysisFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<CardAnalysisFragment>(R.id.screen_fragment_container_view)
            }
        }
    }

    private fun renderCardCheckListFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<CardCheckListFragment>(R.id.screen_fragment_container_view)
            }
        }
    }

    private fun renderInventoryFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<InventoryFragment>(R.id.screen_fragment_container_view)
            }
        }
    }


}
