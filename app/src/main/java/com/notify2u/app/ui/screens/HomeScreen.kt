package com.notify2u.app.ui.screens

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.notify2u.app.data.local.PaymentReminderEntity
import com.notify2u.app.data.model.PaymentStatus
import com.notify2u.app.ui.components.IOSCard
import com.notify2u.app.ui.components.PaymentCard
import com.notify2u.app.ui.components.ReminderDetailsBottomSheet
import com.notify2u.app.ui.theme.*
import com.notify2u.app.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    filterType: String = "ALL",
    showAddScreen: Boolean = false,
    initialName: String = ""
) {
    val reminders by viewModel.reminders.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var selectedReminder by remember { mutableStateOf<PaymentReminderEntity?>(null) }
    val gridState = rememberLazyGridState()
    val context = LocalContext.current

    val filteredReminders = when (filterType) {
        "TO_PAY" -> reminders.filter { !it.isReceived && it.amount > 0 }
        "TO_RECEIVE" -> reminders.filter { !it.isReceived && it.amount < 0 }
        else -> reminders.filter { !it.isReceived }
    }

    LaunchedEffect(showAddScreen) {
        if (showAddScreen) {
            navController.navigate("add_reminder")
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Notify2u",
                    style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                
                Spacer(Modifier.height(16.dp))
                
                // Segmented Control
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    val options = listOf("ALL", "TO_PAY", "TO_RECEIVE")
                    val labels = listOf("All", "To Pay", "To Receive")
                    
                    options.forEachIndexed { index, option ->
                        val isSelected = filterType == option
                        val shape = when (index) {
                            0 -> RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                            options.size - 1 -> RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                            else -> RectangleShape
                        }
                        
                        Button(
                            onClick = { 
                                navController.navigate("home/$option") {
                                    popUpTo("home/$filterType") { inclusive = true }
                                }
                            },
                            shape = shape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .then(
                                    if (isSelected) Modifier.border(1.dp, Color.White.copy(alpha = 0.5f), shape) else Modifier
                                ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(labels[index], style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_reminder") },
                shape = MaterialTheme.shapes.extraLarge,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                modifier = Modifier.border(2.dp, Brush.linearGradient(listOf(Color(0xFFFF00E5), Color(0xFF00E0FF))), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Brush.linearGradient(listOf(Color(0xFFFF00E5), Color(0xFF00E0FF))), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filteredReminders.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(modifier = Modifier.fillMaxSize().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "No payments found", 
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredReminders) { reminder ->
                    PaymentCard(
                        reminder = reminder.copy(amount = kotlin.math.abs(reminder.amount)),
                        onClick = { selectedReminder = it }
                    )
                }
            }
        }

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
            onMarkAsReceived = { rem, paidDate ->
                viewModel.markAsReceived(rem, paidDate)
            },
            onNoteChange = { viewModel.updateReminder(it) }
        )
    }
    }
}
