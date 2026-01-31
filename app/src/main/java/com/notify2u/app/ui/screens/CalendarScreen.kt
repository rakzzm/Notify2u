package com.notify2u.app.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notify2u.app.data.local.TodoTaskEntity
import com.notify2u.app.ui.viewmodel.HomeViewModel
import com.notify2u.app.ui.viewmodel.TodoViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    homeViewModel: HomeViewModel,
    todoViewModel: TodoViewModel,
    navController: androidx.navigation.NavHostController
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val reminders by homeViewModel.reminders.collectAsState()
    val tasks by todoViewModel.allTasks.collectAsState()

    val tasksForDay = tasks.filter { it.dueDate?.startsWith(selectedDate.toString()) == true }
    val remindersForDay = reminders.filter { it.dueDate.startsWith(selectedDate.toString()) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Calendar",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add Event */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) { 
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MonthHeader(
                        currentMonth = currentMonth,
                        onPrevious = { currentMonth = currentMonth.minusMonths(1) },
                        onNext = { currentMonth = currentMonth.plusMonths(1) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CalendarGrid(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it },
                        reminders = reminders.map { it.dueDate },
                        tasks = tasks.mapNotNull { it.dueDate }
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))

            Text(
                text = "${selectedDate.dayOfMonth} ${selectedDate.month.name.lowercase().capitalize()} Schedule",
                modifier = Modifier.padding(horizontal = 20.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (tasksForDay.isEmpty() && remindersForDay.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No events scheduled",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(tasksForDay) { task ->
                    CalendarTaskItem(task.title, task.isCompleted, task.colorHex, "9:00 AM")
                }
                items(remindersForDay) { reminder ->
                    CalendarTaskItem(reminder.name, reminder.isReceived, "#E91E63", reminder.dueDate.substringAfter(" "))
                }
            }
        }
    }
}

@Composable
fun CalendarTaskItem(title: String, isDone: Boolean, colorHex: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored Vertical Bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(48.dp)
                .background(Color(android.graphics.Color.parseColor(colorHex)))
        )
        Spacer(Modifier.width(12.dp))
        // Circular Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.dp, if (isDone) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline, CircleShape)
                .background(if (isDone) MaterialTheme.colorScheme.secondary else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSecondary)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyLarge)
            if (isDone) Text("Completed today", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
        }
        Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(time, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    reminders: List<String>,
    tasks: List<String>
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstOfMonth = currentMonth.atDay(1)
    val dayOfWeekOffset = firstOfMonth.dayOfWeek.value % 7

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {
        val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        items(days) { day ->
            Text(day, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.labelSmall, color = Color.Gray, textAlign = TextAlign.Center)
        }
        items(dayOfWeekOffset) { Box(modifier = Modifier.size(40.dp)) }
        items(daysInMonth) { dayIndex ->
            val day = dayIndex + 1
            val date = currentMonth.atDay(day)
            val isSelected = date == selectedDate
            val hasActivity = reminders.any { it.startsWith(date.toString()) } || tasks.any { it.startsWith(date.toString()) }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onDateSelected(date) }
                    .background(if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(day.toString(), color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface)
                    if (hasActivity && !isSelected) {
                        Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.secondary, shape = CircleShape))
                    }
                }
            }
        }
    }
}

@Composable
fun MonthHeader(
    currentMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = currentMonth.month.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = currentMonth.year.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row {
            IconButton(onClick = onPrevious) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
            }
        }
    }
}
