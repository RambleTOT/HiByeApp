package ramble.sokol.hibyeapp.data.model.schedule

import com.google.gson.annotations.SerializedName

data class ScheduleItem(
    @SerializedName("scheduleId")
    val scheduleId: Long? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("parentId")
    val parentId: Long? = null,

    @SerializedName("timeStart")
    val timeStart: String? = null,

    @SerializedName("timeEnd")
    val timeEnd: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("tags")
    val tags: List<String>? = null,

    @SerializedName("section")
    val section: String? = null
)