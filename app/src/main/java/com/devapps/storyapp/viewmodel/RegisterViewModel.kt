package com.devapps.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devapps.storyapp.data.Resource
import com.devapps.storyapp.data.StoryRepository
import com.devapps.storyapp.data.api.ApiConfig
import com.devapps.storyapp.data.request.RegisterRequest
import com.devapps.storyapp.data.response.AppResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel (private val repository: StoryRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Resource<String>>()
    val registerResult: LiveData<Resource<String>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.postValue(Resource.Loading())
            try {
                val response: Response<AppResponse> =
                    ApiConfig.getApiClient().register(RegisterRequest(name, email, password))
                handleRegisterResponse(response)
            } catch (e: Exception) {
                Log.e(RegisterViewModel::class.java.simpleName, "Exception during register: $e")
                _registerResult.postValue(Resource.Error("An error occurred"))
            }
        }
    }

    private fun handleRegisterResponse(response: Response<AppResponse>) {
        try {
            if (response.isSuccessful) {
                val message = response.body()?.message.toString()
                _registerResult.postValue(Resource.Success(message))
            } else {
                val errorResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    AppResponse::class.java
                )
                _registerResult.postValue(Resource.Error(errorResponse?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Log.e(RegisterViewModel::class.java.simpleName, "Exception during handleRegisterResponse: $e")
            _registerResult.postValue(Resource.Error("An error occurred"))
        }
    }
}
