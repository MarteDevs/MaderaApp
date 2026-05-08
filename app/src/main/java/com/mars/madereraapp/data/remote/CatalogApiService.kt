package com.mars.madereraapp.data.remote

import com.mars.madereraapp.data.local.entities.ArticuloEntity
import com.mars.madereraapp.data.local.entities.MinaEntity
import com.mars.madereraapp.data.local.entities.ProveedorEntity
import com.mars.madereraapp.data.local.entities.SupervisorEntity
import retrofit2.http.GET

interface CatalogApiService {
    @GET("minas")
    suspend fun getMinas(): List<MinaEntity>

    @GET("articulos")
    suspend fun getArticulos(): List<ArticuloEntity>

    @GET("proveedores")
    suspend fun getProveedores(): List<ProveedorEntity>

    @GET("supervisores")
    suspend fun getSupervisores(): List<SupervisorEntity>
}
