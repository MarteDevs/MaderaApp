package com.mars.madereraapp.ui.requerimientos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mars.madereraapp.data.remote.RequerimientoApiService
import com.mars.madereraapp.data.remote.RequerimientoDetalleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequerimientoDetalleViewModel @Inject constructor(
    private val apiService: RequerimientoApiService
) : ViewModel() {

    private val _detalles = MutableStateFlow<List<RequerimientoDetalleItem>>(emptyList())
    val detalles: StateFlow<List<RequerimientoDetalleItem>> = _detalles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _detalles.value = apiService.getDetalles(id)
            } catch (e: Exception) {
                _error.value = "Error al cargar detalles: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _cierreSuccess = MutableStateFlow(false)
    val cierreSuccess: StateFlow<Boolean> = _cierreSuccess

    fun forzarCierre(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.forzarCierre(id)
                if (response.success) {
                    _cierreSuccess.value = true
                } else {
                    _error.value = response.mensaje ?: "Error al forzar cierre"
                }
            } catch (e: Exception) {
                _error.value = "Error al forzar cierre: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
