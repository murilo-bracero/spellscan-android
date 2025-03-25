package com.example.spellscanapp.provider

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PermissionsProvider(private val activity: AppCompatActivity) {
    private val requiredPermissions =
        mutableListOf(
            Manifest.permission.CAMERA
        ).toTypedArray()

    private val activityResultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            var permissionGranted = true
            perms.entries.forEach {
                if (it.key in requiredPermissions && !it.value) {
                    permissionGranted = false
                }
            }

            if (!permissionGranted) {
                Toast.makeText(
                    activity, "Permissions not granted", Toast.LENGTH_SHORT
                ).show()
            }
        }

    fun requestAppPermissions() {
        activityResultLauncher.launch(requiredPermissions)
    }

    fun allPermissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(
            activity.baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}