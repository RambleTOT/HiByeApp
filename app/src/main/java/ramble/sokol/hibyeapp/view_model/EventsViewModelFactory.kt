package ramble.sokol.hibyeapp.view_model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ramble.sokol.hibyeapp.data.repository.AuthRepository
import ramble.sokol.hibyeapp.data.repository.EventsRepository

class EventsViewModelFactory(private val eventsRepository: EventsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventsViewModel::class.java)) {
            return EventsViewModel(eventsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}