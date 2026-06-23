package com.mars.madereraapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private val _token = MutableStateFlow<String?>(null)
    val token: Flow<String?> = _token.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: Flow<String?> = _userName.asStateFlow()

    suspend fun saveSession(token: String, userName: String) {
        _token.value = token
        _userName.value = userName
    }

    suspend fun clearSession() {
        _token.value = null
        _userName.value = null
    }
}
