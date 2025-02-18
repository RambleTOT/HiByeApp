package ramble.sokol.hibyeapp.managers

import android.content.Context

class ProfileAndCodeManager(context: Context) {

    companion object{
        private const val PREF_REGISTR = "PREF_REGISTR"
        private const val REGISTR = "REGISTR"
        private const val PREF_PROFILE_ENTRY = "PREF_PROFILE_ENTRY"
        private const val PROFILE_ENTRY = "PROFILE_ENTRY"
        private const val PREF_CODE_ENTRY = "PREF_CODE_ENTRY"
        private const val CODE_ENTRY = "CODE_ENTRY"
    }

    private var sPref = context.getSharedPreferences(PREF_REGISTR, Context.MODE_PRIVATE)

    fun saveRegistr(entry: Boolean){
        val editor = sPref.edit()
        editor.putBoolean(REGISTR, entry)
        editor.apply()
    }

    fun getRegistr() : Boolean? {
        return sPref.getBoolean(REGISTR, false)
    }

    private var sPrefProfile = context.getSharedPreferences(PREF_PROFILE_ENTRY, Context.MODE_PRIVATE)

    fun saveProfile(entry: Boolean){
        val editor = sPrefProfile.edit()
        editor.putBoolean(PROFILE_ENTRY, entry)
        editor.apply()
    }

    fun getProfile() : Boolean? {
        return sPrefProfile.getBoolean(PROFILE_ENTRY, false)
    }

}