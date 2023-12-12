package com.devapps.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devapps.storyapp.data.Resource
import com.devapps.storyapp.data.StoryRepository
import com.devapps.storyapp.data.api.ApiConfig
import com.devapps.storyapp.data.request.LoginRequest
import com.devapps.storyapp.data.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<String>>()
    val loginResult: LiveData<Resource<String>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.postValue(Resource.Loading())
            try {
                val response: Response<LoginResponse> =
                    ApiConfig.getApiClient().login(LoginRequest(email, password))
                handleLoginResponse(response)
            } catch (e: Exception) {
                Log.e(LoginViewModel::class.java.simpleName, "Exception during login: $e")
                _loginResult.postValue(Resource.Error("An error occurred"))
            }
        }
    }

    private suspend fun handleLoginResponse(response: Response<LoginResponse>) {
        try {
            if (response.isSuccessful) {
                val result = response.body()?.loginResult?.token
                result?.let { repository.getUserPreferences().saveSession(it) }
                _loginResult.postValue(Resource.Success(result))
            } else {
                val errorResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    LoginResponse::class.java
                )
                _loginResult.postValue(Resource.Error(errorResponse?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Log.e(LoginViewModel::class.java.simpleName, "Exception during login: $e")
            _loginResult.postValue(Resource.Error("An error occurred"))
        }
    }
}
