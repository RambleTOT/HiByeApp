package ramble.sokol.hibyeapp.view_model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.data.repository.AuthRepository
import ramble.sokol.hibyeapp.data.repository.EventsRepository
import ramble.sokol.hibyeapp.data.repository.MeetsRepository

class MeetsViewModelFactory(private val meetsRepository: MeetsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeetsViewModel::class.java)) {
            return MeetsViewModel(meetsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}