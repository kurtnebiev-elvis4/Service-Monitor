package com.mycelium.servicemonitor.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    init {
        createNotificationChannel(context)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    /**
     * Shows a notification and groups it together with other server status notifications.
     */
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = System.currentTimeMillis().toInt(),
        smallIcon: Int = android.R.drawable.ic_dialog_alert
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted; request it from the Activity if needed.
            return
        }
        // Group key for all server status notifications.
        val groupKey = "server_status_group"

        // Build the individual notification.
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(groupKey)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)

        // Build the group summary notification.
        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setSummaryText("Server status notifications")
            )
            .setContentTitle("Server Status")
            .setContentText("You have new notifications")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .build()

        // Use a fixed ID for the summary so it gets updated.
        NotificationManagerCompat.from(context).notify(SUMMARY_NOTIFICATION_ID, summaryNotification)
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    companion object {
        private const val CHANNEL_ID = "server_status_channel"
        private const val CHANNEL_DESCRIPTION = "Channel for server status notifications"
        private const val CHANNEL_NAME = "Server Status Channel"
        private const val SUMMARY_NOTIFICATION_ID = 0
    }
}
