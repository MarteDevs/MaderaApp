package com.mars.madereraapp.data.repository

import com.mars.madereraapp.data.local.dao.RequerimientoDao
import com.mars.madereraapp.data.local.entities.RequerimientoDetalleEntity
import com.mars.madereraapp.data.local.entities.RequerimientoEntity
import com.mars.madereraapp.data.remote.DetalleRequest
import com.mars.madereraapp.data.remote.RequerimientoApiService
import com.mars.madereraapp.data.remote.RequerimientoRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequerimientoRepository @Inject constructor(
    private val apiService: RequerimientoApiService,
    private val dao: RequerimientoDao
) {
    val requerimientos: Flow<List<RequerimientoEntity>> = dao.getAllRequerimientos()

    suspend fun getDetalles(localId: Long) = dao.getDetallesForRequerimiento(localId)

    suspend fun fetchHistorial() {
        try {
            val remoteHistorial = apiService.getHistorial()
            dao.clearSyncedRequerimientos()
            // Map remote to local entities
            // Note: This is a simple sync. In a real app we might want to be more careful.
            remoteHistorial.forEach { item ->
                val entity = RequerimientoEntity(
                    serverId = item.id,
                    codigo_req = item.codigo_req,
                    fecha = item.fecha,
                    mina_id = 0, // No tenemos el ID en el historial remoto, solo el nombre
                    minaNombre = item.mina,
                    supervisor_id = null,
                    supervisorNombre = item.supervisor,
                    estado = item.estado,
                    total_proveedor = item.total_proveedor,
                    total_mina = item.total_mina,
                    isPendingSync = false
                )
                dao.insertRequerimiento(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun guardarRequerimientoLocal(
        fecha: String,
        minaId: Int,
        minaNombre: String,
        supervisorId: Int?,
        supervisorNombre: String?,
        detalles: List<RequerimientoDetalleEntity>
    ): Long {
        val requerimiento = RequerimientoEntity(
            fecha = fecha,
            mina_id = minaId,
            minaNombre = minaNombre,
            supervisor_id = supervisorId,
            supervisorNombre = supervisorNombre,
            estado = "PENDIENTE",
            isPendingSync = true
        )
        val localId = dao.insertRequerimiento(requerimiento)
        val detallesWithId = detalles.map { it.copy(localRequerimientoId = localId) }
        dao.insertDetalles(detallesWithId)
        return localId
    }

    suspend fun syncRequerimiento(localId: Long): Boolean {
        val requerimiento = dao.getPendingSyncRequerimientos().find { it.localId == localId } ?: return false
        val detalles = dao.getDetallesForRequerimiento(localId)

        val request = RequerimientoRequest(
            fecha = requerimiento.fecha,
            mina_id = requerimiento.mina_id,
            supervisor_id = requerimiento.supervisor_id,
            detalles = detalles.map {
                DetalleRequest(
                    articulo_id = it.articulo_id,
                    proveedor_id = it.proveedor_id,
                    cantidad = it.cantidad,
                    precio_proveedor = it.precio_proveedor,
                    precio_mina = it.precio_mina
                )
            }
        )

        return try {
            val response = apiService.crear(request)
            if (response.success) {
                dao.markAsSynced(localId, null, response.codigo)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun forzarCierre(id: Int): Result<Boolean> {
        return try {
            val response = apiService.forzarCierre(id)
            if (response.success) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.mensaje ?: "Error al cerrar"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
