package com.mars.madereraapp.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mars.madereraapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
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
                _loginSuccess.emit(Unit)
            } else {
                error = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
            cargando = false
        }
    }
}
