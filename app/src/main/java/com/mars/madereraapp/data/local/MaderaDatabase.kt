package com.mars.madereraapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mars.madereraapp.data.local.dao.CatalogDao
import com.mars.madereraapp.data.local.dao.IngresoDao
import com.mars.madereraapp.data.local.dao.RequerimientoDao
import com.mars.madereraapp.data.local.entities.*

@Database(
    entities = [
        MinaEntity::class,
        ArticuloEntity::class,
        ProveedorEntity::class,
        SupervisorEntity::class,
        RequerimientoEntity::class,
        RequerimientoDetalleEntity::class,
        RequerimientoPendienteEntity::class,
        IngresoEntity::class,
        IngresoDetalleEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MaderaDatabase : RoomDatabase() {
    abstract fun catalogDao(): CatalogDao
    abstract fun requerimientoDao(): RequerimientoDao
    abstract fun ingresoDao(): IngresoDao
}
