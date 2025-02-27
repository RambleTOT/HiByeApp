package ramble.sokol.hibyeapp.data.model.events

import com.google.gson.annotations.SerializedName

data class CreateUserResponse(
    @SerializedName("userId")
    val userId: Long? = null,

    @SerializedName("telegramId")
    val telegramId: Long? = null,

    @SerializedName("telegramName")
    val telegramName: String? = null,

    @SerializedName("eventId")
    val eventId: Long? = null,

    @SerializedName("userInfo")
    val userInfo: String? = null,

    @SerializedName("userName")
    val userName: String? = null,

    @SerializedName("request")
    val request: String? = null,

    @SerializedName("photoLink")
    val photoLink: String? = null,

    @SerializedName("miniPhotoLink")
    val miniPhotoLink: String? = null,

    @SerializedName("telegramUrl")
    val telegramUrl: String? = null,

    @SerializedName("role")
    val role: String? = null,

)
