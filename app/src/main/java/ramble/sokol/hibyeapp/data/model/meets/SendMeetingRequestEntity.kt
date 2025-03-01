package ramble.sokol.hibyeapp.data.model.meets

import com.google.gson.annotations.SerializedName

data class SendMeetingRequestEntity(
    @SerializedName("eventId")
    val userId: Long? = null,

    @SerializedName("idFrom")
    val eventId: Long? = null,

    @SerializedName("idTo")
    val userInfo: String? = null,

    @SerializedName("status")
    val userName: String? = null,

    @SerializedName("meetingType")
    val request: String? = null

)
