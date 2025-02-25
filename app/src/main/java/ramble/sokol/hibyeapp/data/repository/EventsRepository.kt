package ramble.sokol.hibyeapp.data.repository

import ramble.sokol.hibyeapp.managers.TokenManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ramble.sokol.hibyeapp.data.api.AuthApi
import ramble.sokol.hibyeapp.data.api.EventsApi
import ramble.sokol.hibyeapp.data.model.auth.LoginEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import ramble.sokol.hibyeapp.data.model.events.EventsEntity
import ramble.sokol.hibyeapp.data.model.events.JoinByPinEntity
import ramble.sokol.hibyeapp.view.getExpFromToken
import ramble.sokol.hibyeapp.view.getUserIdFromToken
import retrofit2.awaitResponse

class EventsRepository(
    private val eventsApi: EventsApi,
    private val tokenManager: TokenManager
) {

    suspend fun joinByPin(pin: String, telegramId: Long, userId: Long): Result<EventsEntity> {
        return try {
            val response = eventsApi.joinByPin(JoinByPinEntity(pin, telegramId, userId)).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                tokenManager.saveCurrentEvent(result.eventId!!.toLong())
                tokenManager.saveCurrentScheduleId(result.scheduleId!!.toLong())
                Log.d("MyLog", "$result")
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEvents(telegramId: Long): Result<List<EventsEntity>> {
        return try {
                val response = eventsApi.getUserEvents(telegramId).awaitResponse()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch events: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
    }

}