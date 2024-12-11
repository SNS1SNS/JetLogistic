package com.example.jet.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String,
)

data class LoginResponse(
    val token: String,
    val message: String
)

data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String
)
data class RegisterResponse(
    val message: String,
    val user: User
)

data class User(
    val first_name: String?,
    val last_name: String?,
    val email: String?,
    val balance: Int?,
    val position_name: String?
)


interface ApiService {
    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
    @POST("/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
    @GET("/users/profile")
    fun getCurrentUser(@Header("Authorization") authHeader: String): Call<User>
    @GET("/users/all")
    fun getAllUser(@Header("Authorization") authHeader: String): Call<List<User>>


}
