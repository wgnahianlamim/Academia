package com.agon.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "academia_prefs")

class DataStoreManager(private val context: Context) {
    private val PROFILE_KEY = stringPreferencesKey("user_profile")
    private val CGPA_KEY = stringPreferencesKey("cgpa_data")

    val userProfileFlow: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        val jsonString = prefs[PROFILE_KEY]
        if (jsonString != null) {
            try {
                Json.decodeFromString(jsonString)
            } catch (e: Exception) {
                UserProfile()
            }
        } else {
            UserProfile()
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[PROFILE_KEY] = Json.encodeToString(profile)
        }
    }

    val cgpaDataFlow: Flow<CgpaData> = context.dataStore.data.map { prefs ->
        val jsonString = prefs[CGPA_KEY]
        if (jsonString != null) {
            try {
                Json.decodeFromString(jsonString)
            } catch (e: Exception) {
                CgpaData()
            }
        } else {
            CgpaData()
        }
    }

    suspend fun saveCgpaData(data: CgpaData) {
        context.dataStore.edit { prefs ->
            prefs[CGPA_KEY] = Json.encodeToString(data)
        }
    }
}
