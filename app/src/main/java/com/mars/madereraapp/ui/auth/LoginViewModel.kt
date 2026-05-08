package com.mars.madereraapp.ui.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mars.madereraapp.data.repository.AuthRepository
import com.mars.madereraapp.data.sync.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var usuario by mutableStateOf("")
    var clave by mutableStateOf("")
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    private val _loginSuccess = MutableSharedFlow<Unit>()
    val loginSuccess = _loginSuccess.asSharedFlow()

    fun onLoginClick() {
        if (usuario.isBlank() || clave.isBlank()) {
            error = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            cargando = true
            error = null
            val result = repository.login(usuario, clave)
            if (result.isSuccess) {
                // Iniciar sincronización de catálogos al iniciar sesión
                triggerSync()
                _loginSuccess.emit(Unit)
            } else {
                error = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
            cargando = false
        }
    }

    private fun triggerSync() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}
