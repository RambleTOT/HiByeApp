package ramble.sokol.hibyeapp.view

import ramble.sokol.hibyeapp.managers.TokenManager
import android.app.Application
import ramble.sokol.hibyeapp.data.repository.AuthRepository
import ramble.sokol.hibyeapp.data.RetrofitClient
import ramble.sokol.hibyeapp.data.repository.EventsRepository
import ramble.sokol.hibyeapp.data.repository.MeetsRepository

class MyApplication : Application() {

    val tokenManager: TokenManager by lazy {
        TokenManager(this)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(RetrofitClient.instance, tokenManager)
    }

    val eventsRepository: EventsRepository by lazy {
        EventsRepository(RetrofitClient.instanceEvents, tokenManager)
    }

    val meetsRepository: MeetsRepository by lazy {
        MeetsRepository(RetrofitClient.instanceMeets, tokenManager)
    }
}