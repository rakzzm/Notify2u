package com.notify2u.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notify2u.app.data.local.Priority
import com.notify2u.app.data.local.TodoTaskEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoBottomSheet(
    onDismiss: () -> Unit,
    onSave: (TodoTaskEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Add New Task", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Priority", style = MaterialTheme.typography.labelLarge)
            Row {
                Priority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.name) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Color", style = MaterialTheme.typography.labelLarge)
            val colors = listOf("#03A9F4", "#0288D1", "#B3E5FC", "#E91E63", "#F8BBD0", "#C2185B")
            var selectedColor by remember { mutableStateOf(colors[0]) }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colors.forEach { colorStr ->
                    val color = Color(android.graphics.Color.parseColor(colorStr))
                    Surface(
                        modifier = Modifier.size(32.dp).clickable { selectedColor = colorStr },
                        shape = CircleShape,
                        color = color,
                        border = if (selectedColor == colorStr) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                    ) {}
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Reference: image_1 (Reminders, notifications & alarms)
            ListItem(
                headlineContent = { Text("Due Date") },
                supportingContent = { Text("Set a date and time") },
                leadingContent = { Icon(Icons.Default.AccessTime, null) },
                trailingContent = { Switch(checked = false, onCheckedChange = {}) }
            )
            ListItem(
                headlineContent = { Text("Reminder") },
                supportingContent = { Text("Remind me when due") },
                leadingContent = { Icon(Icons.Default.Notifications, null) }
            )
            ListItem(
                headlineContent = { Text("Repeat") },
                supportingContent = { Text("Never") },
                leadingContent = { Icon(Icons.Default.Repeat, null) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(TodoTaskEntity(
                            title = title, 
                            description = description, 
                            priority = priority,
                            colorHex = selectedColor,
                            category = "PERSONAL" // Defaulting to one of the tabs for now
                        ))
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Task")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
