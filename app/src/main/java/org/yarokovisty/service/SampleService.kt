package org.yarokovisty.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SampleService : Service() {

    private val instanceId = System.currentTimeMillis()
    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, SampleService::class.java)
        }

    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand instanceId=$instanceId")
        log("onStartCommand")

        coroutineScope.launch {
            for (i in 0 until 60) {
                delay(1000)
                log("Timer $i instanceId=$instanceId")
            }

            stopSelf()
        }


        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "SampleService: $message")
    }
}