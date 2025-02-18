//package ramble.sokol.hibyeapp.data
//
//import ramble.sokol.hibyeapp.managers.TokenManager
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import okhttp3.Interceptor
//import okhttp3.Response
//import ramble.sokol.hibyeapp.LoginFragment
//import ramble.sokol.hibyeapp.R
//import java.io.IOException
//
//class AuthInterceptor(
//    private val tokenManager: ramble.sokol.hibyeapp.managers.TokenManager,
//    private val authRepository: AuthRepository
//) : ViewModel(), Interceptor {
//
//    @Throws(IOException::class)
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//
//        // Добавляем accessToken к запросу
//        val accessToken = tokenManager.getAccessToken()
//        val requestWithToken = originalRequest.newBuilder()
//            .header("Authorization", "Bearer $accessToken")
//            .build()
//
//        // Выполняем запрос
//        val response = chain.proceed(requestWithToken)
//
//        // Если accessToken истек (ошибка 401), обновляем его
//        if (response.code == 401) {
//            synchronized(this) {
//                val newToken = runBlocking { // Используем runBlocking для вызова suspend-функции
//                    refreshToken()
//                }
//                if (newToken != null) {
//                    // Повторяем запрос с новым accessToken
//                    val newRequest = originalRequest.newBuilder()
//                        .header("Authorization", "Bearer $newToken")
//                        .build()
//                    return chain.proceed(newRequest)
//                }else{
////                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
////                    val loginFragment = LoginFragment()
////                    transaction.replace(R.id.layout_fragment, loginFragment)
////                    transaction.disallowAddToBackStack()
////                    transaction.commit()
////                    return response
//                }
//            }
//        }
//
//        return response
//    }
//
//    private suspend fun refreshToken(): String? {
//        val refreshToken = tokenManager.getRefreshToken() ?: return null
//
//        val result = authRepository.refreshToken(refreshToken)
//        return if (result.isSuccess) {
//            tokenManager.saveTokens(result.getOrNull()!!.accessToken, result.getOrNull()!!.refreshToken)
//            result.getOrNull()!!.accessToken
//        } else {
//            null
//        }
//    }
//}