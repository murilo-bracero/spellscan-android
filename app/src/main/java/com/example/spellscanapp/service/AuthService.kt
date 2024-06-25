package com.example.spellscanapp.service

import android.content.Context
import com.example.spellscanapp.exception.ExpiredTokenException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthService(context: Context) {

    init {
        FirebaseApp.initializeApp(context)
    }

    fun isAuthorized() = Firebase.auth.currentUser != null

    fun logout() = Firebase.auth.signOut()

    fun getCurrentUser() = Firebase.auth.currentUser

    fun sendPasswordResetEmail(email: String) = Firebase.auth.sendPasswordResetEmail(email)

    @Throws(ExpiredTokenException::class)
    suspend fun <T : Any> applyAccessToken(function: (String) -> T): T {
        val user = getCurrentUser() ?: throw ExpiredTokenException()

        return withContext(Dispatchers.IO) {
            val getIdTokenResponse = user.getIdToken(false).await()

            if(getIdTokenResponse.token == null) {
                throw ExpiredTokenException()
            }

            function(getIdTokenResponse.token!!)
        }
    }
}