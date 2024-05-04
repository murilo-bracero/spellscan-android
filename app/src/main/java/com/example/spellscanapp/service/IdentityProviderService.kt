package com.example.spellscanapp.service

import com.example.spellscanapp.BuildConfig.OIDC_HOST
import com.example.spellscanapp.model.dto.IdpTokenResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IdentityProviderService {

    @FormUrlEncoded
    @POST("realms/spellscan/protocol/openid-connect/token")
    fun refreshAccessToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String
    ): Call<IdpTokenResponse>
}

fun buildIdentityProviderService(): IdentityProviderService {
    return Retrofit.Builder()
        .baseUrl("https://$OIDC_HOST/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(IdentityProviderService::class.java)
}