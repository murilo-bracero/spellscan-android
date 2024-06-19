package com.example.spellscanapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.spellscanapp.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            onLoginButtonClickListener()
        }
    }

    private fun onLoginButtonClickListener() {
        val email = binding.emailLoginFormView.editText?.text?.toString() ?: ""
        val password = binding.passwordLoginFormView.editText?.text?.toString() ?: ""

        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            onSignInResult(it)
        }
    }

    private fun onSignInResult(authResult: Task<AuthResult>) {
        if (authResult.isSuccessful) {
            handleSignInSuccess()
        }

        handleSignInFailure(authResult.result)
    }

    private fun handleSignInSuccess() {
        Log.d(TAG, "Sign in successful")
        goToMainActivity()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        return startActivity(intent)
    }

    private fun handleSignInFailure(response: AuthResult?) {
        if(response == null) {
            return goToMainActivity()
        }

        Log.w(TAG, "Sign in error: $response")
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}