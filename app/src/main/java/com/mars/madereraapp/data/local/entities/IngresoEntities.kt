package com.mars.madereraapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "requerimientos_pendientes")
data class RequerimientoPendienteEntity(
    @PrimaryKey val requerimiento_detalle_id: Int,
    val codigo_req: String,
    val mina: String,
    val articulo: String,
    val proveedor: String,
    val pedido: Double,
    val entregado: Double,
    val faltante: Double
)

@Entity(tableName = "ingresos")
data class IngresoEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverId: Int? = null,
    val codigo_ingreso: String? = null,
    val fecha: String,
    val viaje: String?,
    val vale: String?,
    val observacion: String?,
    val isPendingSync: Boolean = false
)

@Entity(
    tableName = "ingresos_detalle",
    foreignKeys = [
        ForeignKey(
            entity = IngresoEntity::class,
            parentColumns = ["localId"],
            childColumns = ["localIngresoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["localIngresoId"])]
)
data class IngresoDetalleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val localIngresoId: Long,
    val requerimiento_detalle_id: Int,
    val cantidad_entregada: Double
)
