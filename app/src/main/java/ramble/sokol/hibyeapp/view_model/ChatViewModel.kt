package ramble.sokol.hibyeapp.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ramble.sokol.hibyeapp.data.repository.AuthRepository
import ramble.sokol.hibyeapp.data.model.auth.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.auth.TokenResponse
import ramble.sokol.hibyeapp.data.model.chat.ChatReadEntity
import ramble.sokol.hibyeapp.data.model.chat.ChatResponse
import ramble.sokol.hibyeapp.data.model.chat.ChatSendMessageEntity
import ramble.sokol.hibyeapp.data.repository.ChatRepository

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _getAllChat = MutableLiveData<Result<List<ChatResponse>>>()
    val getAllChat: LiveData<Result<List<ChatResponse>>> get() = _getAllChat

    private val _getAllChatMessage = MutableLiveData<Result<List<ChatSendMessageEntity>>>()
    val getAllChatMessage: LiveData<Result<List<ChatSendMessageEntity>>> get() = _getAllChatMessage

    private val _chatRead = MutableLiveData<Result<String>>()
    val chatRead: LiveData<Result<String>> get() = _chatRead

    private val _sendMessage = MutableLiveData<Result<ChatSendMessageEntity>>()
    val sendMessage: LiveData<Result<ChatSendMessageEntity>> get() = _sendMessage


    fun getAllChat(eventId: Long, userId: Long) {
        viewModelScope.launch {
            _getAllChat.value = chatRepository.getAllChat(eventId, userId)
        }
    }

    fun getAllChatMessage(eventId: Long, userId: Long, chatId: Long) {
        viewModelScope.launch {
            _getAllChatMessage.value = chatRepository.getAllChatMessage(eventId, userId, chatId)
        }
    }

    fun chatRead(chatReadEntity: ChatReadEntity) {
        viewModelScope.launch {
            _chatRead.value = chatRepository.chatRead(chatReadEntity)
        }
    }

    fun sendMessage(eventId: Long, chatSendMessageEntity: ChatSendMessageEntity) {
        viewModelScope.launch {
            _sendMessage.value = chatRepository.sendMessage(eventId, chatSendMessageEntity)
        }
    }

}