package ramble.sokol.hibyeapp

import TokenManager
import android.app.Application
import ramble.sokol.hibyeapp.data.AuthRepository
import ramble.sokol.hibyeapp.data.RetrofitClient

class MyApplication : Application() {

    val tokenManager: TokenManager by lazy {
        TokenManager(this)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(RetrofitClient.instance, tokenManager)
    }
}