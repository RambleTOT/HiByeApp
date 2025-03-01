package ramble.sokol.hibyeapp.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ramble.sokol.hibyeapp.data.repository.AuthRepository
import ramble.sokol.hibyeapp.data.model.auth.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import ramble.sokol.hibyeapp.data.model.events.CreateUserEntity
import ramble.sokol.hibyeapp.data.model.events.CreateUserResponse
import ramble.sokol.hibyeapp.data.model.events.EventsEntity
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleResponse
import ramble.sokol.hibyeapp.data.repository.EventsRepository
import ramble.sokol.hibyeapp.data.repository.MeetsRepository
import ramble.sokol.hibyeapp.data.repository.ScheduleRepository

class ScheduleViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {

    private val _getSchedule = MutableLiveData<Result<ScheduleResponse>>()
    val getSchedule: LiveData<Result<ScheduleResponse>> get() = _getSchedule

    fun getSchedule(scheduleId: Long){
        viewModelScope.launch {
            Log.d("MyLog", "ViewModelSchedule")
            _getSchedule.value = scheduleRepository.getSchedule(scheduleId)
        }
    }

}