package es.ejemplo.android.mybooklist.general

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var readingGoal: Int
        get() = prefs.getInt("reading_goal", 12)
        set(value) = prefs.edit().putInt("reading_goal", value).apply()

    var profileImageUri: String?
        get() = prefs.getString("profile_image_uri", null)
        set(value) = prefs.edit().putString("profile_image_uri", value).apply()

    var userName: String
        get() = prefs.getString("user_name", "Lector") ?: "Lector"
        set(value) = prefs.edit().putString("user_name", value).apply()
}
