package com.example.spellscanapp.service

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.spellscanapp.exception.ExpiredTokenException
import com.example.spellscanapp.model.dto.IdpTokenResponse
import com.example.spellscanapp.provider.AuthorizationProvider.Companion.AUTHORIZATION_CONFIG
import com.example.spellscanapp.provider.AuthorizationProvider.Companion.CLIENT_ID
import com.example.spellscanapp.repository.AuthStateRepository
import com.example.spellscanapp.repository.isValid
import com.example.spellscanapp.ui.MainActivity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenResponse
import retrofit2.HttpException
import retrofit2.await
import kotlin.jvm.Throws

class AuthService(private val authStateRepository: AuthStateRepository) {

    private val identityProviderService: IdentityProviderService by lazy {
        buildIdentityProviderService()
    }

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

    @Throws(ExpiredTokenException::class)
    suspend fun <T : Any> applyAccessToken(context: Context, function: (String) -> T): T{
        val authState = authStateRepository.find(context)

        if (authState.isValid()) {
            Log.d(TAG, "Using cached access token")
            return function(authState.accessToken!!)
        }

        if (authState.needsTokenRefresh && !authState.refreshToken.isNullOrEmpty()) {
            Log.d(TAG, "Access token is expired, but refresh token is still valid.")

            val tokenResponse: IdpTokenResponse

            try {
                tokenResponse = identityProviderService.refreshAccessToken(
                    CLIENT_ID,
                    TOKEN_GRANT_TYPE,
                    authState.refreshToken!!
                ).await()
            } catch (e: HttpException) {
                if (e.code() == 400) {
                    throw ExpiredTokenException(e)
                }

                throw e
            }

            return withContext(Dispatchers.IO) {
                launch {
                    updateAuthState(context, tokenResponse, authState)
                }

                function(tokenResponse.accessToken)
            }
        }

        throw ExpiredTokenException()
    }

    private fun updateAuthState(
        context: Context,
        tokenResponse: IdpTokenResponse,
        authState: AuthState
    ) {
        val req = authState.createTokenRefreshRequest()
        val res = TokenResponse.Builder(req)
            .fromResponseJsonString(Gson().toJson(tokenResponse))
            .setRequest(req)
            .build()
        authState.update(res, null)
        authStateRepository.save(context, authState)
        Log.d(TAG, "Saved new idpToken")
    }

    companion object {
        const val TAG = "AuthService"
        const val TOKEN_GRANT_TYPE = "refresh_token"
    }
}