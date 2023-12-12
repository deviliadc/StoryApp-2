package com.devapps.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.devapps.storyapp.data.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStoriesLocation() = repository.getMaps()
}
