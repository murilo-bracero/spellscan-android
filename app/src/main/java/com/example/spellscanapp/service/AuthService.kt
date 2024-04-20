package com.example.spellscanapp.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.spellscanapp.provider.AuthorizationProvider
import com.example.spellscanapp.repository.AuthStateRepository
import com.example.spellscanapp.ui.MainActivity
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest

class AuthService(private val authStateRepository: AuthStateRepository) {

    fun isAuthorized(context: Context) = authStateRepository.find(context).isAuthorized

    fun logout(context: Context) {
        val state = authStateRepository.find(context)
        state.update(null)
        authStateRepository.save(context, state)

        val endSessionRequest = EndSessionRequest.Builder(AuthorizationProvider.AUTHORIZATION_CONFIG)
            .build()

        val authService = AuthorizationService(context)
        authService.performEndSessionRequest(
            endSessionRequest, PendingIntent.getActivity(
                context, 0, Intent(
                    context,
                    MainActivity::class.java
                ), PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}