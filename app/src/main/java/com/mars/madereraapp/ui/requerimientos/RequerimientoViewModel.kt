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
import com.mars.madereraapp.data.repository.IngresoRepository
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
    private val ingresoRepository: IngresoRepository,
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
    val filtroProveedor = MutableStateFlow("TODOS")
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

    private data class FiltrosReq(
        val estado: String,
        val mina: String,
        val prov: String,
        val mes: String,
        val query: String
    )

    // Filtered list to display in Requerimientos Tab (excludes hidden)
    val requerimientosFiltrados: StateFlow<List<RequerimientoEntity>> = combine(
        repository.visibleRequerimientos,
        combine(filtroEstado, filtroMina, filtroProveedor, filtroMes, searchQuery) { estado, mina, prov, mes, query ->
            FiltrosReq(estado, mina, prov, mes, query)
        }
    ) { list, filtros ->
        val supervisor = filtroSupervisor.value
        val anio = filtroAnio.value
        list.filter { req ->
            val matchesEstado = if (filtros.estado == "TODOS") true else req.estado == filtros.estado
            val matchesMina = if (filtros.mina == "TODAS") true else req.minaNombre == filtros.mina
            val matchesSupervisor = if (supervisor == "TODOS") true else req.supervisorNombre == supervisor
            val matchesProveedor = if (filtros.prov == "TODOS") true else req.proveedores?.contains(filtros.prov, ignoreCase = true) == true
            val matchesMes = if (filtros.mes.isBlank()) true else req.fecha.length >= 7 && req.fecha.substring(5, 7) == filtros.mes
            val matchesAnio = if (anio.isBlank()) true else req.fecha.startsWith(anio)
            
            val q = filtros.query.lowercase()
            val matchesQuery = if (q.isBlank()) true else {
                (req.codigo_req?.lowercase()?.contains(q) == true)
            }

            matchesEstado && matchesMina && matchesSupervisor && matchesProveedor && matchesMes && matchesAnio && matchesQuery
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

    // New metrics for Dashboard
    val totalArticulos: StateFlow<Int> = catalogRepository.getArticulos()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val itemsPorEntregar: StateFlow<Int> = ingresoRepository.pendientes
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val requerimientosConPendiente: StateFlow<Int> = ingresoRepository.pendientes
        .map { list -> list.map { it.codigo_req }.distinct().size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalProveedorPendiente: StateFlow<Double> = repository.requerimientos
        .map { list ->
            list.filter { it.estado == "PENDIENTE" || it.estado == "PARCIAL" }
                .sumOf { it.total_proveedor }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalMinaPendiente: StateFlow<Double> = repository.requerimientos
        .map { list ->
            list.filter { it.estado == "PENDIENTE" || it.estado == "PARCIAL" }
                .sumOf { it.total_mina }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val ultimosRequerimientos: StateFlow<List<RequerimientoEntity>> = repository.requerimientos
        .map { it.take(5) }
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
