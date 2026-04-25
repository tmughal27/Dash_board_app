package com.taskboard.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val dueDate: Long? = null,
    val notificationsEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
