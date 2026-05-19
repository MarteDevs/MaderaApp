package com.mars.madereraapp.data.remote

import com.mars.madereraapp.data.local.entities.RequerimientoPendienteEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class IngresoRequest(
    val fecha: String,
    val viaje: String?,
    val vale: String?,
    val observacion: String?,
    val detalles: List<IngresoDetalleRequest>
)

data class IngresoDetalleRequest(
    val requerimiento_detalle_id: Int,
    val cantidad_entregada: Double
)

data class IngresoResponse(
    val mensaje: String,
    val codigo_ingreso: String? = null
)

data class IngresoHistorialItem(
    val id: Int,
    val codigo_ingreso: String,
    val fecha: String,
    val viaje: String?,
    val vale: String?,
    val observacion: String?,
    val total_items: Int,
    val total_entregado: Double,
    val total_proveedor: Double,
    val total_mina: Double
)

data class IngresoDetalleItem(
    val id: Int,
    val articulo: String,
    val proveedor: String,
    val pedido: Double? = 0.0,
    val cantidad_entregada: Double = 0.0,
    val precio_proveedor: Double = 0.0,
    val precio_mina: Double = 0.0,
    val es_extra: Int = 0
) {
    val isExtra: Boolean get() = es_extra == 1
}

interface IngresoApiService {
    @GET("ingresos/pendientes")
    suspend fun getPendientes(): List<RequerimientoPendienteEntity>

    @GET("ingresos")
    suspend fun getHistorial(): List<IngresoHistorialItem>

    @GET("ingresos/{id}/detalle")
    suspend fun getDetalle(@retrofit2.http.Path("id") id: Int): List<IngresoDetalleItem>

    @POST("ingresos")
    suspend fun crear(@Body body: IngresoRequest): IngresoResponse
}
