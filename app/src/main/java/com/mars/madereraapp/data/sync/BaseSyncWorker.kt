package com.mars.madereraapp.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Workers de subida (Ingreso/Requerimiento) comparten el mismo esqueleto:
 * leer "localId" de inputData, sincronizar un único registro pendiente y
 * reintentar hasta 3 veces antes de fallar. Solo cambia qué repositorio
 * sincroniza, así que eso queda como el único método abstracto.
 */
abstract class BaseSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    /** Sincroniza el registro local [localId] contra el backend; true si tuvo éxito. */
    protected abstract suspend fun sync(localId: Long): Boolean

    override suspend fun doWork(): Result {
        val localId = inputData.getLong("localId", -1)
        if (localId == -1L) return Result.failure()

        return if (sync(localId)) {
            Result.success()
        } else {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
