package com.notify2u.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notify2u.app.data.local.PaymentReminderEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun PaymentCard(
    reminder: PaymentReminderEntity,
    onClick: (PaymentReminderEntity) -> Unit,
    cardColor: Color? = null,
    extraLabel: String? = null
) {
    val dueDate = LocalDate.parse(reminder.dueDate)
    val today = LocalDate.now()
    val daysLeft = ChronoUnit.DAYS.between(today, dueDate)

    val defaultColor = when {
        !reminder.isReceived && daysLeft < 0 -> Color(0xFFB71C1C)
        !reminder.isReceived && daysLeft == 0L -> Color(0xFFE53935)
        !reminder.isReceived && daysLeft in 1..3 -> Color(0xFFFB8C00)
        !reminder.isReceived -> Color(0xFF43A047)
        else -> Color(0xFFBDBDBD)
    }

    val actualColor = cardColor ?: defaultColor

    val paymentDirection = when (reminder.direction) {
        "TO_PAY" -> "To Pay"
        "TO_RECEIVE" -> "To Receive"
        else -> ""
    }

    IOSCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(reminder) },
        backgroundColor = actualColor,
        elevation = 4.dp
    ) {
        Column {
            Text(
                text = reminder.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            extraLabel?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it, 
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "₹${reminder.amount}",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            val formattedDueDate = dueDate.format(formatter)

            val statusText = when {
                reminder.isReceived -> {
                    val formattedPaidDate = reminder.paidDate?.let {
                        LocalDate.parse(it).format(formatter)
                    } ?: "Unknown"
                    "Received on $formattedPaidDate"
                }
                reminder.status == "PARTIALLY_PAID" -> {
                    val paid = reminder.partialAmountPaid ?: 0.0
                    "Partially paid: ₹$paid"
                }
                dueDate.isBefore(today) -> "Overdue - Due: $formattedDueDate"
                else -> "Pending - Due: $formattedDueDate"
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyLarge,
                color = if (dueDate.isBefore(today) && !reminder.isReceived) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Repeat: ${reminder.recurringType}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = paymentDirection,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (reminder.note.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = reminder.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentCardPreview() {
    val dummyReminder = PaymentReminderEntity(
        id = 1,
        name = "Test Payment",
        amount = 2500.0,
        dueDate = LocalDate.now().plusDays(-1).toString(),
        isReceived = false,
        status = "PARTIALLY_PAID",
        partialAmountPaid = 500.0,
        partialDueDate = LocalDate.now().plusDays(7).toString(),
        personName = "John Doe",
        month = "JULY 2025",
        recurringType = "Monthly",
        direction = "TO_PAY",
        note = "This is a test note"
    )

    MaterialTheme {
        PaymentCard(reminder = dummyReminder, onClick = {})
    }
}