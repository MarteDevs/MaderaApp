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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequerimientoViewModel @Inject constructor(
    private val repository: RequerimientoRepository,
    private val catalogRepository: CatalogRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val requerimientos: StateFlow<List<RequerimientoEntity>> = repository.requerimientos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
