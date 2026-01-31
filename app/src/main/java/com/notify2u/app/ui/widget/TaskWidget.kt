package com.notify2u.app.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.compose.ui.unit.dp
import com.notify2u.app.data.local.Notify2uDatabase
import kotlinx.coroutines.flow.first

class TaskWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = Notify2uDatabase.getInstance(context)
        val tasks = database.todoDao().getAllTasks().first().take(3)

        provideContent {
            WidgetContent(tasks.map { it.title })
        }
    }

    @Composable
    private fun WidgetContent(tasks: List<String>) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = "Notify2u Tasks",
                style = TextStyle(fontWeight = androidx.glance.text.FontWeight.Bold)
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            if (tasks.isEmpty()) {
                Text(text = "No pending tasks")
            } else {
                tasks.forEach { task ->
                    Text(text = "• $task", maxLines = 1)
                }
            }
        }
    }
}

class TaskWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskWidget()
}
