package org.yarokovisty.service

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class SampleWorker(
    context: Context,
    private val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    companion object {

        private const val COUNT_DOWN = "count_down"

        const val WORK_NAME = "work_name"

        fun makeRequest(contDown: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SampleWorker>()
                .setInputData(workDataOf(COUNT_DOWN to contDown))
                .setConstraints(makeConstraints())
                .build()
        }

        private fun makeConstraints() = Constraints.Builder()
            .setRequiresCharging(true)
            .build()
    }

    override fun doWork(): Result {
        log("doWork")
        val countDown = workerParameters.inputData.getInt(COUNT_DOWN, 0)
        for (i in 0 until countDown) {
            Thread.sleep(1000)
            log("Timer $i")
        }

        return Result.success()
    }

    private fun log(message: String) {
        Log.d("WORKER_TAG", "SampleWorker: $message")
    }
}