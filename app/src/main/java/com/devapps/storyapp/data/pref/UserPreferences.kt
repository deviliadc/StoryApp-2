package com.devapps.storyapp.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        const val TOKEN_KEY = "token"

        fun getInstance(
            dataStore: DataStore<Preferences>
        ): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getSession(): Flow<String> {
        val tokenKey = stringPreferencesKey(TOKEN_KEY)
        return dataStore.data.map { preferences ->
            preferences[tokenKey] ?: ""
        }
    }

    suspend fun saveSession(token: String) {
        val tokenKey = stringPreferencesKey(TOKEN_KEY)
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun deleteSession() {
        val tokenKey = stringPreferencesKey(TOKEN_KEY)
        dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }
}
