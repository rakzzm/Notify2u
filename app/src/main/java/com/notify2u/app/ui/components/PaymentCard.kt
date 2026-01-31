package com.notify2u.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

    val neonColor = when {
        !reminder.isReceived && daysLeft < 0 -> Color(0xFFFF4848) // Neon Red/Pink
        !reminder.isReceived && daysLeft == 0L -> Color(0xFFFF00E5) // Neon Pink
        !reminder.isReceived && daysLeft in 1..3 -> Color(0xFFFF8C00) // Neon Orange
        !reminder.isReceived -> Color(0xFF00E0FF) // Neon Blue
        else -> Color(0xFFB0B0B0)
    }

    val paymentDirection = when (reminder.direction) {
        "TO_PAY" -> "To Pay"
        "TO_RECEIVE" -> "To Receive"
        else -> ""
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, neonColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable { onClick(reminder) },
        color = Color.White.copy(alpha = 0.05f),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reminder.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                // Status Glow
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(neonColor, CircleShape)
                        .border(2.dp, neonColor.copy(alpha = 0.3f), CircleShape)
                )
            }

            extraLabel?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it, 
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "₹${reminder.amount}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = Brush.linearGradient(listOf(Color(0xFFFF00E5), Color(0xFF00E0FF)))
                ),
                color = Color.White
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
                    Color(0xFFFF4848) 
                else 
                    Color.White.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Repeat: ${reminder.recurringType}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Text(
                    text = paymentDirection,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            if (reminder.note.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = reminder.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.4f),
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