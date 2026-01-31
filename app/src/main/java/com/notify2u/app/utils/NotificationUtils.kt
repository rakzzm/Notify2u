package com.notify2u.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.notify2u.app.R

const val CHANNEL_ID = "DUE_REMINDER_CHANNEL"

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Payment Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifies when payments are due today"
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

fun showDueNotification(context: Context, reminderName: String, reminderId: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionGranted = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            return
        }
    }

    val markDoneIntent = android.app.PendingIntent.getBroadcast(
        context,
        reminderId,
        android.content.Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_MARK_DONE"
            putExtra("reminder_id", reminderId)
        },
        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.app_logo)
        .setContentTitle("Payment Due Today")
        .setContentText("Reminder: $reminderName")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .addAction(android.R.drawable.ic_menu_save, "Mark as Done", markDoneIntent)

    with(NotificationManagerCompat.from(context)) {
        notify(reminderId, builder.build())
    }
}

fun showPersistentQuickAddNotification(context: Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
        putExtra("ACTION_QUICK_ADD", true)
    }
    val pendingIntent = android.app.PendingIntent.getActivity(
        context, 
        999, 
        intent, 
        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.app_logo)
        .setContentTitle("Notify2u Quick Add")
        .setContentText("Tap to quickly add a task or reminder")
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true) // Persistent
        .setContentIntent(pendingIntent)

    with(NotificationManagerCompat.from(context)) {
        notify(999, builder.build())
    }
}
