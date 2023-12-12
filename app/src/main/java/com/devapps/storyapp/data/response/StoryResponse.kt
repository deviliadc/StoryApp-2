package com.devapps.storyapp.data.response

import com.devapps.storyapp.data.model.Story
import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @SerializedName("error")
    val error: Boolean?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("listStory")
    val listStory: List<Story>
)
