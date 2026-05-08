package com.mars.madereraapp.data.repository

import com.mars.madereraapp.data.SessionManager
import com.mars.madereraapp.data.remote.AuthApiService
import com.mars.madereraapp.data.remote.model.LoginRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val sessionManager: SessionManager
) {
    suspend fun login(usuario: String, clave: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(usuario, clave))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                sessionManager.saveSession(loginResponse.token, loginResponse.user.nombre)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuario o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
