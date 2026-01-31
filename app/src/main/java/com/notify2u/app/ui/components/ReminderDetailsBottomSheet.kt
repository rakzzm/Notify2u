package com.notify2u.app.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import com.notify2u.app.data.local.PaymentReminderEntity
import com.notify2u.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailsBottomSheet(
    reminder: PaymentReminderEntity,
    onDismiss: () -> Unit,
    onDelete: (PaymentReminderEntity) -> Unit,
    onMarkAsReceived: (PaymentReminderEntity, String) -> Unit,
    onNoteChange: (PaymentReminderEntity) -> Unit,
    onEditReminder: (PaymentReminderEntity) -> Unit = {}
) {
    val context = LocalContext.current

    var note by remember { mutableStateOf(reminder.note) }
    var name by remember { mutableStateOf(reminder.name) }
    var amount by remember { mutableStateOf(reminder.amount.toString()) }
    var dueDate by remember { mutableStateOf(LocalDate.parse(reminder.dueDate)) }
    var recurringType by remember { mutableStateOf(reminder.recurringType) }
    var direction by remember { mutableStateOf(reminder.direction) }

    var showDatePickerForDueDate by remember { mutableStateOf(false) }
    var showDatePickerForPaidDate by remember { mutableStateOf(false) }
    var showPartialPaymentDialog by remember { mutableStateOf(false) }
    var showNextDueDatePicker by remember { mutableStateOf(false) }

    var nextPartialDueDate by remember { mutableStateOf(LocalDate.now()) }
    var paidAmount by remember { mutableStateOf("") }


    val expandedRepeat = remember { mutableStateOf(false) }
    val expandedDirection = remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    val isNoteModified = note.trim() != reminder.note
    val isInfoModified = name != reminder.name ||
            amount.toDoubleOrNull() != reminder.amount ||
            dueDate.toString() != reminder.dueDate ||
            recurringType != reminder.recurringType ||
            direction != reminder.direction

    // Date pickers
    if (showDatePickerForDueDate) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                dueDate = LocalDate.of(year, month + 1, day)
                showDatePickerForDueDate = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showDatePickerForPaidDate) {
        val today = LocalDate.now()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val paidDate = LocalDate.of(year, month + 1, day).toString()
                onMarkAsReceived(reminder.copy(note = note), paidDate)
                showDatePickerForPaidDate = false
                onDismiss()
            },
            today.year, today.monthValue - 1, today.dayOfMonth
        ).show()
    }

    if (showNextDueDatePicker) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                nextPartialDueDate = LocalDate.of(year, month + 1, day)
                showNextDueDatePicker = false
            },
            nextPartialDueDate.year,
            nextPartialDueDate.monthValue - 1,
            nextPartialDueDate.dayOfMonth
        ).show()
    }

    // Bottom sheet UI
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Edit Reminder", style = MaterialTheme.typography.titleLarge, color = Color.White)

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", color = Color.White.copy(alpha = 0.7f)) },
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
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount", color = Color.White.copy(alpha = 0.7f)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = dueDate.format(formatter),
                onValueChange = {},
                label = { Text("Due Date", color = Color.White.copy(alpha = 0.7f)) },
                readOnly = true,
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerForDueDate = true },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f),
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedRepeat.value,
                onExpandedChange = { expandedRepeat.value = !expandedRepeat.value }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = recurringType,
                    onValueChange = {},
                    label = { Text("Repeat", color = Color.White.copy(alpha = 0.7f)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedRepeat.value) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedRepeat.value,
                    onDismissRequest = { expandedRepeat.value = false },
                    modifier = Modifier.background(BackgroundDark)
                ) {
                    listOf("None", "Daily", "Weekly", "Monthly").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, color = Color.White) },
                            onClick = {
                                recurringType = type
                                expandedRepeat.value = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedDirection.value,
                onExpandedChange = { expandedDirection.value = !expandedDirection.value }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = if (direction == "TO_PAY") "To Pay" else "To Receive",
                    onValueChange = {},
                    label = { Text("Direction", color = Color.White.copy(alpha = 0.7f)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedDirection.value) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedDirection.value,
                    onDismissRequest = { expandedDirection.value = false },
                    modifier = Modifier.background(BackgroundDark)
                ) {
                    listOf("TO_PAY", "TO_RECEIVE").forEach { dir ->
                        DropdownMenuItem(
                            text = { Text(if (dir == "TO_PAY") "To Pay" else "To Receive", color = Color.White) },
                            onClick = {
                                direction = dir
                                expandedDirection.value = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f),
                    cursorColor = Color.White
                )
            )

            if (isNoteModified || isInfoModified) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        val updated = reminder.copy(
                            name = name.trim(),
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            dueDate = dueDate.toString(),
                            note = note,
                            recurringType = recurringType,
                            direction = direction
                        )
                        onNoteChange(updated)
                        onEditReminder(updated)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(NeonPink, NeonBlue))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (!reminder.isReceived) {
                Button(
                    onClick = { showDatePickerForPaidDate = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, NeonBlue.copy(alpha = 0.5f))
                ) {
                    Text("Mark as Paid", color = NeonBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showPartialPaymentDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f))
                ) {
                    Text("Mark as Partially Paid", color = NeonPink, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        val updated = reminder.copy(
                            isReceived = false,
                            paidDate = null,
                            status = "FUTURE"
                        )
                        onNoteChange(updated)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Text("Mark as Unpaid", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    onDelete(reminder)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF4848)
                ),
                border = BorderStroke(1.dp, Color(0xFFFF4848).copy(alpha = 0.5f))
            ) {
                Text("Delete", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Partial Payment Dialog
    if (showPartialPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPartialPaymentDialog = false },
            title = { Text("Partial Payment") },
            text = {
                Column {
                    OutlinedTextField(
                        value = paidAmount,
                        onValueChange = { paidAmount = it },
                        label = { Text("Amount Paid Today") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nextPartialDueDate.format(formatter),
                        onValueChange = {},
                        label = { Text("Next Due Date") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showNextDueDatePicker = true }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val paid = paidAmount.toDoubleOrNull() ?: 0.0
                        val remaining = reminder.amount - paid

                        // Remove previous partial note (if exists)
                        val baseNote = reminder.note
                            .lineSequence()
                            .filterNot { it.startsWith("Partially paid ₹") }
                            .joinToString("\n")
                            .trim()

                        val updated = reminder.copy(
                            partialAmountPaid = paid,
                            partialDueDate = nextPartialDueDate.toString(),
                            status = "PARTIALLY_PAID",
                            note = if (baseNote.isEmpty())
                                "Partially paid ₹$paid, remaining ₹$remaining"
                            else
                                "$baseNote\nPartially paid ₹$paid, remaining ₹$remaining"
                        )

                        onNoteChange(updated)
                        showPartialPaymentDialog = false
                        onDismiss()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPartialPaymentDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
