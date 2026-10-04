package com.mars.madereraapp.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mars.madereraapp.data.repository.RequerimientoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadRequerimientoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: RequerimientoRepository
) : BaseSyncWorker(appContext, workerParams) {

    override suspend fun sync(localId: Long): Boolean = repository.syncRequerimiento(localId)
}
