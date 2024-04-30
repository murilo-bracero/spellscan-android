package com.example.spellscanapp.service

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.spellscanapp.provider.AuthorizationProvider
import com.example.spellscanapp.provider.AuthorizationProvider.Companion.AUTHORIZATION_CONFIG
import com.example.spellscanapp.repository.AuthStateRepository
import com.example.spellscanapp.repository.isValid
import com.example.spellscanapp.ui.MainActivity
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest

class AuthService(private val authStateRepository: AuthStateRepository) {

    fun isAuthorized(context: Context) = authStateRepository.find(context).isAuthorized

    fun logout(context: Context) {
        val state = authStateRepository.find(context)
        state.update(null)
        authStateRepository.save(context, state)

        val endSessionRequest = EndSessionRequest.Builder(AUTHORIZATION_CONFIG)
            .build()

        val authService = AuthorizationService(context)
        authService.performEndSessionRequest(
            endSessionRequest, PendingIntent.getActivity(
                context, 0, Intent(
                    context,
                    MainActivity::class.java
                ), FLAG_IMMUTABLE
            )
        )
    }

    fun applyAccessToken(context: Context, success: (String) -> Any, error: () -> Unit) {
        val authState = authStateRepository.find(context)

        if (authState.isValid()) {
            success(authState.accessToken!!)
            return
        }

        authState.performActionWithFreshTokens(AuthorizationService(context)) { accessToken, _, ex ->
            if(ex != null || accessToken.isNullOrEmpty()) {
                Log.d(TAG, "Failed to get access token: $ex")
                return@performActionWithFreshTokens error()
            }

            success(accessToken)
            authStateRepository.save(context, authState)
        }
    }

    companion object {
        const val TAG = "AuthService"
    }
}