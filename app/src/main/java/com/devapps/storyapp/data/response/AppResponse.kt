package com.devapps.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class AppResponse(

    @SerializedName("error")
    val error: Boolean?,

    @SerializedName("message")
    val message: String?,
)