package com.notify2u.app.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.notify2u.app.data.local.Notify2uDatabase
import com.notify2u.app.data.local.PaymentReminderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminder_id", -1)
        val action = intent.action

        if (reminderId == -1) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(reminderId)

        val dao = Notify2uDatabase.getInstance(context).paymentReminderDao()
        val firestoreRepository = com.notify2u.app.data.repository.FirestoreRepository()

        when (action) {
            "ACTION_MARK_DONE" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val reminder = dao.getAllRemindersOnce().find { it.id == reminderId }
                    reminder?.let {
                        val updated = it.copy(
                            isReceived = true, 
                            paidDate = LocalDate.now().toString(),
                            lastUpdated = System.currentTimeMillis()
                        )
                        dao.updateReminder(updated)
                        firestoreRepository.syncPaymentReminder(updated)
                    }
                }
            }
            "ACTION_SNOOZE" -> {
                // For snooze, we could reschedule the notification for later
                // Simulating snooze by showing it again in 10 minutes (logic would be in Scheduler)
            }
        }
    }
}
