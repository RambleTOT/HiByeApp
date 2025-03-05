package ramble.sokol.hibyeapp.data.model.chat

import com.google.gson.annotations.SerializedName

data class ChatReadEntity(
    @SerializedName("eventId")
    val eventId: Long? = null,

    @SerializedName("userId")
    val userId: Long? = null,

    @SerializedName("chatId")
    val chatId: Long? = null,
)