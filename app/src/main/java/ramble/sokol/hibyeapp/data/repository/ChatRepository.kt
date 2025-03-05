package ramble.sokol.hibyeapp.data.repository

import ramble.sokol.hibyeapp.managers.TokenManager
import android.util.Log
import ramble.sokol.hibyeapp.data.api.ChatApi
import ramble.sokol.hibyeapp.data.api.MeetsApi
import ramble.sokol.hibyeapp.data.model.chat.ChatReadEntity
import ramble.sokol.hibyeapp.data.model.chat.ChatResponse
import ramble.sokol.hibyeapp.data.model.chat.ChatSendMessageEntity
import ramble.sokol.hibyeapp.data.model.meets.MeetingResponse
import ramble.sokol.hibyeapp.data.model.meets.SendMeetingRequestEntity
import retrofit2.awaitResponse

class ChatRepository(
    private val chatApi: ChatApi,
    private val tokenManager: TokenManager
) {

    suspend fun getAllChat(eventId: Long, userId: Long): Result<List<ChatResponse>> {
        return try {
            val response = chatApi.getAllChats(eventId, userId ).awaitResponse()
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

    suspend fun getAllChatMessage(eventId: Long, userId: Long, chatId: Long): Result<List<ChatSendMessageEntity>> {
        return try {
            val response = chatApi.getAllChatMessage(eventId, userId, chatId).awaitResponse()
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

    suspend fun chatRead(chatReadEntity: ChatReadEntity): Result<String> {
        return try {
            val response = chatApi.chatRead(chatReadEntity).awaitResponse()
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

    suspend fun sendMessage(eventId: Long, chatSendMessageEntity: ChatSendMessageEntity): Result<ChatSendMessageEntity> {
        return try {
            val response = chatApi.sendMessage(eventId, chatSendMessageEntity).awaitResponse()
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

}