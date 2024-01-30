package com.example.spellscan.ui

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.spellscan.R
import com.example.spellscan.databinding.ActivityMainBinding
import com.example.spellscan.provider.PermissionsProvider
import com.example.spellscan.ui.fragment.CardAnalysisFragment
import com.example.spellscan.ui.fragment.CardInventoryFragment

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
                replace<CardInventoryFragment>(R.id.screen_fragment_container_view)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        // Hide keyboard and clear focus when touched outside EditText
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
