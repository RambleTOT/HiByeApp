package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.events.CreateUserEntity
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleAddFavoriteEntity
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApi {

    @GET("/schedule/{scheduleId}")
    fun getSchedule(
        @Path("scheduleId") scheduleId: Long,
    ): Call<ScheduleResponse>

    @POST("/schedule/favorite")
    fun addFavorite(
        @Body scheduleAddFavoriteEntity: ScheduleAddFavoriteEntity
    ): Call<Any>

    @GET("/schedule/favorite/{parentId}/{userId}")
    fun getFavorite(
        @Path("parentId") eventId: Long,
        @Path("userId") userId: Long
    ): Call<List<Long>>

}


