package com.mars.madereraapp.di

import android.content.Context
import androidx.room.Room
import com.mars.madereraapp.data.local.MaderaDatabase
import com.mars.madereraapp.data.local.dao.CatalogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MaderaDatabase {
        return Room.databaseBuilder(
            context,
            MaderaDatabase::class.java,
            "madera_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCatalogDao(database: MaderaDatabase): CatalogDao {
        return database.catalogDao()
    }

    @Provides
    fun provideRequerimientoDao(database: MaderaDatabase): RequerimientoDao {
        return database.requerimientoDao()
    }

    @Provides
    fun provideIngresoDao(database: MaderaDatabase): IngresoDao {
        return database.ingresoDao()
    }
}
