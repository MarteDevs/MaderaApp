package com.mars.madereraapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mars.madereraapp.data.local.entities.IngresoDetalleEntity
import com.mars.madereraapp.data.local.entities.IngresoEntity
import com.mars.madereraapp.data.local.entities.RequerimientoPendienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngresoDao {

    // Pending requirements for selection
    @Query("SELECT * FROM requerimientos_pendientes ORDER BY codigo_req DESC")
    fun getRequerimientosPendientes(): Flow<List<RequerimientoPendienteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequerimientosPendientes(items: List<RequerimientoPendienteEntity>)

    @Query("DELETE FROM requerimientos_pendientes")
    suspend fun clearRequerimientosPendientes()

    // Ingresos
    @Query("SELECT * FROM ingresos ORDER BY localId DESC")
    fun getAllIngresos(): Flow<List<IngresoEntity>>

    @Query("SELECT * FROM ingresos_detalle WHERE localIngresoId = :localId")
    suspend fun getDetallesForIngreso(localId: Long): List<IngresoDetalleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngreso(ingreso: IngresoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalles(detalles: List<IngresoDetalleEntity>)

    @Query("SELECT * FROM ingresos WHERE isPendingSync = 1")
    suspend fun getPendingSyncIngresos(): List<IngresoEntity>

    @Query("UPDATE ingresos SET isPendingSync = 0, serverId = :serverId, codigo_ingreso = :codigo WHERE localId = :localId")
    suspend fun markAsSynced(localId: Long, serverId: Int?, codigo: String?)
    
    @Query("DELETE FROM ingresos WHERE localId = :localId")
    suspend fun deleteIngreso(localId: Long)
}
