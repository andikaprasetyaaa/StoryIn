package com.example.storyin.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null
        private val TOKEN_KEY = stringPreferencesKey("user_token")
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

        fun getInstance(context: Context): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}