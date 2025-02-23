package ramble.sokol.hibyeapp.data

import ramble.sokol.hibyeapp.managers.TokenManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ramble.sokol.hibyeapp.data.api.AuthApi
import ramble.sokol.hibyeapp.data.model.LoginEntity
import ramble.sokol.hibyeapp.data.model.RegistrationEntity
import ramble.sokol.hibyeapp.data.model.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.TokenResponse
import ramble.sokol.hibyeapp.view.getExpFromToken
import ramble.sokol.hibyeapp.view.getUserIdFromToken
import retrofit2.awaitResponse

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun login(phone: String, password: String): Result<TokenResponse> {
        return try {
            val response = authApi.login(LoginEntity(phone, password)).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val tokens = response.body()!!
                Log.d("MyLog", "$tokens")
                tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                extractAndSaveUserId(tokens.accessToken)
                extractAndSaveExp(tokens.accessToken)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(phone: String, password: String): Result<TokenResponse> {
        return try {
            val response = authApi.register(RegistrationEntity(phone, password)).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val tokens = response.body()!!
                tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                extractAndSaveUserId(tokens.accessToken)
                extractAndSaveExp(tokens.accessToken)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerTelegram(registrationTelegramEntity: RegistrationTelegramEntity): Result<RegistrationTelegramEntity> {
        return try {
            val response = authApi.registerTelegram(registrationTelegramEntity).awaitResponse()
            if (response.isSuccessful && response.body() != null && response.body()!!.telegramId != null) {
                val result = response.body()!!
                tokenManager.saveTelegramId(result.telegramId!!)
                tokenManager.saveUserIdTelegram(result.userId!!)
                Log.d("MyLog", "${result.telegramId} ${result.userId}")
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(): Result<TokenResponse> {
        return  withContext(Dispatchers.IO) {
            try {
                val refreshToken = tokenManager.getRefreshToken() ?: return@withContext Result.failure(Exception("No refresh token"))
                val response = authApi.refreshToken(refreshToken).awaitResponse()
                Log.d("MyLog", response.toString())
                if (response.isSuccessful && response.body() != null) {
                    val tokens = response.body()!!
                    tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                    extractAndSaveUserId(tokens.accessToken)
                    extractAndSaveExp(tokens.accessToken)
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Token refresh failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun extractAndSaveUserId(accessToken: String) {
        val userId = getUserIdFromToken(accessToken)
        Log.d("MyLog", userId.toString())
        if (userId != null) {
            tokenManager.saveUserId(userId)
        }
    }

    private fun extractAndSaveExp(accessToken: String) {
        val exp = getExpFromToken(accessToken)
        Log.d("MyLog", exp.toString())
        if (exp != null) {
            tokenManager.saveExp(exp)
        }
    }

}