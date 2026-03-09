package org.yarokovisty.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SampleJobService : JobService() {

    companion object {

        const val JOB_ID = 111
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }


    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartCommand")
        coroutineScope.launch {
            for (i in 0 until 30) {
                delay(1000)
                log("Timer $i")
            }
            jobFinished(params, false)
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        log("onStopJob")
        return true
    }

    private fun log(message: String) {
        Log.d("JOB_SERVICE_TAG", "SampleJobService: $message")
    }
}