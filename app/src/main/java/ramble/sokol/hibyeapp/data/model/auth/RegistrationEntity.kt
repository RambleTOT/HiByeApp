package ramble.sokol.hibyeapp.data.model.auth

import com.google.gson.annotations.SerializedName

data class RegistrationEntity(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String
)
