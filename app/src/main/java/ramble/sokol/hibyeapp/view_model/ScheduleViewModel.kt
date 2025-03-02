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
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleAddFavoriteEntity
import ramble.sokol.hibyeapp.data.model.schedule.ScheduleResponse
import ramble.sokol.hibyeapp.data.repository.EventsRepository
import ramble.sokol.hibyeapp.data.repository.MeetsRepository
import ramble.sokol.hibyeapp.data.repository.ScheduleRepository

class ScheduleViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {

    private val _getSchedule = MutableLiveData<Result<ScheduleResponse>>()
    val getSchedule: LiveData<Result<ScheduleResponse>> get() = _getSchedule

    private val _getFavorite = MutableLiveData<Result<List<Long>>>()
    val getFavorite: LiveData<Result<List<Long>>> get() = _getFavorite

    private val _addFavorite = MutableLiveData<Result<Any>>()
    val addFavorite: LiveData<Result<Any>> get() = _addFavorite

    fun getSchedule(scheduleId: Long){
        viewModelScope.launch {
            _getSchedule.value = scheduleRepository.getSchedule(scheduleId)
        }
    }

    fun getFavorite(parent: Long, userId: Long){
        viewModelScope.launch {
            _getFavorite.value = scheduleRepository.getFavorite(parent, userId)
        }
    }

    fun addFavorite(scheduleAddFavoriteEntity: ScheduleAddFavoriteEntity){
        viewModelScope.launch {
            _addFavorite.value = scheduleRepository.addFavorite(scheduleAddFavoriteEntity)
        }
    }

}