package com.notify2u.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_tasks")
data class TodoTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val dueDate: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "General",
    val colorHex: String = "#FF6200EE", // Default brand color
    val tag: String? = null,
    val subtasks: String = "", // Comma-separated or newline-separated
    val tags: String = "" // Space-separated
)

enum class Priority {
    LOW, MEDIUM, HIGH
}
