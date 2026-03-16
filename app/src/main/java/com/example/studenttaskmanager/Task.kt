package com.example.studenttaskmanager

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import com.example.studenttaskmanager.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class Priority {
    Low, Medium, High
}

data class Task(
    val id: Int,
    var title: String,
    var subject: String,
    var deadline: LocalDateTime,
    var priority: Priority,
    var isCompleted: Boolean = false,
    var subjectColor: Color = PastelBlue,
    var reminderMinutesBefore: Int? = null,
    var description: String = ""
) {
    val formattedDeadline: String
        get() = deadline.format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a"))
}

val sampleTasks = mutableStateListOf(
    Task(1, "Mathematics Assignment", "Math", LocalDateTime.now().plusHours(5), Priority.High, false, PastelPurple, 60),
    Task(2, "History Essay", "History", LocalDateTime.now().plusDays(1), Priority.Medium, false, PastelOrange, 30),
    Task(3, "Physics Lab Report", "Physics", LocalDateTime.now().plusDays(3), Priority.Low, false, PastelBlue),
    Task(4, "Chemistry Quiz", "Chemistry", LocalDateTime.now().minusDays(1), Priority.Medium, true, PastelGreen)
)
