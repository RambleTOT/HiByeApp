package ramble.sokol.hibyeapp.data.repository

import ramble.sokol.hibyeapp.managers.TokenManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ramble.sokol.hibyeapp.data.api.AuthApi
import ramble.sokol.hibyeapp.data.api.ScheduleApi
import ramble.sokol.hibyeapp.data.model.auth.LoginEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleAddFavoriteEntity
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleResponse
import ramble.sokol.hibyeapp.view.getExpFromToken
import ramble.sokol.hibyeapp.view.getUserIdFromToken
import retrofit2.awaitResponse

class ScheduleRepository(
    private val scheduleApi: ScheduleApi,
    private val tokenManager: TokenManager
) {

    suspend fun getSchedule(scheduleId: Long): Result<ScheduleResponse> {
        return try {
            val response = scheduleApi.getSchedule(scheduleId).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "$result")
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavorite(parent: Long, userId: Long): Result<List<Long>> {
        return try {
            val response = scheduleApi.getFavorite(parent, userId).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "$result")
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorite(scheduleAddFavoriteEntity: ScheduleAddFavoriteEntity): Result<Any> {
        return try {
            val response = scheduleApi.addFavorite(scheduleAddFavoriteEntity).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "favorite add $result")
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}