package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.events.CreateUserEntity
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.data.model.meets.MeetingResponse
import ramble.sokol.hibyeapp.data.model.meets.SendMeetingRequestEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MeetsApi {

    @GET("/meeting/is_fast_meeting_exist/{eventId}/{userId}")
    fun isFastMeetingExist(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long
    ): Call<Boolean>

    @POST("/meeting/{eventId}/send_meeting_request")
    fun sendMeetingRequest(
        @Path("eventId") eventId: Long,
        @Body sendMeetingRequestEntity: SendMeetingRequestEntity
    ): Call<MeetingResponse>

}


