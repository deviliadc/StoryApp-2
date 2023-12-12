package com.devapps.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.devapps.storyapp.data.Resource
import com.devapps.storyapp.data.StoryRepository
import com.devapps.storyapp.data.model.Story
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    private val _logoutResult = MutableLiveData<Resource<Unit>>()
    val logoutResult: LiveData<Resource<Unit>> = _logoutResult

     fun getStories(token: String): LiveData<PagingData<Story>> {
        return repository.getAllStory(token).cachedIn(viewModelScope)
    }

     suspend fun logout() {
        viewModelScope.launch {
            deleteSession()
            _logoutResult.postValue(Resource.Success(Unit))
        }
    }

    val sessions = MutableLiveData<String>()

    fun getSession() = viewModelScope.launch {
        repository.getUserPreferences().getSession().collect{
            sessions.value = it
        }
    }

    private suspend fun deleteSession() {
        repository.getUserPreferences().deleteSession()
    }
}
