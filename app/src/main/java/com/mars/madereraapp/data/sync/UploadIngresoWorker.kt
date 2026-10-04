package com.mars.madereraapp.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mars.madereraapp.data.repository.IngresoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadIngresoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: IngresoRepository
) : BaseSyncWorker(appContext, workerParams) {

    override suspend fun sync(localId: Long): Boolean = repository.syncIngreso(localId)
}
