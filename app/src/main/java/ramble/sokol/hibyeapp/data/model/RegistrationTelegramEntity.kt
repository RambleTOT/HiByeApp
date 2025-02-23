package ramble.sokol.hibyeapp.data.model

import com.google.gson.annotations.SerializedName

data class RegistrationTelegramEntity(
    @SerializedName("userId")
    val userId: Long? = null,
    @SerializedName("telegramId")
    val telegramId: Long? = null,
    @SerializedName("telegramName")
    val telegramName: String? = null,
    @SerializedName("photoLink")
    val photoLink: String? = null,
    @SerializedName("miniPhotoLink")
    val miniPhotoLink: String? = null,
    @SerializedName("telegramUrl")
    val telegramUrl: String? = null,
)
