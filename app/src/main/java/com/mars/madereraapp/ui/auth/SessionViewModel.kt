package com.mars.madereraapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mars.madereraapp.data.SessionManager
import com.mars.madereraapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    // null = loading, "" = no session, "token" = active session
    val sessionToken: StateFlow<String?> = sessionManager.token
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isLoggedIn: StateFlow<Boolean?> = sessionManager.token
        .map { token -> if (token != null) token.isNotEmpty() else false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
