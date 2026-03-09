package org.yarokovisty.service

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class SampleIntentService : IntentService("SampleIntentService") {

    companion object {
        private const val CHANNEL_ID = "download_channel"
        private const val CHANNEL_NAME = "Fake Download Channel"
        private const val NOTIFICATION_ID = 101

        fun newIntent(context: Context) =
            Intent(context, SampleIntentService::class.java)
    }

    private lateinit var notificationManager: NotificationManager
    private val repository = FakeDownloadRepository()

    @Deprecated("Deprecated in Java")
    override fun onCreate() {
        super.onCreate()
        setIntentRedelivery(true)
        log("onCreate ${Thread.currentThread()}")
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        log("onHandleIntent ${Thread.currentThread()}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createIndeterminateNotification())
        } else {
            notificationManager.notify(NOTIFICATION_ID, createIndeterminateNotification())
        }


        repository.downloadFakeFile { downloadState ->
            when (downloadState) {

                is DownloadState.Started -> {
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        createIndeterminateNotification()
                    )
                }

                is DownloadState.Progress -> {
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        createProgressNotification(downloadState.percent)
                    )
                }

                is DownloadState.Completed -> {
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        createCompletedNotification()
                    )
                }
            }
        }

        stopSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun onBind(intent: Intent?): IBinder? = null

    private fun createIndeterminateNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading file...")
            .setContentText("Preparing download...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(0, 0, true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun createProgressNotification(progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading file...")
            .setContentText("$progress%")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(100, progress, false)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun createCompletedNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Download complete")
            .setContentText("File successfully downloaded")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(0, 0, false)
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
        Log.d("INTENT_SERVICE_TAG", message)
    }
}