package com.example.studenttaskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.studenttaskmanager.ui.theme.StudentTaskManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentTaskManagerTheme {
                var currentScreen by remember { mutableStateOf("dashboard") }
                var editingTask by remember { mutableStateOf<Task?>(null) }
                var showEditDialog by remember { mutableStateOf(false) }

                if (currentScreen == "dashboard") {
                    DashboardScreen(
                        tasks = sampleTasks,
                        onEditTask = {
                            editingTask = it
                            showEditDialog = true
                        },
                        onAddTask = {
                            editingTask = null
                            showEditDialog = true
                        },
                        onNavigateToCalendar = { currentScreen = "calendar" }
                    )
                } else {
                    CalendarScreen(
                        tasks = sampleTasks,
                        onTaskClick = {
                            editingTask = it
                            showEditDialog = true
                        },
                        onNavigateBack = { currentScreen = "dashboard" }
                    )
                }

                if (showEditDialog) {
                    TaskEditDialog(
                        task = editingTask,
                        onDismiss = { showEditDialog = false },
                        onConfirm = { updatedTask ->
                            val index = sampleTasks.indexOfFirst { it.id == updatedTask.id }
                            if (index != -1) {
                                sampleTasks[index] = updatedTask
                            } else {
                                sampleTasks.add(updatedTask)
                            }
                            showEditDialog = false
                        }
                    )
                }
            }
        }
    }
}
