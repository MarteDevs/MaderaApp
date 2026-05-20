package com.mars.madereraapp.ui.ingresos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mars.madereraapp.data.local.entities.IngresoDetalleEntity
import com.mars.madereraapp.data.local.entities.IngresoEntity
import com.mars.madereraapp.data.local.entities.RequerimientoPendienteEntity
import com.mars.madereraapp.data.repository.IngresoRepository
import com.mars.madereraapp.data.sync.UploadIngresoWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngresoViewModel @Inject constructor(
    private val repository: IngresoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val ingresos: StateFlow<List<IngresoEntity>> = combine(repository.ingresos, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            val q = query.lowercase()
            list.filter {
                (it.viaje?.lowercase()?.contains(q) == true) ||
                (it.vale?.lowercase()?.contains(q) == true) ||
                (it.minas?.lowercase()?.contains(q) == true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val pendientes: StateFlow<List<RequerimientoPendienteEntity>> = repository.pendientes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshHistorial()
            repository.refreshPendientes()
        }
    }

    fun registrarIngreso(
        fecha: String,
        viaje: String?,
        vale: String?,
        observacion: String?,
        detalles: List<IngresoDetalleEntity>
    ) {
        viewModelScope.launch {
            val localId = repository.guardarIngresoLocal(
                fecha, viaje, vale, observacion, detalles
            )
            // Programar subida
            val uploadRequest = OneTimeWorkRequestBuilder<UploadIngresoWorker>()
                .setInputData(Data.Builder().putLong("localId", localId).build())
                .build()
            WorkManager.getInstance(context).enqueue(uploadRequest)
        }
    }
}
