package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.auth.LoginEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import ramble.sokol.hibyeapp.data.model.events.EventsEntity
import ramble.sokol.hibyeapp.data.model.events.JoinByPinEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface EventsApi {
    @POST("/events/join_by_pin")
    fun joinByPin(@Body request: JoinByPinEntity): Call<EventsEntity>

}


