package com.mars.madereraapp.data.repository

import com.mars.madereraapp.data.local.dao.CatalogDao
import com.mars.madereraapp.data.local.entities.ArticuloEntity
import com.mars.madereraapp.data.local.entities.MinaEntity
import com.mars.madereraapp.data.local.entities.ProveedorEntity
import com.mars.madereraapp.data.local.entities.SupervisorEntity
import com.mars.madereraapp.data.remote.CatalogApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor(
    private val apiService: CatalogApiService,
    private val dao: CatalogDao
) {
    // Getters from Local (Single Source of Truth)
    fun getMinas(): Flow<List<MinaEntity>> = dao.getAllMinas()
    fun getArticulos(): Flow<List<ArticuloEntity>> = dao.getAllArticulos()
    fun getProveedores(): Flow<List<ProveedorEntity>> = dao.getAllProveedores()
    fun getSupervisores(): Flow<List<SupervisorEntity>> = dao.getAllSupervisores()

    // Sync method
    suspend fun syncCatalogs() {
        try {
            // Sync Minas
            val remoteMinas = apiService.getMinas()
            dao.clearMinas()
            dao.insertMinas(remoteMinas)

            // Sync Articulos
            val remoteArticulos = apiService.getArticulos()
            dao.clearArticulos()
            dao.insertArticulos(remoteArticulos)

            // Sync Proveedores
            val remoteProveedores = apiService.getProveedores()
            dao.clearProveedores()
            dao.insertProveedores(remoteProveedores)

            // Sync Supervisores
            val remoteSupervisores = apiService.getSupervisores()
            dao.clearSupervisores()
            dao.insertSupervisores(remoteSupervisores)
        } catch (e: Exception) {
            e.printStackTrace()
            // In a real app, handle error (e.g., logging or retry)
        }
    }
}
