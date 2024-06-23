package com.example.spellscanapp.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.ActivityMainBinding
import com.example.spellscanapp.provider.PermissionsProvider

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val permissionsProvider: PermissionsProvider by lazy {
        PermissionsProvider(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!permissionsProvider.allPermissionsGranted()) {
            permissionsProvider.requestAppPermissions()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupWithNavController(binding.bottomNavigationView, navController)
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
