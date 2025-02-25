package ramble.sokol.hibyeapp.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ramble.sokol.hibyeapp.data.repository.AuthRepository
import ramble.sokol.hibyeapp.data.model.auth.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import ramble.sokol.hibyeapp.data.model.events.EventsEntity
import ramble.sokol.hibyeapp.data.repository.EventsRepository

class EventsViewModel(private val eventsRepository: EventsRepository) : ViewModel() {

    private val _joinByPinResult = MutableLiveData<Result<EventsEntity>>()
    val joinByPinResult: LiveData<Result<EventsEntity>> get() = _joinByPinResult

    private val _events = MutableLiveData<List<EventsEntity>>()
    val events: LiveData<List<EventsEntity>> get() = _events


    fun joinByPin(pin: String, telegramId: Long, userId: Long) {
        viewModelScope.launch {
            _joinByPinResult.value = eventsRepository.joinByPin(pin, telegramId, userId)
        }
    }

    fun fetchEvents(telegramId: Long) {
        viewModelScope.launch {
            _events.value = eventsRepository.getEvents(telegramId).getOrNull()
        }
    }


}