package ramble.sokol.hibyeapp.managers

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenManager(context: Context) {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            Log.d("MyLog", "$accessToken $refreshToken")
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun saveUserId(userId: Int) {
        sharedPreferences.edit().apply {
            putInt("user_id", userId)
            apply()
        }
    }

    fun getUserId(): Int? {
        return sharedPreferences.getInt("user_id", -1).takeIf { it != -1 }
    }

    fun saveExp(exp: Long) {
        sharedPreferences.edit().apply {
            putLong("exp", exp)
            apply()
        }
    }

    fun getExp(): Long? {
        return sharedPreferences.getLong("exp", -1).takeIf { it != (-1).toLong() }
    }

    fun saveUserIdTelegram(userIdTelegram: Long) {
        sharedPreferences.edit().apply {
            putLong("user_id_telegram", userIdTelegram)
            apply()
        }
    }

    fun getUserIdTelegram(): Long? {
        return sharedPreferences.getLong("user_id_telegram", -1).takeIf { it != (-1).toLong() }
    }

    fun saveTelegramId(telegramId: Long) {
        sharedPreferences.edit().apply {
            putLong("telegram_id", telegramId)
            apply()
        }
    }

    fun getTelegramId(): Long? {
        return sharedPreferences.getLong("telegram_id", -1).takeIf { it != (-1).toLong() }
    }

    fun saveCurrentEvent(telegramId: Long) {
        sharedPreferences.edit().apply {
            putLong("current_event_id", telegramId)
            apply()
        }
    }

    fun getCurrentEventId(): Long? {
        return sharedPreferences.getLong("current_event_id", -1).takeIf { it != (-1).toLong() }
    }

    fun saveCurrentScheduleId(telegramId: Long) {
        sharedPreferences.edit().apply {
            putLong("current_schedule_id", telegramId)
            apply()
        }
    }

    fun getCurrentScheduleId(): Long? {
        return sharedPreferences.getLong("current_schedule_id", -1).takeIf { it != (-1).toLong() }
    }

    fun saveEventName(name: String) {
        sharedPreferences.edit().apply {
            putString("event_name", name)
            apply()
        }
    }

    fun getEventName(): String? {
        return sharedPreferences.getString("event_name", null)
    }

    fun clearTokens() {
        sharedPreferences.edit().apply {
            remove("access_token")
            remove("refresh_token")
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }
}