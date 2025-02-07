package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.LoginEntity
import ramble.sokol.hibyeapp.data.model.RegistrationEntity
import ramble.sokol.hibyeapp.data.model.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    fun login(@Body request: LoginEntity): Call<TokenResponse>

    @POST("/auth/register")
    fun register(@Body request: RegistrationEntity): Call<TokenResponse>

    @POST("/auth/refresh")
    fun refreshToken(@Body refreshToken: String): Call<TokenResponse>
}