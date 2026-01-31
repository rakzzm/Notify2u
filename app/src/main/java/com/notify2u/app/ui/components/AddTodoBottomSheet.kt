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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notify2u.app.data.local.Priority
import com.notify2u.app.data.local.TodoTaskEntity
import com.notify2u.app.ui.theme.NeonPink
import com.notify2u.app.ui.theme.NeonBlue
import com.notify2u.app.ui.theme.BackgroundDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoBottomSheet(
    onDismiss: () -> Unit,
    onSave: (TodoTaskEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Add New Task", style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = NeonPink,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f),
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = NeonBlue,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f),
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text("Priority", style = MaterialTheme.typography.labelLarge, color = Color.White)
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Priority.values().forEach { p ->
                    val isSelected = priority == p
                    FilterChip(
                        selected = isSelected,
                        onClick = { priority = p },
                        label = { Text(p.name) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (p == Priority.HIGH) Color(0xFFFF4848) else NeonPink,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.05f),
                            labelColor = Color.White.copy(alpha = 0.6f)
                        ),
                        border = if (isSelected) null else FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = Color.White.copy(alpha = 0.2f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text("Accent Color", style = MaterialTheme.typography.labelLarge, color = Color.White)
            val colors = listOf("#FF00E5", "#00E0FF", "#FFFF00", "#00FF00", "#FF4848", "#FFFFFF")
            var selectedColor by remember { mutableStateOf(colors[0]) }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colors.forEach { colorStr ->
                    val color = Color(android.graphics.Color.parseColor(colorStr))
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { selectedColor = colorStr }
                            .border(
                                width = if (selectedColor == colorStr) 2.dp else 0.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        shape = CircleShape,
                        color = color,
                        tonalElevation = if (selectedColor == colorStr) 8.dp else 0.dp
                    ) {}
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            ListItem(
                headlineContent = { Text("Due Date", color = Color.White) },
                supportingContent = { Text("Set a date and time", color = Color.White.copy(alpha = 0.6f)) },
                leadingContent = { Icon(Icons.Default.AccessTime, null, tint = NeonBlue) },
                trailingContent = { Switch(checked = false, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = NeonBlue)) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
            ListItem(
                headlineContent = { Text("Reminder", color = Color.White) },
                supportingContent = { Text("Remind me when due", color = Color.White.copy(alpha = 0.6f)) },
                leadingContent = { Icon(Icons.Default.Notifications, null, tint = NeonPink) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
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
                            category = "PERSONAL"
                        ))
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(NeonPink, NeonBlue))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Save Task", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
