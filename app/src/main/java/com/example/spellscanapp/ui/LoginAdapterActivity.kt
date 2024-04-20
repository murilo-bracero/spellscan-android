package com.example.spellscanapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spellscanapp.provider.AuthorizationProvider

class LoginAdapterActivity: AppCompatActivity() {

    private val authorizationProvider: AuthorizationProvider by lazy {
        AuthorizationProvider(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authorizationIntent = authorizationProvider.createAuthorizationIntent()
        authorizationProvider.createAuthorizationResultLauncher({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }, {
            Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show()
        }).launch(authorizationIntent)
    }
}