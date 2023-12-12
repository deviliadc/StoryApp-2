package com.devapps.storyapp.data

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.devapps.storyapp.data.api.ApiService
import com.devapps.storyapp.data.model.Story
import com.devapps.storyapp.data.pref.UserPreferences
import com.devapps.storyapp.data.response.StoryResponse
import kotlinx.coroutines.flow.first
import retrofit2.Response

class StoryRepository (
    private val apiService: ApiService,
    private val pref: UserPreferences
) {

    fun getUserPreferences(): UserPreferences {
        return pref
    }

    fun getAllStory(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(token, apiService) }
        ).liveData
    }

    fun getMaps(): LiveData<Resource<StoryResponse>> {
        return performApiCall {
            apiService.getStories(
                token = "Bearer ${pref.getSession().first()}",
                page = 1,
                size = 100,
                location = 1
            )
        }
    }

    private fun <T> performApiCall(apiCall: suspend () -> Response<T>): LiveData<Resource<T>> = liveData {
        emit(Resource.Loading())
        runCatching {
            val response = apiCall.invoke()
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        }.onFailure { e ->
            emit(Resource.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            pref: UserPreferences
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, pref)
            }.also { instance = it }
    }
}
