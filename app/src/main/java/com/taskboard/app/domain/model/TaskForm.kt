package com.taskboard.app.domain.model

import com.taskboard.app.data.model.Priority

data class TaskForm(
    val title: String,
    val description: String,
    val priority: Priority,
    val dueDate: Long?,
    val notificationsEnabled: Boolean
)
