package com.mars.madereraapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mars.madereraapp.data.local.entities.RequerimientoDetalleEntity
import com.mars.madereraapp.data.local.entities.RequerimientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RequerimientoDao {

    @Transaction
    @Query("SELECT * FROM requerimientos ORDER BY localId DESC")
    fun getAllRequerimientos(): Flow<List<RequerimientoEntity>>

    @Query("SELECT * FROM requerimientos_detalle WHERE localRequerimientoId = :localId")
    suspend fun getDetallesForRequerimiento(localId: Long): List<RequerimientoDetalleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequerimiento(requerimiento: RequerimientoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalles(detalles: List<RequerimientoDetalleEntity>)

    @Query("SELECT * FROM requerimientos WHERE isPendingSync = 1")
    suspend fun getPendingSyncRequerimientos(): List<RequerimientoEntity>

    @Query("UPDATE requerimientos SET isPendingSync = 0, serverId = :serverId, codigo_req = :codigo WHERE localId = :localId")
    suspend fun markAsSynced(localId: Long, serverId: Int?, codigo: String?)
    
    @Query("DELETE FROM requerimientos WHERE localId = :localId")
    suspend fun deleteRequerimiento(localId: Long)
}
