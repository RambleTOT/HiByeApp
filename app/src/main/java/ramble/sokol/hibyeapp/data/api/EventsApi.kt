package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.auth.LoginEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import ramble.sokol.hibyeapp.data.model.events.CreateUserEntity
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.data.model.events.EventsEntity
import ramble.sokol.hibyeapp.data.model.events.JoinByPinEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventsApi {
    @POST("/events/join_by_pin")
    fun joinByPin(@Body request: JoinByPinEntity): Call<EventsEntity>

    @POST("/events/telegram")
    fun getUserEvents(@Body telegramId: Long): Call<List<EventsEntity>>

    @POST("/form/{eventId}/{userId}")
    fun createUserForEvent(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long,
        @Body createUserEntity: CreateUserEntity
    ): Call<CreateUserResponse>

    @GET("/form/{eventId}/{userId}")
    fun getUserForEvent(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long
    ): Call<CreateUserResponse>

    @GET("/users")
    fun getAllUsersEvent(
        @Query("eventId") eventId: Long
    ): Call<List<CreateUserResponse>>

}


