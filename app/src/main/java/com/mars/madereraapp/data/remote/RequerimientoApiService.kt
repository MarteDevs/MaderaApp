package com.mars.madereraapp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class RequerimientoRequest(
    val fecha: String,
    val mina_id: Int,
    val supervisor_id: Int?,
    val detalles: List<DetalleRequest>
)

data class DetalleRequest(
    val articulo_id: Int,
    val proveedor_id: Int,
    val cantidad: Double,
    val precio_proveedor: Double,
    val precio_mina: Double
)

data class RequerimientoResponse(
    val success: Boolean,
    val codigo: String? = null,
    val mensaje: String? = null
)

data class RequerimientoHistorialItem(
    val id: Int,
    val codigo_req: String,
    val fecha: String,
    val mina: String,
    val supervisor: String?,
    val estado: String,
    val total_proveedor: Double,
    val total_mina: Double
)

data class RequerimientoDetalleItem(
    val id: Int,
    val articulo: String,
    val proveedor: String,
    val pedido: Double,
    val precio_proveedor: Double,
    val precio_mina: Double,
    val entregado: Double,
    val faltante: Double
)

interface RequerimientoApiService {
    @GET("requerimientos/historial")
    suspend fun getHistorial(): List<RequerimientoHistorialItem>

    @GET("requerimientos/{id}/detalles")
    suspend fun getDetalles(@retrofit2.http.Path("id") id: Int): List<RequerimientoDetalleItem>

    @POST("requerimientos")
    suspend fun crear(@Body body: RequerimientoRequest): RequerimientoResponse
}
