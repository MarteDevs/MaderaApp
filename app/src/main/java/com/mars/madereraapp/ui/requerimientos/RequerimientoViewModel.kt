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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequerimientoViewModel @Inject constructor(
    private val repository: RequerimientoRepository,
    private val catalogRepository: CatalogRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Raw list directly from DB (all, including hidden)
    val requerimientosRaw: StateFlow<List<RequerimientoEntity>> = repository.requerimientos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Hidden count from DB
    val hiddenCount: StateFlow<Int> = repository.hiddenCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filter states
    val filtroEstado = MutableStateFlow("TODOS")
    val filtroMina = MutableStateFlow("TODAS")
    val filtroSupervisor = MutableStateFlow("TODOS")
    val filtroMes = MutableStateFlow("")   // "", "01", "02", ..., "12"
    val filtroAnio = MutableStateFlow("")  // "", "2024", "2025", ...

    val searchQuery = MutableStateFlow("")

    // Años disponibles extraídos dinámicamente del historial
    val aniosDisponibles: StateFlow<List<String>> = repository.requerimientos.map { list ->
        list.map { it.fecha.take(4) }
            .filter { it.length == 4 }
            .distinct()
            .sortedDescending()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered list to display in Requerimientos Tab (excludes hidden)
    val requerimientosFiltrados: StateFlow<List<RequerimientoEntity>> = combine(
        repository.visibleRequerimientos,
        filtroEstado,
        filtroMina,
        filtroMes,
        searchQuery
    ) { list, estado, mina, mes, query ->
        val supervisor = filtroSupervisor.value
        val anio = filtroAnio.value
        list.filter { req ->
            val matchesEstado = if (estado == "TODOS") true else req.estado == estado
            val matchesMina = if (mina == "TODAS") true else req.minaNombre == mina
            val matchesSupervisor = if (supervisor == "TODOS") true else req.supervisorNombre == supervisor
            val matchesMes = if (mes.isBlank()) true else req.fecha.length >= 7 && req.fecha.substring(5, 7) == mes
            val matchesAnio = if (anio.isBlank()) true else req.fecha.startsWith(anio)
            
            val q = query.lowercase()
            val matchesQuery = if (q.isBlank()) true else {
                (req.codigo_req?.lowercase()?.contains(q) == true)
            }

            matchesEstado && matchesMina && matchesSupervisor && matchesMes && matchesAnio && matchesQuery
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
        refresh()
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
            try {
                catalogRepository.syncCatalogs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            repository.fetchHistorial()
        }
    }

    /** Ocultar un requerimiento completado (persistente en Room) */
    fun hideRequerimiento(localId: Long) {
        viewModelScope.launch {
            repository.hideRequerimiento(localId)
        }
    }

    /** Mostrar todos los requerimientos ocultos */
    fun unhideAll() {
        viewModelScope.launch {
            repository.unhideAll()
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
