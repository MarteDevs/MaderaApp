package com.mars.madereraapp.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Patrón load() → isLoading/error compartido por las pantallas de detalle
 * (Ingreso, Requerimiento): pedir una lista de items por id y exponerla como
 * StateFlow. Cada ViewModel solo implementa cómo pedir sus propios datos.
 */
abstract class BaseDetalleViewModel<T> : ViewModel() {

    protected val _detalles = MutableStateFlow<List<T>>(emptyList())
    val detalles: StateFlow<List<T>> = _detalles

    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    protected val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Nombre de la entidad para el mensaje de error, ej. "detalle" o "detalles". */
    protected open val nombreError: String = "detalle"

    protected abstract suspend fun fetchDetalles(id: Int): List<T>

    fun load(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _detalles.value = fetchDetalles(id)
            } catch (e: Exception) {
                _error.value = "Error al cargar $nombreError: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
