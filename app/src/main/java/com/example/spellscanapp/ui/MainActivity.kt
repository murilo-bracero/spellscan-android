package com.example.spellscanapp.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.ActivityMainBinding
import com.example.spellscanapp.provider.PermissionsProvider
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.ui.fragment.CardAnalysisFragment
import com.example.spellscanapp.ui.fragment.InventoryListFragment
import com.example.spellscanapp.ui.fragment.SignInFragment
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val permissionsProvider: PermissionsProvider by lazy {
        PermissionsProvider(this)
    }

    private val authService: AuthService by lazy {
        AuthService(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!permissionsProvider.allPermissionsGranted()) {
            permissionsProvider.requestAppPermissions()
        }

        renderCardAnalysisFragment(savedInstanceState)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.scan_item -> {
                    if (binding.bottomNavigationView.selectedItemId != R.id.scan_item) {
                        renderCardAnalysisFragment(savedInstanceState)
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.check_list_item -> {
                    if (binding.bottomNavigationView.selectedItemId != R.id.check_list_item && authService.isAuthorized()) {
                        renderInventoryListFragment(savedInstanceState)
                    }

                    if (!authService.isAuthorized()) {
                        renderSignInFragment(savedInstanceState)
                    }
                    return@setOnItemSelectedListener true
                }

                else -> false
            }
        }
    }

    private fun renderCardAnalysisFragment(savedInstanceState: Bundle?) {
        renderFragment<CardAnalysisFragment>(R.id.screen_fragment_container_view, savedInstanceState)
    }

    private fun renderInventoryListFragment(savedInstanceState: Bundle?) {
        renderFragment<InventoryListFragment>(R.id.screen_fragment_container_view, savedInstanceState)
    }

    private fun renderSignInFragment(savedInstanceState: Bundle?) {
        renderFragment<SignInFragment>(R.id.screen_fragment_container_view, savedInstanceState)
    }

    private inline fun <reified F : Fragment> renderFragment(id: Int, savedInstanceState: Bundle?){
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<F>(id)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        hideKeyboardWhenTappedOutside(event)
        return super.dispatchTouchEvent(event)
    }

    private fun hideKeyboardWhenTappedOutside(event: MotionEvent) {
        if (event.action == ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
    }
}
