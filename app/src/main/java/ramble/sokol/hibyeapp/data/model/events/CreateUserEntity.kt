package ramble.sokol.hibyeapp.data.model.events

import com.google.gson.annotations.SerializedName

data class CreateUserEntity(
    @SerializedName("userId")
    val userId: Long? = null,

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

)
