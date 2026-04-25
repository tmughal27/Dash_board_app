package com.taskboard.app.domain.model

import com.taskboard.app.data.model.Task
import com.taskboard.app.data.model.TaskStatus

data class BoardState(
    val columns: Map<TaskStatus, List<Task>>,
    val taskCounts: Map<TaskStatus, Int>
)
