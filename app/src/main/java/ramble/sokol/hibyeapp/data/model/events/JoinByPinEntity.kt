package ramble.sokol.hibyeapp.data.model.events

import com.google.gson.annotations.SerializedName

data class JoinByPinEntity(
    @SerializedName("pin")
    val pin: String,
    @SerializedName("telegramId")
    val telegramId: Long,
    @SerializedName("userId")
    val userId: Long,
)
