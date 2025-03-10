package ramble.sokol.hibyeapp.data.repository

import ramble.sokol.hibyeapp.managers.TokenManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ramble.sokol.hibyeapp.data.api.AuthApi
import ramble.sokol.hibyeapp.data.api.EventsApi
import ramble.sokol.hibyeapp.data.api.MeetsApi
import ramble.sokol.hibyeapp.data.model.auth.LoginEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import ramble.sokol.hibyeapp.data.model.events.CreateUserEntity
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.data.model.events.EventsEntity
import ramble.sokol.hibyeapp.data.model.events.JoinByPinEntity
import ramble.sokol.hibyeapp.data.model.meets.MeetingIdEntity
import ramble.sokol.hibyeapp.data.model.meets.MeetingResponse
import ramble.sokol.hibyeapp.data.model.meets.SendMeetingRequestEntity
import ramble.sokol.hibyeapp.view.getExpFromToken
import ramble.sokol.hibyeapp.view.getUserIdFromToken
import retrofit2.awaitResponse

class MeetsRepository(
    private val meetsApi: MeetsApi,
    private val tokenManager: TokenManager
) {

    suspend fun isFastMeeting(eventId: Long, userId: Long): Result<Boolean> {
        return try {
            val response = meetsApi.isFastMeetingExist(eventId, userId).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "Resullt^ ==: $result")
                Result.success(response.body()!!)
            } else {
                Log.d("MyLog", "Response^ ==: $response")
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllMeets(eventId: Long, userId: Long): Result<List<MeetingResponse>> {
        return try {
            val response = meetsApi.getAllMeets(eventId, userId).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "Resullt^ ==: $result")
                Result.success(response.body()!!)
            } else {
                Log.d("MyLog", "Response^ ==: $response")
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllAvailableMeets(eventId: Long, userId: Long): Result<List<MeetingResponse>> {
        return try {
            val response = meetsApi.getAllAvailableMeets(eventId, userId).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "Resullt^ ==: $result")
                Result.success(response.body()!!)
            } else {
                Log.d("MyLog", "Response^ ==: $response")
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllEndedMeets(eventId: Long, userId: Long): Result<List<MeetingResponse>> {
        return try {
            val response = meetsApi.getAllEndedMeets(eventId, userId).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "Resullt^ ==: $result")
                Result.success(response.body()!!)
            } else {
                Log.d("MyLog", "Response^ ==: $response")
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postMeetingRequest(eventId: Long, sendMeetingRequestEntity: SendMeetingRequestEntity): Result<MeetingResponse> {
        return try {
            val response = meetsApi.sendMeetingRequest(eventId, sendMeetingRequestEntity).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "Resullt^ ==: $result")
                Result.success(response.body()!!)
            } else {
                Log.d("MyLog", "Response^ ==: $response")
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postMeetingAnswer(eventId: Long, sendMeetingRequestEntity: SendMeetingRequestEntity): Result<MeetingResponse> {
        return try {
            val response = meetsApi.sendMeetingAnswer(eventId, sendMeetingRequestEntity).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "Resullt^ ==: $result")
                Result.success(response.body()!!)
            } else {
                Log.d("MyLog", "Response^ ==: $response")
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createGroupMeeting(eventId: Long, meetingResponse: MeetingResponse): Result<MeetingResponse> {
        return try {
            val response = meetsApi.createGroupMeeting(eventId, meetingResponse).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "Resullt^ ==: $result")
                Result.success(response.body()!!)
            } else {
                Log.d("MyLog", "Response^ ==: $response")
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinGroupMeeting(eventId: Long, userId: Long, meetingIdEntity: MeetingIdEntity): Result<MeetingResponse> {
        return try {
            val response = meetsApi.joinGroupMeeting(eventId, userId, meetingIdEntity).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Log.d("MyLog", "Resullt^ ==: $result")
                Result.success(response.body()!!)
            } else {
                Log.d("MyLog", "Response^ ==: $response")
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun meetingNotBegin(eventId: Long, userId: Long, meetingIdEntity: MeetingIdEntity): Result<String> {
        return try {
            val response = meetsApi.meetingNotBegin(eventId, userId, meetingIdEntity).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun meetingFinished(eventId: Long, meetingIdEntity: MeetingIdEntity): Result<String> {
        return try {
            val response = meetsApi.meetingFinished(eventId, meetingIdEntity).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun meetingLeft(eventId: Long, userId: Long, meetingIdEntity: MeetingIdEntity): Result<String> {
        return try {
            val response = meetsApi.meetingLeft(eventId, userId, meetingIdEntity).awaitResponse()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}