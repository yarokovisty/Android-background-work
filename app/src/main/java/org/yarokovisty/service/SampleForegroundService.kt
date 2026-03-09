package org.yarokovisty.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class SampleForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "Foreground Service Channel"
        private const val NOTIFICATION_ID = 1

        private const val ACTION_START = "action_start"
        private const val ACTION_STOP = "action_stop"
        private const val ACTION_RESET = "action_reset"
        private const val ACTION_DELETE = "action_delete"

        fun newIntent(context: Context) =
            Intent(context, SampleForegroundService::class.java)
    }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var notificationManager: NotificationManager

    private var counter = 0
    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startCounter()
            ACTION_STOP -> stopCounter()
            ACTION_RESET -> resetCounter()
            ACTION_DELETE -> deleteCounter()
            else -> startForegroundIfNeeded()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        job?.cancel()
        log("onDestroy")
    }

    private fun startForegroundIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, getNotification())
        } else {
            notificationManager.notify(NOTIFICATION_ID, getNotification())
        }
    }

    private fun startCounter() {
        if (job?.isActive == true) return
        job = coroutineScope.launch {
            while (isActive) {
                delay(1000)
                counter++
                notificationManager.notify(NOTIFICATION_ID, getNotification())
                log("Counter: $counter")
            }
        }
    }

    private fun stopCounter() {
        job?.cancel()
    }

    private fun resetCounter() {
        counter = 0
        notificationManager.notify(NOTIFICATION_ID, getNotification())
    }

    private fun deleteCounter() {
        job?.cancel()
        stopSelf()
        log("Service stopped by user closing notification")
    }

    private fun getNotification(): Notification {
        val remoteViews = RemoteViews(packageName, R.layout.notification_custom)
        remoteViews.setTextViewText(R.id.tvCounter, "Counter: $counter")

        val startIntent = PendingIntent.getService(
            this, 0,
            Intent(this, SampleForegroundService::class.java).setAction(ACTION_START),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, SampleForegroundService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resetIntent = PendingIntent.getService(
            this, 2,
            Intent(this, SampleForegroundService::class.java).setAction(ACTION_RESET),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val deleteIntent = PendingIntent.getService(
            this,
            3,
            Intent(this, SampleForegroundService::class.java).setAction(ACTION_DELETE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        remoteViews.setOnClickPendingIntent(R.id.btnStart, startIntent)
        remoteViews.setOnClickPendingIntent(R.id.btnStop, stopIntent)
        remoteViews.setOnClickPendingIntent(R.id.btnReset, resetIntent)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setOngoing(false)
            .setDeleteIntent(deleteIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(null, null)
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun log(message: String) {
        Log.d("FOREGROUND_SERVICE_TAG", message)
    }
}