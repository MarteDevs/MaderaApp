package com.mars.madereraapp.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mars.madereraapp.data.repository.IngresoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadIngresoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: IngresoRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val localId = inputData.getLong("localId", -1)
        if (localId == -1L) return Result.failure()

        return if (repository.syncIngreso(localId)) {
            Result.success()
        } else {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
