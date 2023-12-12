package com.devapps.storyapp.data.api

import com.devapps.storyapp.data.request.LoginRequest
import com.devapps.storyapp.data.request.RegisterRequest
import com.devapps.storyapp.data.response.AppResponse
import com.devapps.storyapp.data.response.LoginResponse
import com.devapps.storyapp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService{

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AppResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): Response<StoryResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null,
    ): Response<AppResponse>
}

