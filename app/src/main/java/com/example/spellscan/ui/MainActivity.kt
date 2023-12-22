package com.example.spellscan.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.spellscan.R
import com.example.spellscan.databinding.ActivityMainBinding
import com.example.spellscan.provider.PermissionsProvider
import com.example.spellscan.ui.fragment.CameraFragment

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    private lateinit var permissionsProvider: PermissionsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        permissionsProvider = PermissionsProvider(this)

        if (permissionsProvider.allPermissionsGranted()) {
            renderCameraFragment(savedInstanceState)
        } else {
            permissionsProvider.requestAppPermissions()
        }
    }

    private fun renderCameraFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CameraFragment>(R.id.cameraFragmentContainerView)
            }
        }
    }
}
