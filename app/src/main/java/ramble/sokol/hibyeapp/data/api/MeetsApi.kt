package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MeetsApi {

    @GET("/meeting/is_fast_meeting_exist/{eventId}/{userId}")
    fun isFastMeetingExist(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long
    ): Call<Boolean>

}


