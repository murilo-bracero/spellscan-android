package com.example.spellscanapp.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.text.TextUtils.isEmpty
import net.openid.appauth.AuthState

class AuthStateRepository {

    fun find(context: Context): AuthState {
        val jsonString = context
            .getSharedPreferences(AUTH_STATE, MODE_PRIVATE)
            .getString(AUTH_STATE, null)

        if (jsonString == null || isEmpty(jsonString)) {
            return AuthState()
        }

        return AuthState.jsonDeserialize(jsonString)
    }

    fun save(context: Context, state: AuthState) {
        context
            .getSharedPreferences(AUTH_STATE, MODE_PRIVATE)
            .edit()
            .putString(AUTH_STATE, state.jsonSerialize().toString())
            .apply()
    }

    companion object {
        private const val AUTH_STATE = "auth_state"
    }
}

fun AuthState.isValid(): Boolean {
    return this.isAuthorized && this.accessToken != null && !this.needsTokenRefresh
}