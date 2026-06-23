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
import kotlin.OptIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OrdenIngreso { POR_FECHA, POR_VALE }

@HiltViewModel
class IngresoViewModel @Inject constructor(
    private val repository: IngresoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _filtroMina = MutableStateFlow("")
    val filtroMina = _filtroMina.asStateFlow()

    private val _filtroViaje = MutableStateFlow("")
    val filtroViaje = _filtroViaje.asStateFlow()

    private val _filtroVale = MutableStateFlow("")
    val filtroVale = _filtroVale.asStateFlow()

    private val _filtroMes = MutableStateFlow("")   // "", "01", "02", ..., "12"
    val filtroMes = _filtroMes.asStateFlow()

    private val _filtroAnio = MutableStateFlow("")  // "", "2024", "2025", ...
    val filtroAnio = _filtroAnio.asStateFlow()

    private val _ordenActual = MutableStateFlow(OrdenIngreso.POR_FECHA)
    val ordenActual = _ordenActual.asStateFlow()

    val minasDisponibles: StateFlow<List<String>> = repository.ingresos.map { list ->
        list.mapNotNull { it.minas }
            .flatMap { it.split(",") }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val viajesDisponibles: StateFlow<List<String>> = repository.ingresos.map { list ->
        list.mapNotNull { it.viaje }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aniosDisponibles: StateFlow<List<String>> = repository.ingresos.map { list ->
        list.map { it.fecha.take(4) }
            .filter { it.length == 4 }
            .distinct()
            .sortedDescending()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val ingresos: StateFlow<List<IngresoEntity>> = combine(
        _ordenActual, _filtroMes, _filtroAnio
    ) { orden, mes, anio -> Triple(orden, mes, anio) }
        .flatMapLatest { (orden, mes, anio) ->
            val sourceFlow = if (orden == OrdenIngreso.POR_VALE) repository.ingresosByVale else repository.ingresos
            combine(
                sourceFlow,
                _filtroMina,
                _filtroViaje,
                _filtroVale
            ) { list, mina, viaje, vale ->
                var filtrado = list
                if (mina.isNotBlank()) {
                    filtrado = filtrado.filter { it.minas?.contains(mina, ignoreCase = true) == true }
                }
                if (viaje.isNotBlank()) {
                    filtrado = filtrado.filter { it.viaje?.equals(viaje, ignoreCase = true) == true }
                }
                if (vale.isNotBlank()) {
                    filtrado = filtrado.filter { it.vale?.contains(vale, ignoreCase = true) == true }
                }
                if (mes.isNotBlank()) {
                    filtrado = filtrado.filter { it.fecha.length >= 7 && it.fecha.substring(5, 7) == mes }
                }
                if (anio.isNotBlank()) {
                    filtrado = filtrado.filter { it.fecha.startsWith(anio) }
                }
                filtrado
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateFiltroMina(query: String) {
        _filtroMina.value = query
    }

    fun updateFiltroViaje(query: String) {
        _filtroViaje.value = query
    }

    fun updateFiltroVale(query: String) {
        _filtroVale.value = query
    }

    fun updateFiltroMes(mes: String) {
        _filtroMes.value = mes
    }

    fun updateFiltroAnio(anio: String) {
        _filtroAnio.value = anio
    }

    fun toggleOrden() {
        _ordenActual.value = if (_ordenActual.value == OrdenIngreso.POR_FECHA) OrdenIngreso.POR_VALE else OrdenIngreso.POR_FECHA
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
