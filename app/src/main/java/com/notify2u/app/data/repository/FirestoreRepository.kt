package com.notify2u.app.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.notify2u.app.data.local.PaymentReminderEntity
import com.notify2u.app.data.local.TodoTaskEntity
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    private val userId: String?
        get() = auth.currentUser?.uid

    suspend fun syncPaymentReminder(reminder: PaymentReminderEntity) {
        val uid = userId ?: return
        val reminderMap = mapOf(
            "id" to reminder.id,
            "name" to reminder.name,
            "amount" to reminder.amount,
            "dueDate" to reminder.dueDate,
            "isReceived" to reminder.isReceived,
            "status" to reminder.status,
            "personName" to reminder.personName,
            "month" to reminder.month,
            "recurringType" to reminder.recurringType,
            "note" to reminder.note,
            "direction" to reminder.direction,
            "partialAmountPaid" to reminder.partialAmountPaid,
            "partialDueDate" to reminder.partialDueDate,
            "lastUpdated" to reminder.lastUpdated,
            "userId" to uid
        )

        firestore.collection("users")
            .document(uid)
            .collection("payment_reminders")
            .document(reminder.id.toString())
            .set(reminderMap, SetOptions.merge())
            .await()
    }

    suspend fun syncTodoTask(task: TodoTaskEntity) {
        val uid = userId ?: return
        val taskMap = mapOf(
            "id" to task.id,
            "title" to task.title,
            "description" to task.description,
            "isCompleted" to task.isCompleted,
            "dueDate" to task.dueDate,
            "priority" to task.priority.name,
            "category" to task.category,
            "colorHex" to task.colorHex,
            "lastUpdated" to task.lastUpdated,
            "userId" to uid
        )

        firestore.collection("users")
            .document(uid)
            .collection("todo_tasks")
            .document(task.id.toString())
            .set(taskMap, SetOptions.merge())
            .await()
    }

    suspend fun deletePaymentReminder(reminderId: Int) {
        val uid = userId ?: return
        firestore.collection("users")
            .document(uid)
            .collection("payment_reminders")
            .document(reminderId.toString())
            .delete()
            .await()
    }

    suspend fun deleteTodoTask(taskId: Int) {
        val uid = userId ?: return
        firestore.collection("users")
            .document(uid)
            .collection("todo_tasks")
            .document(taskId.toString())
            .delete()
            .await()
    }
}
