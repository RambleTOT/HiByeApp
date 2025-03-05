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
import ramble.sokol.hibyeapp.data.model.meets.MeetingIdEntity
import ramble.sokol.hibyeapp.data.model.meets.MeetingResponse
import ramble.sokol.hibyeapp.data.model.meets.SendMeetingRequestEntity
import ramble.sokol.hibyeapp.data.repository.EventsRepository
import ramble.sokol.hibyeapp.data.repository.MeetsRepository

class MeetsViewModel(private val meetsRepository: MeetsRepository) : ViewModel() {

    private val _isFastMeetings = MutableLiveData<Result<Boolean>>()
    val isFastMeetings: LiveData<Result<Boolean>> get() = _isFastMeetings

    private val _getAllMeets = MutableLiveData<Result<List<MeetingResponse>>>()
    val getAllMeets: LiveData<Result<List<MeetingResponse>>> get() = _getAllMeets

    private val _getAllAvailableMeets = MutableLiveData<Result<List<MeetingResponse>>>()
    val getAllAvailableMeets: LiveData<Result<List<MeetingResponse>>> get() = _getAllAvailableMeets

    private val _getAllEndedMeets = MutableLiveData<Result<List<MeetingResponse>>>()
    val getAllEndedMeets: LiveData<Result<List<MeetingResponse>>> get() = _getAllEndedMeets

    private val _sendRequestMeeting = MutableLiveData<Result<MeetingResponse>>()
    val sendRequestMeeting: LiveData<Result<MeetingResponse>> get() = _sendRequestMeeting

    private val _sendAnswerMeeting = MutableLiveData<Result<MeetingResponse>>()
    val sendAnswerMeeting: LiveData<Result<MeetingResponse>> get() = _sendAnswerMeeting

    private val _sendAnswerMeetingNot = MutableLiveData<Result<MeetingResponse>>()
    val sendAnswerMeetingNot: LiveData<Result<MeetingResponse>> get() = _sendAnswerMeetingNot

    private val _createGroupMeeting = MutableLiveData<Result<MeetingResponse>>()
    val createGroupMeeting: LiveData<Result<MeetingResponse>> get() = _createGroupMeeting

    private val _joinGroupMeeting = MutableLiveData<Result<MeetingResponse>>()
    val joinGroupMeeting: LiveData<Result<MeetingResponse>> get() = joinGroupMeeting


    fun isFastMeetings(eventId: Long, userId: Long){
        viewModelScope.launch {
            _isFastMeetings.value = meetsRepository.isFastMeeting(eventId, userId)
        }
    }

    fun getAllMeets(eventId: Long, userId: Long){
        viewModelScope.launch {
            _getAllMeets.value = meetsRepository.getAllMeets(eventId, userId)
        }
    }

    fun getAllAvailableMeets(eventId: Long, userId: Long){
        viewModelScope.launch {
            _getAllAvailableMeets.value = meetsRepository.getAllAvailableMeets(eventId, userId)
        }
    }

    fun getAllEndedMeets(eventId: Long, userId: Long){
        viewModelScope.launch {
            _getAllEndedMeets.value = meetsRepository.getAllEndedMeets(eventId, userId)
        }
    }

    fun sendRequestMeeting(eventId: Long, sendMeetingRequestEntity: SendMeetingRequestEntity){
        viewModelScope.launch {
            _sendRequestMeeting.value = meetsRepository.postMeetingRequest(eventId, sendMeetingRequestEntity)
        }
    }

    fun sendAnswerMeeting(eventId: Long, sendMeetingRequestEntity: SendMeetingRequestEntity){
        viewModelScope.launch {
            _sendAnswerMeeting.value = meetsRepository.postMeetingAnswer(eventId, sendMeetingRequestEntity)
        }
    }

    fun sendAnswerMeetingNot(eventId: Long, sendMeetingRequestEntity: SendMeetingRequestEntity){
        viewModelScope.launch {
            _sendAnswerMeeting.value = meetsRepository.postMeetingAnswer(eventId, sendMeetingRequestEntity)
        }
    }

    fun createGroupMeeting(eventId: Long, meetingResponse: MeetingResponse){
        viewModelScope.launch {
            _createGroupMeeting.value = meetsRepository.createGroupMeeting(eventId, meetingResponse)
        }
    }

    fun joinGroupMeeting(eventId: Long, userId: Long, meetingIdEntity: MeetingIdEntity){
        viewModelScope.launch {
            _joinGroupMeeting.value = meetsRepository.joinGroupMeeting(eventId, userId, meetingIdEntity)
        }
    }

}