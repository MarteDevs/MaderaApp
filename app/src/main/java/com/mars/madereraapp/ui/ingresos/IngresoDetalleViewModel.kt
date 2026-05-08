package com.mars.madereraapp.ui.ingresos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mars.madereraapp.data.remote.IngresoApiService
import com.mars.madereraapp.data.remote.IngresoDetalleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngresoDetalleViewModel @Inject constructor(
    private val apiService: IngresoApiService
) : ViewModel() {

    private val _detalles = MutableStateFlow<List<IngresoDetalleItem>>(emptyList())
    val detalles: StateFlow<List<IngresoDetalleItem>> = _detalles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _detalles.value = apiService.getDetalle(id)
            } catch (e: Exception) {
                _error.value = "Error al cargar detalle: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
