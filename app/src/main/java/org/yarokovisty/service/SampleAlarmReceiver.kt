package org.yarokovisty.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class SampleAlarmReceiver : BroadcastReceiver() {

    companion object {

        private const val CHANNEL_ID = "alarm_channel"
        private const val CHANNEL_NAME = "Sample Alarm Manager"
        private const val NOTIFICATION_ID = 201
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationManager = createNotificationManager(context)
        val notification = getNotification(context)

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationManager(context: Context) =
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).also {
            createNotificationChannel(it)
        }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotification(context: Context): Notification =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Alarm")
            .setContentText("Tap to open the app!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(getPendingAppIntent(context))
            .setAutoCancel(true)
            .build()

    private fun getPendingAppIntent(context: Context): PendingIntent {
        val appIntent = Intent(context, MainActivity::class.java)
        appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        return PendingIntent.getActivity(
            context,
            0,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}