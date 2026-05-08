package com.mars.madereraapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mars.madereraapp.data.local.entities.ArticuloEntity
import com.mars.madereraapp.data.local.entities.MinaEntity
import com.mars.madereraapp.data.local.entities.ProveedorEntity
import com.mars.madereraapp.data.local.entities.SupervisorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {
    // Minas
    @Query("SELECT * FROM minas WHERE estado = 1 ORDER BY nombre ASC")
    fun getAllMinas(): Flow<List<MinaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMinas(minas: List<MinaEntity>)

    // Articulos
    @Query("SELECT * FROM articulos ORDER BY nombre ASC")
    fun getAllArticulos(): Flow<List<ArticuloEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticulos(articulos: List<ArticuloEntity>)

    // Proveedores
    @Query("SELECT * FROM proveedores WHERE estado = 1 ORDER BY nombre ASC")
    fun getAllProveedores(): Flow<List<ProveedorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProveedores(proveedores: List<ProveedorEntity>)

    // Supervisores
    @Query("SELECT * FROM supervisores WHERE estado = 1 ORDER BY nombre ASC")
    fun getAllSupervisores(): Flow<List<SupervisorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupervisores(supervisores: List<SupervisorEntity>)
    
    @Query("DELETE FROM minas")
    suspend fun clearMinas()
    
    @Query("DELETE FROM articulos")
    suspend fun clearArticulos()
    
    @Query("DELETE FROM proveedores")
    suspend fun clearProveedores()
    
    @Query("DELETE FROM supervisores")
    suspend fun clearSupervisores()
}
