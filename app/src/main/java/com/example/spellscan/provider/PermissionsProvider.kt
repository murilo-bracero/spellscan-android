package com.example.spellscan.provider

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.spellscan.logger.TAG

class PermissionsProvider(private val activity: AppCompatActivity) {
    private val requiredPermissions =
        mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    private val activityResultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            var permissionGranted = true
            perms.entries.forEach {
                if (it.key in requiredPermissions && !it.value) {
                    permissionGranted = false
                }
            }

            if (!permissionGranted) {
                Log.e(TAG, "Permissions not granted")

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