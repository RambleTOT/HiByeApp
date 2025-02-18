package ramble.sokol.hibyeapp.managers

import android.content.Context

class NameAndPhotoManager(context: Context) {

    companion object{
        private const val PREF_NAME = "PREF_NAME"
        private const val NAME = "NAME"
        private const val PREF_PHOTO = "PREF_PHOTO"
        private const val PHOTO = "PHOTO"
    }

    private var sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveName(entry: String){
        val editor = sPref.edit()
        editor.putString(NAME, entry)
        editor.apply()
    }

    fun getName() : String? {
        return sPref.getString(NAME, null)
    }

    private var sPrefProfile = context.getSharedPreferences(PREF_PHOTO, Context.MODE_PRIVATE)

    fun savePhoto(entry: String){
        val editor = sPrefProfile.edit()
        editor.putString(PHOTO, entry)
        editor.apply()
    }

    fun getPhoto() : String? {
        return sPrefProfile.getString(PHOTO, null)
    }

}