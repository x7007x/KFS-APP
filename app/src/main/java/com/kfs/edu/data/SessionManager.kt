package com.kfs.edu.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kfu_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_LOGGED_IN_PROFILE = stringPreferencesKey("logged_in_profile")
        private val KEY_NATIONAL_ID = stringPreferencesKey("national_id")
        private val KEY_LAST_SYNC = stringPreferencesKey("last_sync_time")

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }

    val savedProfileJson: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_LOGGED_IN_PROFILE]
    }

    val savedNationalId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_NATIONAL_ID]
    }

    val lastSyncTime: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_SYNC]
    }

    suspend fun saveSession(profileJson: String, nationalId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LOGGED_IN_PROFILE] = profileJson
            prefs[KEY_NATIONAL_ID] = nationalId
            prefs[KEY_LAST_SYNC] = java.time.LocalDateTime.now().toString()
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_LOGGED_IN_PROFILE)
            prefs.remove(KEY_NATIONAL_ID)
            prefs.remove(KEY_LAST_SYNC)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.first()[KEY_LOGGED_IN_PROFILE] != null
    }

    suspend fun getSavedProfile(): RealStudentProfile? {
        val json = context.dataStore.data.first()[KEY_LOGGED_IN_PROFILE] ?: return null
        return try {
            val obj = org.json.JSONObject(json)
            RealStudentProfile(
                nationalNumber = obj.optString("NATIONAL_NUMBER"),
                fullName = obj.optString("FULL_NAME_AR"),
                email = obj.optString("METHOD_DESCR"),
                notes = obj.optString("METHOD_NOTES"),
                studentCode = obj.optString("STUD_CODE"),
                passwordItec = obj.optString("PASSWORD_ITEC"),
                phaseParent = obj.optString("phase_parent"),
                gender = obj.optString("GENDER_DESCR_AR"),
                caseDescr = obj.optString("CASE_DESCR_AR"),
                religion = obj.optString("RELIGION_DESCR_AR"),
                methodDescr = obj.optString("METHOD_DESCR_AR"),
                natureDescr = obj.optString("NATURE_DESCR_AR"),
                enrollStatus = obj.optString("ED_CODE_STUD_ENROLL_AR"),
                programName = obj.optString("Program_Name"),
                asFacultyInfoId = obj.optDouble("AS_FACULTY_INFO_ID"),
                photo = obj.optString("STUD_PHOTO")
            )
        } catch (e: Exception) {
            null
        }
    }
}
