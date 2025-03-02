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

    @POST("/meeting/{eventId}/answer_meeting_request")
    fun sendMeetingAnswer(
        @Path("eventId") eventId: Long,
        @Body sendMeetingRequestEntity: SendMeetingRequestEntity
    ): Call<MeetingResponse>

    @GET("/meeting/get_user_meetings/{eventId}/{userId}")
    fun getAllMeets(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long
    ): Call<List<MeetingResponse>>

    @POST("/meeting/create_custom_meeting/{eventId}")
    fun createGroupMeeting(
        @Path("eventId") eventId: Long,
        @Body meetingResponse: MeetingResponse
    ): Call<MeetingResponse>

}


