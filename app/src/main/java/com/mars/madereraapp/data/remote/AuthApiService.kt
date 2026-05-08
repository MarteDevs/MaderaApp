package com.mars.madereraapp.data.remote

import com.mars.madereraapp.data.remote.model.LoginRequest
import com.mars.madereraapp.data.remote.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
