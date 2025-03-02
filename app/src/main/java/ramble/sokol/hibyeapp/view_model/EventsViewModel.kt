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

class EventsViewModel(private val eventsRepository: EventsRepository) : ViewModel() {

    private val _joinByPinResult = MutableLiveData<Result<EventsEntity>>()
    val joinByPinResult: LiveData<Result<EventsEntity>> get() = _joinByPinResult

    private val _events = MutableLiveData<List<EventsEntity>?>()
    val events: MutableLiveData<List<EventsEntity>?> get() = _events

    private val _createUser = MutableLiveData<Result<CreateUserResponse>>()
    val createUser: LiveData<Result<CreateUserResponse>> get() = _createUser

    private val _getUser = MutableLiveData<Result<CreateUserResponse>>()
    val getUser: LiveData<Result<CreateUserResponse>> get() = _getUser

    private val _getAllUsersEvent = MutableLiveData<Result<List<CreateUserResponse>>>()
    val getAllUsersEvent: LiveData<Result<List<CreateUserResponse>>> get() = _getAllUsersEvent

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

    fun createUser(eventId: Long, userId: Long, createUserEntity: CreateUserEntity){
        viewModelScope.launch {
            _createUser.value = eventsRepository.createUser(eventId, userId, createUserEntity)
        }
    }

    fun getUser(eventId: Long, userId: Long){
        viewModelScope.launch {
            _getUser.value = eventsRepository.getUser(eventId, userId)
        }
    }

    fun getAllUsersEvent(eventId: Long){
        viewModelScope.launch {
            _getAllUsersEvent.value = eventsRepository.getAllUsersEvents(eventId)
        }
    }

}