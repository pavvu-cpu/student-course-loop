package com.example.studenttaskmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studenttaskmanager.ui.theme.*
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    tasks: List<Task>,
    onEditTask: (Task) -> Unit,
    onAddTask: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }

    val filteredTasks = tasks.filter {
        it.title.contains(searchQuery, ignoreCase = true) || 
        it.subject.contains(searchQuery, ignoreCase = true)
    }.filter {
        when (selectedFilter) {
            "High Priority" -> it.priority == Priority.High
            "Medium Priority" -> it.priority == Priority.Medium
            "Low Priority" -> it.priority == Priority.Low
            else -> true
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(onCalendarClick = onNavigateToCalendar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = Primary,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DashboardHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onFilterClick = { showFilterMenu = true }
            )

            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    listOf("All", "High Priority", "Medium Priority", "Low Priority").forEach { filter ->
                        DropdownMenuItem(
                            text = { Text(filter) },
                            onClick = {
                                selectedFilter = filter
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { GreetingSection() }
                item { ProgressTracker(tasks) }
                
                if (searchQuery.isEmpty() && selectedFilter == "All") {
                    item { SectionHeader(title = "Today's tasks") }
                    items(filteredTasks.filter { !it.isCompleted && it.deadline.toLocalDate() == LocalDateTime.now().toLocalDate() }) { task ->
                        TaskCard(task = task, onClick = { onEditTask(task) })
                    }

                    item { SectionHeader(title = "Upcoming assignments") }
                    items(filteredTasks.filter { !it.isCompleted && it.deadline.toLocalDate() > LocalDateTime.now().toLocalDate() }) { task ->
                        TaskCard(task = task, onClick = { onEditTask(task) })
                    }
                } else {
                    item { SectionHeader(title = "Results") }
                    items(filteredTasks) { task ->
                        TaskCard(task = task, onClick = { onEditTask(task) })
                    }
                }

                item { SectionHeader(title = "Completed tasks") }
                items(filteredTasks.filter { it.isCompleted }) { task ->
                    TaskCard(task = task, onClick = { onEditTask(task) })
                }
                
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = { Text("Search tasks...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(56.dp)
                .background(Surface, RoundedCornerShape(16.dp))
        ) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Primary)
        }
    }
}

@Composable
fun GreetingSection() {
    Column {
        Text(
            text = "Hello, Alex!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Stay productive today.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
    }
}

@Composable
fun ProgressTracker(tasks: List<Task>) {
    val completed = tasks.count { it.isCompleted }
    val total = tasks.size
    val progress = if (total > 0) completed.toFloat() / total else 0f
    
    val gradient = Brush.horizontalGradient(
        colors = listOf(Primary, Color(0xFF9D8BFF))
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Weekly Progress",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% of tasks completed",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        TextButton(onClick = { }) {
            Text(
                text = "See all",
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(task.subjectColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = task.subject.take(1),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = task.formattedDeadline,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    PriorityBadge(task.priority)
                }
            }
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { task.isCompleted = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Primary,
                    uncheckedColor = TextSecondary.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val color = when (priority) {
        Priority.High -> Color(0xFFFFEBEE)
        Priority.Medium -> Color(0xFFFFF3E0)
        Priority.Low -> Color(0xFFE8F5E9)
    }
    val textColor = when (priority) {
        Priority.High -> Color(0xFFD32F2F)
        Priority.Medium -> Color(0xFFF57C00)
        Priority.Low -> Color(0xFF388E3C)
    }
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = priority.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun BottomNavigationBar(onCalendarClick: () -> Unit = {}, onHomeClick: () -> Unit = {}) {
    NavigationBar(
        containerColor = Surface,
        tonalElevation = 0.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        val items = listOf(
            NavigationItem("Home", Icons.Default.Home, onHomeClick),
            NavigationItem("Tasks", Icons.AutoMirrored.Filled.List, {}),
            NavigationItem("Calendar", Icons.Default.CalendarMonth, onCalendarClick),
            NavigationItem("Profile", Icons.Default.Person, {})
        )
        items.forEach { item ->
            NavigationBarItem(
                selected = item.title == "Home",
                onClick = item.onClick,
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Secondary
                )
            )
        }
    }
}

data class NavigationItem(val title: String, val icon: ImageVector, val onClick: () -> Unit)
