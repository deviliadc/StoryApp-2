package com.devapps.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.devapps.storyapp.data.StoryRepository
import com.devapps.storyapp.data.api.ApiConfig
import com.devapps.storyapp.data.pref.UserPreferences


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiClient()
        return StoryRepository.getInstance(apiService, pref)
    }
}
