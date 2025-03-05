package ramble.sokol.hibyeapp.data.model.schedule

import com.google.gson.annotations.SerializedName

data class ScheduleAddFavoriteEntity(

    @SerializedName("userId")
    val userId: Long? = null,

    @SerializedName("scheduleId")
    val scheduleId: Long? = null,

)