package com.classroomconnect.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.classroomconnect.app.data.PortalRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AssignmentSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PortalRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = try {
        repository.syncPending()
        Result.success()
    } catch (error: Exception) {
        if (runAttemptCount < 3) Result.retry() else Result.failure()
    }
}
