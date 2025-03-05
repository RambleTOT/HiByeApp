package ramble.sokol.hibyeapp.data.model.events

import com.google.gson.annotations.SerializedName

data class EventsEntity(
    @SerializedName("eventId")
    val eventId: Long? = null,

    @SerializedName("eventName")
    val eventName: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("customFormFieldsRawJson")
    val customFormFieldsRawJson: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("features")
    val features: List<String>? = null,

    @SerializedName("startingDateTime")
    val startingDateTime: String? = null,

    @SerializedName("endingDateTime")
    val endingDateTime: String? = null,

    @SerializedName("chatLink")
    val chatLink: String? = null,

    @SerializedName("participantsNumber")
    val participantsNumber: Int? = null,

    @SerializedName("scheduleId")
    val scheduleId: Long? = null,

    @SerializedName("ownerId")
    val ownerId: Long? = null,

    @SerializedName("pin")
    val pin: String? = null
)
