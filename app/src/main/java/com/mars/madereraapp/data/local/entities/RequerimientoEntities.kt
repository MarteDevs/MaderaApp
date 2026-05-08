package com.mars.madereraapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "requerimientos",
    indices = [Index(value = ["codigo_req"], unique = true)]
)
data class RequerimientoEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverId: Int? = null,
    val codigo_req: String? = null,
    val fecha: String,
    val mina_id: Int,
    val minaNombre: String,
    val supervisor_id: Int?,
    val supervisorNombre: String?,
    val estado: String, // PENDIENTE, COMPLETADO, PARCIAL
    val isPendingSync: Boolean = false
)

@Entity(
    tableName = "requerimientos_detalle",
    foreignKeys = [
        ForeignKey(
            entity = RequerimientoEntity::class,
            parentColumns = ["localId"],
            childColumns = ["localRequerimientoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["localRequerimientoId"])]
)
data class RequerimientoDetalleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val localRequerimientoId: Long,
    val serverId: Int? = null,
    val articulo_id: Int,
    val articuloNombre: String,
    val proveedor_id: Int,
    val proveedorNombre: String,
    val cantidad: Double,
    val precio_proveedor: Double,
    val precio_mina: Double
)
