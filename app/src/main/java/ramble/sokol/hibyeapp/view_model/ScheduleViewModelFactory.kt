package ramble.sokol.hibyeapp.view_model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.data.repository.AuthRepository
import ramble.sokol.hibyeapp.data.repository.EventsRepository
import ramble.sokol.hibyeapp.data.repository.MeetsRepository
import ramble.sokol.hibyeapp.data.repository.ScheduleRepository

class ScheduleViewModelFactory(private val scheduleRepository: ScheduleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            return ScheduleViewModel(scheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}