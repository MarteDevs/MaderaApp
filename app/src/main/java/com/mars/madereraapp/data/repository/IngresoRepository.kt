package com.mars.madereraapp.data.repository

import com.mars.madereraapp.data.local.dao.IngresoDao
import com.mars.madereraapp.data.local.entities.IngresoDetalleEntity
import com.mars.madereraapp.data.local.entities.IngresoEntity
import com.mars.madereraapp.data.local.entities.RequerimientoPendienteEntity
import com.mars.madereraapp.data.remote.IngresoApiService
import com.mars.madereraapp.data.remote.IngresoDetalleRequest
import com.mars.madereraapp.data.remote.IngresoRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngresoRepository @Inject constructor(
    private val apiService: IngresoApiService,
    private val dao: IngresoDao
) {
    val ingresos: Flow<List<IngresoEntity>> = dao.getAllIngresos()
    val pendientes: Flow<List<RequerimientoPendienteEntity>> = dao.getRequerimientosPendientes()

    suspend fun refreshPendientes() {
        try {
            val remotePendientes = apiService.getPendientes()
            dao.clearRequerimientosPendientes()
            dao.insertRequerimientosPendientes(remotePendientes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun refreshHistorial() {
        try {
            val remoteHistorial = apiService.getHistorial()
            dao.clearSyncedIngresos()
            remoteHistorial.forEach { item ->
                val entity = IngresoEntity(
                    serverId = item.id,
                    codigo_ingreso = item.codigo_ingreso,
                    fecha = item.fecha,
                    viaje = item.viaje,
                    vale = item.vale,
                    observacion = item.observacion,
                    isPendingSync = false
                )
                dao.insertIngreso(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun guardarIngresoLocal(
        fecha: String,
        viaje: String?,
        vale: String?,
        observacion: String?,
        detalles: List<IngresoDetalleEntity>
    ): Long {
        val ingreso = IngresoEntity(
            fecha = fecha,
            viaje = viaje,
            vale = vale,
            observacion = observacion,
            isPendingSync = true
        )
        val localId = dao.insertIngreso(ingreso)
        val detallesWithId = detalles.map { it.copy(localIngresoId = localId) }
        dao.insertDetalles(detallesWithId)
        return localId
    }

    suspend fun syncIngreso(localId: Long): Boolean {
        val ingreso = dao.getPendingSyncIngresos().find { it.localId == localId } ?: return false
        val detalles = dao.getDetallesForIngreso(localId)

        val request = IngresoRequest(
            fecha = ingreso.fecha,
            viaje = ingreso.viaje,
            vale = ingreso.vale,
            observacion = ingreso.observacion,
            detalles = detalles.map {
                IngresoDetalleRequest(
                    requerimiento_detalle_id = it.requerimiento_detalle_id,
                    cantidad_entregada = it.cantidad_entregada
                )
            }
        )

        return try {
            val response = apiService.crear(request)
            dao.markAsSynced(localId, null, response.codigo_ingreso)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
