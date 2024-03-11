package com.example.spellscan.provider

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.spellscan.BuildConfig
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class AuthorizationProvider(private val activity: AppCompatActivity) {

    private val authorizationConfig = AuthorizationServiceConfiguration(
        Uri.parse(AUTH_ENDPOINT),
        Uri.parse(TOKEN_ENDPOINT)
    )

    fun authorize() {
        val authState = getAuthState()

        val authRequest = AuthorizationRequest.Builder(
            authorizationConfig,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(REDIRECT_URI)
        )
            .build()

        val authService = AuthorizationService(activity)

        val authIntent = authService
            .getAuthorizationRequestIntent(authRequest)

        if (authState.isAuthorized) {
            return
        }

        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val ex = AuthorizationException.fromIntent(it.data!!)
                val result = AuthorizationResponse.fromIntent(it.data!!)

                if (ex != null) {
                    Log.e(TAG, "failed launcher: $ex")
                }

                authState.update(result, ex)
                persistAuthState(authState)

                val tokenRequest = result!!.createTokenExchangeRequest()

                authService
                    .performTokenRequest(tokenRequest) { response, exception ->
                        if (exception != null) {
                            Log.e(TAG, "failed token request: $ex")

                        }

                        // TODO: Test and implement this

                        if (response != null) {
                            Log.i(TAG, "accessToken: ${response.accessToken}")
                        }

                        authState.update(response, exception)
                        persistAuthState(authState)
                    }

            }
        }
            .launch(authIntent)
    }

    private fun getAuthState(): AuthState {
        val jsonString = activity.application
            .getSharedPreferences(AUTH_STATE, MODE_PRIVATE)
            .getString(AUTH_STATE, null)

        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return AuthState()
        }

        return AuthState.jsonDeserialize(jsonString)
    }

    private fun persistAuthState(state: AuthState) {
        activity.application
            .getSharedPreferences(AUTH_STATE, MODE_PRIVATE)
            .edit()
            .putString(AUTH_STATE, state.jsonSerialize().toString())
            .apply()
    }

    companion object {
        private const val TAG = "AuthorizationProvider"
        private const val TOKEN_ENDPOINT =
            "http://${BuildConfig.OIDC_HOST}:${BuildConfig.OIDC_PORT}/realms/spellscan-dev/protocol/openid-connect/token"
        private const val AUTH_ENDPOINT =
            "http://${BuildConfig.OIDC_HOST}:${BuildConfig.OIDC_PORT}/realms/spellscan-dev/protocol/openid-connect/auth"
        private const val CLIENT_ID = "spellscan-mobile-app"
        private const val REDIRECT_URI = "com.example.spellscan:/oauth2redirect"
        private const val AUTH_STATE = "auth_state"
    }
}
