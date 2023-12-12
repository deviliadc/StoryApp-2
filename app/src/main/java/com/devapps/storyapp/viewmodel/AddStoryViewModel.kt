package com.devapps.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devapps.storyapp.data.Resource
import com.devapps.storyapp.data.StoryRepository
import com.devapps.storyapp.data.api.ApiConfig
import com.devapps.storyapp.data.response.AppResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _uploadInfo = MutableLiveData<Resource<String>>()
    val uploadInfo: LiveData<Resource<String>> = _uploadInfo

    suspend fun uploadStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        _uploadInfo.postValue(Resource.Loading())
        try {
            val response: Response<AppResponse> = ApiConfig.getApiClient().addStory(
                token = "Bearer ${repository.getUserPreferences().getSession().first()}",
                imageMultipart,
                description,
                lat,
                lon
            )
            handleUploadResponse(response)
        } catch (e: Exception) {
            Log.e(AddStoryViewModel::class.java.simpleName, "Exception during uploadStory: $e")
            _uploadInfo.postValue(Resource.Error("An error occurred"))
        }
    }

    private fun handleUploadResponse(response: Response<AppResponse>) {
        try {
            if (response.isSuccessful) {
                _uploadInfo.postValue(Resource.Success(response.body()?.message))
            } else {
                val errorResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    AppResponse::class.java
                )
                _uploadInfo.postValue(Resource.Error(errorResponse.message))
            }
        } catch (e: Exception) {
            Log.e(AddStoryViewModel::class.java.simpleName, "Exception during handleUploadResponse: $e")
            _uploadInfo.postValue(Resource.Error("An error occurred"))
        }
    }
}