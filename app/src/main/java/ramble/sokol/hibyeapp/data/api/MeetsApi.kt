package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.events.CreateUserEntity
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.data.model.meets.MeetingIdEntity
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

    @GET("/meeting/get_available_custom_meetings/{eventId}/{userId}")
    fun getAllAvailableMeets(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long
    ): Call<List<MeetingResponse>>

    @GET("/meeting/ended_user_meetings/{eventId}/{userId}")
    fun getAllEndedMeets(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long
    ): Call<List<MeetingResponse>>

    @POST("/meeting/create_custom_meeting/{eventId}")
    fun createGroupMeeting(
        @Path("eventId") eventId: Long,
        @Body meetingResponse: MeetingResponse
    ): Call<MeetingResponse>

    @POST("/meeting/join_meeting/{eventId}/{userId}")
    fun joinGroupMeeting(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long,
        @Body meetingIdEntity: MeetingIdEntity
    ): Call<MeetingResponse>

    @POST("/meeting/meeting_not_happend/{eventId}/{userId}")
    fun meetingNotBegin(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long,
        @Body meetingIdEntity: MeetingIdEntity
    ): Call<String>

    @POST("/meeting/mark_meeting_finished/{eventId}")
    fun meetingFinished(
        @Path("eventId") eventId: Long,
        @Body meetingIdEntity: MeetingIdEntity
    ): Call<String>

    @POST("/meeting/left_meeting/{eventId}/{userId}")
    fun meetingLeft(
        @Path("eventId") eventId: Long,
        @Path("userId") userId: Long,
        @Body meetingIdEntity: MeetingIdEntity
    ): Call<String>

}


