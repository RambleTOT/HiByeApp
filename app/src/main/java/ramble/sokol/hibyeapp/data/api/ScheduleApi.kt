package ramble.sokol.hibyeapp.data.api

import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApi {

    @GET("/schedule/{scheduleId}")
    fun getSchedule(
        @Path("scheduleId") scheduleId: Long,
    ): Call<ScheduleResponse>

}


