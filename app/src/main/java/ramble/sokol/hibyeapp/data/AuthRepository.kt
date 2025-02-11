package ramble.sokol.hibyeapp.data

import ramble.sokol.hibyeapp.TokenManager
import ramble.sokol.hibyeapp.data.api.AuthApi
import ramble.sokol.hibyeapp.data.model.LoginEntity
import ramble.sokol.hibyeapp.data.model.RegistrationEntity
import ramble.sokol.hibyeapp.data.model.TokenResponse
import retrofit2.awaitResponse

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun login(phone: String, password: String): Result<TokenResponse> {
        return try {
            val response = authApi.login(LoginEntity(phone, password)).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
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
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(refreshToken: String): Result<TokenResponse> {
        return try {
            val response = authApi.refreshToken(refreshToken).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Token refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshTokens(): Result<TokenResponse> {
        val refreshToken = tokenManager.getRefreshToken() ?: return Result.failure(Exception("No refresh token"))
        return try {
            val response = authApi.refreshToken(refreshToken).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                tokenManager.saveTokens(response.body()!!.accessToken, response.body()!!.refreshToken)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Token refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}