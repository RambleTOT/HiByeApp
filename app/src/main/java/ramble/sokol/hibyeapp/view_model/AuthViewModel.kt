package ramble.sokol.hibyeapp.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ramble.sokol.hibyeapp.data.AuthRepository
import ramble.sokol.hibyeapp.data.model.RegistrationTelegramEntity
import ramble.sokol.hibyeapp.data.model.TokenResponse

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<TokenResponse>>()
    val loginResult: LiveData<Result<TokenResponse>> get() = _loginResult

    private val _registerResult = MutableLiveData<Result<TokenResponse>>()
    val registerResult: LiveData<Result<TokenResponse>> get() = _registerResult

    private val _registerTelegramResult = MutableLiveData<Result<RegistrationTelegramEntity>>()
    val registerTelegramResult: LiveData<Result<RegistrationTelegramEntity>> get() = _registerTelegramResult

    private val _refreshTokenResult = MutableLiveData<Result<TokenResponse>>()
    val refreshTokenResult: LiveData<Result<TokenResponse>> get() = _refreshTokenResult

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = authRepository.login(phone, password)
        }
    }

    fun register(phone: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = authRepository.register(phone, password)
        }
    }

    fun registerTelegram(registrationTelegramEntity: RegistrationTelegramEntity) {
        viewModelScope.launch {
            _registerTelegramResult.value = authRepository.registerTelegram(registrationTelegramEntity)
        }
    }

    fun refreshToken() {
        viewModelScope.launch {
            _refreshTokenResult.value = authRepository.refreshToken()
        }
    }
}