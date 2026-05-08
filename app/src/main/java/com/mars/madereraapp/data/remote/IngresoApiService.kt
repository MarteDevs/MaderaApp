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
    val total_entregado: Double
)

interface IngresoApiService {
    @GET("ingresos/pendientes")
    suspend fun getPendientes(): List<RequerimientoPendienteEntity>

    @GET("ingresos/historial")
    suspend fun getHistorial(): List<IngresoHistorialItem>

    @POST("ingresos")
    suspend fun crear(@Body body: IngresoRequest): IngresoResponse
}
