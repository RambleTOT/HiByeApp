package ramble.sokol.hibyeapp.data.model.chat

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("chatId")
    val chatId: Long? = null,

    @SerializedName("chatName")
    val chatName: String? = null,

    @SerializedName("chatPhoto")
    val chatPhoto: String? = null,

    @SerializedName("lastMessage")
    val lastMessage: ChatSendMessageEntity? = null,

    @SerializedName("isUserSeen")
    val isUserSeen: Boolean? = null,
)