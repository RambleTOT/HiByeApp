package ramble.sokol.hibyeapp.managers

import android.content.Context

class EmptyEventsManager(context: Context) {

    companion object{
        private const val PREF_EVENT = "PREF_EMPTY_EVENT"
        private const val EVENT = "EMPTY_EVENT"
        private const val PREF_LOGIN = "PREF_LOGIN"
        private const val LOGIN = "EMPTY_LOGIN"
        private const val PREF_IS_EVENT = "PREF_IS"
        private const val IS_EVENT = "EMPTY_IS"
    }

    private var sPref = context.getSharedPreferences(PREF_EVENT, Context.MODE_PRIVATE)

    fun saveEmptyEvent(entry: Boolean){
        val editor = sPref.edit()
        editor.putBoolean(EVENT, entry)
        editor.apply()
    }

    fun getEmptyEvent() : Boolean? {
        return sPref.getBoolean(EVENT, false)
    }

    private var sPrefLogin = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)

    fun saveLogin(entry: Boolean){
        val editor = sPrefLogin.edit()
        editor.putBoolean(LOGIN, entry)
        editor.apply()
    }

    fun getLogin() : Boolean? {
        return sPrefLogin.getBoolean(LOGIN, false)
    }

    private var sPrefIs = context.getSharedPreferences(PREF_IS_EVENT, Context.MODE_PRIVATE)

    fun saveIsEvent(entry: Boolean){
        val editor = sPrefIs.edit()
        editor.putBoolean(IS_EVENT, entry)
        editor.apply()
    }

    fun getIsEvent() : Boolean? {
        return sPrefIs.getBoolean(IS_EVENT, false)
    }

}