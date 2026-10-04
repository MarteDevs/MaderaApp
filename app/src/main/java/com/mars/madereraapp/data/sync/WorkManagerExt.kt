package com.mars.madereraapp.data.sync

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

/**
 * Encola un [BaseSyncWorker] (o cualquier Worker) pasándole el id local a
 * sincronizar. Extraído de IngresoViewModel.registrarIngreso y
 * RequerimientoViewModel.crearRequerimiento, que armaban el mismo
 * OneTimeWorkRequest a mano.
 */
inline fun <reified W : ListenableWorker> enqueueUploadWork(context: Context, localId: Long) {
    val request = OneTimeWorkRequest.Builder(W::class.java)
        .setInputData(Data.Builder().putLong("localId", localId).build())
        .build()
    WorkManager.getInstance(context).enqueue(request)
}
