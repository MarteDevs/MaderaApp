package com.mars.madereraapp.ui.analisis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mars.madereraapp.data.repository.IngresoRepository
import com.mars.madereraapp.data.repository.RequerimientoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnalisisViewModel @Inject constructor(
    private val requerimientoRepo: RequerimientoRepository,
    private val ingresoRepo: IngresoRepository
) : ViewModel() {

    private val _filtroMes = MutableStateFlow("")
    val filtroMes = _filtroMes.asStateFlow()

    private val _filtroAnio = MutableStateFlow("")
    val filtroAnio = _filtroAnio.asStateFlow()

    private val _filtroDestino = MutableStateFlow("TODOS")
    val filtroDestino = _filtroDestino.asStateFlow()

    fun updateFiltroMes(mes: String) {
        _filtroMes.value = mes
    }

    fun updateFiltroAnio(anio: String) {
        _filtroAnio.value = anio
    }

    fun updateFiltroDestino(destino: String) {
        _filtroDestino.value = destino
    }

    val aniosDisponibles: StateFlow<List<String>> = combine(
        requerimientoRepo.requerimientos.map { list -> list.mapNotNull { it.fecha }.filter { it.length >= 4 }.map { it.take(4) } },
        ingresoRepo.ingresos.map { list -> list.mapNotNull { it.fecha }.filter { it.length >= 4 }.map { it.take(4) } }
    ) { reqAnios, ingAnios ->
        (reqAnios + ingAnios).distinct().sortedDescending()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalProveedorReq = combine(
        requerimientoRepo.visibleRequerimientos, _filtroMes, _filtroAnio, _filtroDestino
    ) { reqs, mes, anio, destino ->
        reqs.filter { req ->
            val matchesMes = if (mes.isBlank()) true else req.fecha.length >= 7 && req.fecha.substring(5, 7) == mes
            val matchesAnio = if (anio.isBlank()) true else req.fecha.startsWith(anio)
            val matchesDestino = if (destino == "TODOS") true else req.tipoPago == destino
            matchesMes && matchesAnio && matchesDestino
        }.sumOf { it.total_proveedor }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalMinaReq = combine(
        requerimientoRepo.visibleRequerimientos, _filtroMes, _filtroAnio, _filtroDestino
    ) { reqs, mes, anio, destino ->
        reqs.filter { req ->
            val matchesMes = if (mes.isBlank()) true else req.fecha.length >= 7 && req.fecha.substring(5, 7) == mes
            val matchesAnio = if (anio.isBlank()) true else req.fecha.startsWith(anio)
            val matchesDestino = if (destino == "TODOS") true else req.tipoPago == destino
            matchesMes && matchesAnio && matchesDestino
        }.sumOf { it.total_mina }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalProveedorIngreso = combine(
        ingresoRepo.ingresos, _filtroMes, _filtroAnio, _filtroDestino
    ) { ingresos, mes, anio, destino ->
        ingresos.filter { ing ->
            val matchesMes = if (mes.isBlank()) true else ing.fecha.length >= 7 && ing.fecha.substring(5, 7) == mes
            val matchesAnio = if (anio.isBlank()) true else ing.fecha.startsWith(anio)
            val matchesDestino = if (destino == "TODOS") true else ing.tipoPago == destino
            matchesMes && matchesAnio && matchesDestino
        }.sumOf { it.total_proveedor }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalMinaIngreso = combine(
        ingresoRepo.ingresos, _filtroMes, _filtroAnio, _filtroDestino
    ) { ingresos, mes, anio, destino ->
        ingresos.filter { ing ->
            val matchesMes = if (mes.isBlank()) true else ing.fecha.length >= 7 && ing.fecha.substring(5, 7) == mes
            val matchesAnio = if (anio.isBlank()) true else ing.fecha.startsWith(anio)
            val matchesDestino = if (destino == "TODOS") true else ing.tipoPago == destino
            matchesMes && matchesAnio && matchesDestino
        }.sumOf { it.total_mina }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}
