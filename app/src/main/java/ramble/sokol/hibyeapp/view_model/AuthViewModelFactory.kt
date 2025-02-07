package ramble.sokol.hibyeapp.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.data.AuthRepository

class AuthViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}