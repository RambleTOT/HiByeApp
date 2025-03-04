package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.auth.LoginEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationEntity
import ramble.sokol.hibyeapp.data.model.auth.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import ramble.sokol.hibyeapp.data.model.chat.ChatReadEntity
import ramble.sokol.hibyeapp.data.model.chat.ChatResponse
import ramble.sokol.hibyeapp.data.model.chat.ChatSendMessageEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {

    @POST("/chats/read")
    fun chatRead(@Body chatReadEntity: ChatReadEntity): Call<String>

    @POST("/chats/messages/{eventId}")
    fun sendMessage(
        @Path("eventId") eventId: Long,
        @Body chatSendMessageEntity: ChatSendMessageEntity
    ): Call<ChatSendMessageEntity>

    @GET("/chats/{eventId}/{userId}")
    fun getAllChats(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long,
    ): Call<List<ChatResponse>>

    @GET("/chats/messages/{eventId}/{userId}/{chatId}")
    fun getAllChatMessage(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long,
        @Path("chatId") chatId: Long
    ): Call<List<ChatSendMessageEntity>>

}




