package ramble.sokol.hibyeapp.data.model.meets

import com.google.gson.annotations.SerializedName

data class MeetingResponse(
    @SerializedName("eventId")
    val eventId: Long? = null,

    @SerializedName("meetingId")
    val meetingId: Long? = null,

    @SerializedName("organisatorId")
    val organisatorId: Long? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("capacity")
    val capacity: Int? = null,

    @SerializedName("timeStart")
    val timeStart: String? = null,

    @SerializedName("meetingType")
    val meetingType: String? = null,

    @SerializedName("meetingStatus")
    val meetingStatus: String? = null,

    @SerializedName("mark")
    val mark: Int? = null,

    @SerializedName("meetingNote")
    val meetingNote: String? = null,

    @SerializedName("userIds")
    val userIds: List<Long>? = null,

    @SerializedName("card")
    val card: Int? = null,

    @SerializedName("telegramChatLink")
    val telegramChatLink: String? = null,

    @SerializedName("chatId")
    val chatId: Long? = null,

    @SerializedName("isOfficeActivity")
    val isOfficeActivity: Boolean? = null,

    @SerializedName("specialMeetingInfo")
    val specialMeetingInfo: String? = null
)