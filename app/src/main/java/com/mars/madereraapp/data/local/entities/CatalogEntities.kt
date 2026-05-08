package com.mars.madereraapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "minas")
data class MinaEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val razonSocial: String?,
    val ruc: String?,
    val estado: Int
)

@Entity(tableName = "articulos")
data class ArticuloEntity(
    @PrimaryKey val id: Int,
    val codigo: String?,
    val nombre: String,
    val precioProveedor: Double,
    val precioMina: Double
)

@Entity(tableName = "proveedores")
data class ProveedorEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val estado: Int
)

@Entity(tableName = "supervisores")
data class SupervisorEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val estado: Int
)
