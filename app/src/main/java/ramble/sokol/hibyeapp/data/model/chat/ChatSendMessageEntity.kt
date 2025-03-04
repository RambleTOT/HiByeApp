package ramble.sokol.hibyeapp.data.model.chat

import com.google.gson.annotations.SerializedName

data class ChatSendMessageEntity(
    @SerializedName("messageId")
    val messageId: Long? = null,

    @SerializedName("chatId")
    val chatId: Long? = null,

    @SerializedName("userId")
    val userId: Long? = null,

    @SerializedName("messageText")
    val messageText: String? = null,

    @SerializedName("timestamp")
    val timestamp: String? = null,
)