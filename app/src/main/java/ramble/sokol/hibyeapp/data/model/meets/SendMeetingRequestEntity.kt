package ramble.sokol.hibyeapp.data.model.meets

import com.google.gson.annotations.SerializedName

data class SendMeetingRequestEntity(
    @SerializedName("eventId")
    val eventId: Long? = null,

    @SerializedName("idFrom")
    val idFrom: Long? = null,

    @SerializedName("idTo")
    val idTo: Long? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("meetingType")
    val meetingType: String? = null

)
