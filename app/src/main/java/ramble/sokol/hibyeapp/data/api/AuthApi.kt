package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.auth.LoginEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    fun login(@Body request: LoginEntity): Call<TokenResponse>

    @POST("/auth/register")
    fun register(@Body request: RegistrationEntity): Call<TokenResponse>

    @POST("/auth/register/telegram")
    fun registerTelegram(@Body request: RegistrationTelegramEntity): Call<RegistrationTelegramEntity>

    @POST("/auth/refresh")
    fun refreshToken(@Body refreshToken: String): Call<TokenResponse>
}


