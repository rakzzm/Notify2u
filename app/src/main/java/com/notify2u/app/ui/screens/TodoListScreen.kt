package com.notify2u.app.ui.screens
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Brush
import com.notify2u.app.data.local.TodoTaskEntity
import com.notify2u.app.data.local.Priority
import com.notify2u.app.ui.components.AddTodoBottomSheet
import com.notify2u.app.ui.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel
) {
    val tasks by viewModel.allTasks.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    var quickAddTaskText by remember { mutableStateOf("") }

    val categories = listOf("ALL", "GROCERIES", "HOLIDAY", "DIY", "PERSONAL")
    val labels = listOf("All", "Groceries", "Holiday", "DIY", "Personal")
    val filteredTasks = if (categories[selectedTabIndex] == "ALL") tasks else tasks.filter { it.category.uppercase() == categories[selectedTabIndex] }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "To-Do List",
                    style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                
                Spacer(Modifier.height(16.dp))
                
                ScrollableRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    categories.forEachIndexed { index, category ->
                        val isSelected = selectedTabIndex == index
                        val count = if (category == "ALL") tasks.size else tasks.count { it.category.uppercase() == category }
                        
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            label = { 
                                Text(
                                    if (category == "ALL") "All" else "${labels[index]} ($count)",
                                    style = MaterialTheme.typography.labelLarge
                                ) 
                            },
                            modifier = Modifier.padding(end = 8.dp),
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = Color.White.copy(alpha = 0.7f)
                            ),
                            border = if (isSelected) FilterChipDefaults.filterChipBorder(enabled = true, selected = true, borderColor = Color.White.copy(alpha = 0.5f)) else null
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                modifier = Modifier.imePadding(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = quickAddTaskText,
                        onValueChange = { quickAddTaskText = it },
                        placeholder = { Text("Add a new task...", color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White
                        ),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (quickAddTaskText.isNotBlank()) {
                                viewModel.insertTask(TodoTaskEntity(title = quickAddTaskText, category = categories[selectedTabIndex]))
                                quickAddTaskText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .background(Brush.linearGradient(listOf(Color(0xFFFF00E5), Color(0xFF00E0FF))), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredTasks, key = { it.id }) { task ->
                TaskItem(task, viewModel)
            }
        }
    }
}

@Composable
fun ScrollableRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun TaskItem(task: TodoTaskEntity, viewModel: TodoViewModel) {
    val neonColor = if (task.isCompleted) Color(0xFF00E0FF) else Color(0xFFFF00E5)
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .border(
                1.dp, 
                if (task.isCompleted) Color.White.copy(alpha = 0.1f) else neonColor.copy(alpha = 0.4f), 
                RoundedCornerShape(20.dp)
            ),
        onClick = { viewModel.toggleTaskCompletion(task) },
        color = if (task.isCompleted) Color.White.copy(alpha = 0.02f) else Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Checkbox
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(
                        2.dp, 
                        if (task.isCompleted) Color(0xFF00E0FF) else Color.White.copy(alpha = 0.3f),
                        CircleShape
                    )
                    .background(
                        if (task.isCompleted) Color(0xFF00E0FF) else Color.Transparent,
                        CircleShape
                    )
                    .clickable { viewModel.toggleTaskCompletion(task) },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted) Color.White.copy(alpha = 0.4f) else Color.White
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 2
                    )
                }
                if (task.priority == Priority.HIGH) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFFF4848).copy(alpha = 0.15f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, Color(0xFFFF4848).copy(alpha = 0.5f))
                    ) {
                        Text(
                            "High Priority",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF4848)
                        )
                    }
                }
            }
        }
    }
}
