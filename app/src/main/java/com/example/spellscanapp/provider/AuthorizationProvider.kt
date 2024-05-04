package com.example.spellscanapp.provider

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.example.spellscanapp.BuildConfig.OIDC_HOST
import com.example.spellscanapp.repository.AuthStateRepository
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues.CODE


class AuthorizationProvider(private val activity: FragmentActivity) {
    private val authorizationService: AuthorizationService = AuthorizationService(activity)
    private val authState: AuthState
    private val authStateRepository: AuthStateRepository = AuthStateRepository()

    init {
        authState = authStateRepository.find(activity)
    }

    fun createAuthorizationIntent(): Intent? {
        val authRequest = AuthorizationRequest.Builder(
            AUTHORIZATION_CONFIG,
            CLIENT_ID,
            CODE,
            Uri.parse(REDIRECT_URI)
        )
            .build()

        return authorizationService
            .getAuthorizationRequestIntent(authRequest)
    }

    fun createAuthorizationResultLauncher(
        onSuccessCallback: () -> Unit,
        onErrorCallback: () -> Unit
    ): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val ex = AuthorizationException.fromIntent(it.data!!)
                val result = AuthorizationResponse.fromIntent(it.data!!)

                authState.update(result, ex)

                if (ex != null || result == null) {
                    Log.e(TAG, "Failed to authorize user: $ex")
                    onErrorCallback()
                    return@registerForActivityResult
                }

                authStateRepository.save(activity, authState)

                val tokenRequest = result.createTokenExchangeRequest()

                authorizationService
                    .performTokenRequest(tokenRequest) { response, exception ->
                        authState.update(response, exception)

                        if (exception != null) {
                            Log.e(TAG, "failed token request: $exception")
                            return@performTokenRequest
                        }

                        authStateRepository.save(activity, authState)
                    }

                onSuccessCallback()
            }
        }
    }

    companion object {
        private const val TAG = "AuthorizationProvider"
        const val TOKEN_ENDPOINT =
            "https://$OIDC_HOST/realms/spellscan/protocol/openid-connect/token"
        private const val AUTH_ENDPOINT =
            "https://$OIDC_HOST/realms/spellscan/protocol/openid-connect/auth"
        const val CLIENT_ID = "spellscan-mobile-app"
        private const val REDIRECT_URI = "com.example.spellscanapp:/oauth2redirect"
        private const val LOGOUT_URI = "com.example.spellscanapp:/logout"

        val AUTHORIZATION_CONFIG: AuthorizationServiceConfiguration =
            AuthorizationServiceConfiguration(
                Uri.parse(AUTH_ENDPOINT),
                Uri.parse(TOKEN_ENDPOINT),
                null,
                Uri.parse(LOGOUT_URI)
            )
    }
}
