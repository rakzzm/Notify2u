package com.notify2u.app.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notify2u.app.data.local.PaymentReminderEntity
import com.notify2u.app.ui.components.PaymentCard
import com.notify2u.app.ui.components.ReminderDetailsBottomSheet
import com.notify2u.app.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    viewModel: HomeViewModel,
    navController: androidx.navigation.NavHostController
) {
    val reminders by viewModel.reminders.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedReminder by remember { mutableStateOf<PaymentReminderEntity?>(null) }

    val completedReminders = remember(reminders) {
        reminders.filter { it.isReceived }
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredReminders = remember(searchQuery, completedReminders) {
        if (searchQuery.isBlank()) completedReminders
        else completedReminders.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.personName.contains(searchQuery, ignoreCase = true) ||
                    it.amount.toString().contains(searchQuery)
        }
    }

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                
                Spacer(Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search history...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

            if (filteredReminders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No matching reminders found.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredReminders) { reminder ->
                        val cardColor = if (reminder.amount < 0)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer

                        PaymentCard(
                            reminder = reminder,
                            onClick = { selectedReminder = it },
                            cardColor = cardColor
                        )
                }
            }
        }
    }

    // Bottom sheet for details
    selectedReminder?.let { reminder ->
        ReminderDetailsBottomSheet(
            reminder = reminder,
            onDismiss = { selectedReminder = null },
            onDelete = { rem ->
                coroutineScope.launch {
                    viewModel.deleteReminder(rem)
                    val result = snackbarHostState.showSnackbar(
                        message = "Reminder deleted",
                        actionLabel = "UNDO",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.addReminder(rem)
                    }
                }
            },
            onMarkAsReceived = { rem, paidDate -> viewModel.markAsReceived(rem, paidDate) },
            onNoteChange = { viewModel.updateReminder(it) }
        )
    }
}
