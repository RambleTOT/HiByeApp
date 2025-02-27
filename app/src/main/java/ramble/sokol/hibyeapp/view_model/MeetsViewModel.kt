package ramble.sokol.hibyeapp.view_model

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
import ramble.sokol.hibyeapp.data.repository.EventsRepository
import ramble.sokol.hibyeapp.data.repository.MeetsRepository

class MeetsViewModel(private val meetsRepository: MeetsRepository) : ViewModel() {

    private val _isFastMeetings = MutableLiveData<Result<Boolean>>()
    val isFastMeetings: LiveData<Result<Boolean>> get() = _isFastMeetings

    fun isFastMeetings(eventId: Long, userId: Long){
        viewModelScope.launch {
            _isFastMeetings.value = meetsRepository.isFastMeeting(eventId, userId)
        }
    }

}