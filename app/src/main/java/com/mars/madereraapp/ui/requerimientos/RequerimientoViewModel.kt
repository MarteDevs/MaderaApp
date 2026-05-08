package com.mars.madereraapp.ui.requerimientos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mars.madereraapp.data.local.entities.RequerimientoDetalleEntity
import com.mars.madereraapp.data.local.entities.RequerimientoEntity
import com.mars.madereraapp.data.repository.CatalogRepository
import com.mars.madereraapp.data.repository.RequerimientoRepository
import com.mars.madereraapp.data.sync.UploadRequerimientoWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequerimientoViewModel @Inject constructor(
    private val repository: RequerimientoRepository,
    private val catalogRepository: CatalogRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Raw list directly from DB
    val requerimientosRaw: StateFlow<List<RequerimientoEntity>> = repository.requerimientos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter states
    val filtroEstado = MutableStateFlow("TODOS")
    val filtroMina = MutableStateFlow("TODAS")
    val filtroFecha = MutableStateFlow<String?>(null) // Format: "dd-MM-yyyy" or similar depending on DB

    // Filtered list to display in Requerimientos Tab
    val requerimientosFiltrados: StateFlow<List<RequerimientoEntity>> = combine(
        requerimientosRaw,
        filtroEstado,
        filtroMina,
        filtroFecha
    ) { list, estado, mina, fecha ->
        list.filter { req ->
            val matchesEstado = if (estado == "TODOS") true else req.estado == estado
            val matchesMina = if (mina == "TODAS") true else req.minaNombre == mina
            val matchesFecha = if (fecha == null) true else req.fecha.contains(fecha)
            matchesEstado && matchesMina && matchesFecha
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Metrics for Dashboard (based on raw list)
    val totalRequerimientos = MutableStateFlow(0)
    val pendientes = MutableStateFlow(0)
    val parciales = MutableStateFlow(0)
    val completados = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            requerimientosRaw.collect { list ->
                totalRequerimientos.value = list.size
                pendientes.value = list.count { it.estado == "PENDIENTE" }
                parciales.value = list.count { it.estado == "PARCIAL" }
                completados.value = list.count { it.estado == "COMPLETADO" }
            }
        }
    }

    val minas = catalogRepository.getMinas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val articulos = catalogRepository.getArticulos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val proveedores = catalogRepository.getProveedores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supervisores = catalogRepository.getSupervisores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refresh() {
        viewModelScope.launch {
            repository.fetchHistorial()
        }
    }

    // Still kept for compatibility if needed elsewhere, though removed from UI
    fun crearRequerimiento(
        fecha: String,
        minaId: Int,
        minaNombre: String,
        supervisorId: Int?,
        supervisorNombre: String?,
        detalles: List<RequerimientoDetalleEntity>
    ) {
        viewModelScope.launch {
            val localId = repository.guardarRequerimientoLocal(
                fecha, minaId, minaNombre, supervisorId, supervisorNombre, detalles
            )
            // Programar subida
            val uploadRequest = OneTimeWorkRequestBuilder<UploadRequerimientoWorker>()
                .setInputData(Data.Builder().putLong("localId", localId).build())
                .build()
            WorkManager.getInstance(context).enqueue(uploadRequest)
        }
    }
}
