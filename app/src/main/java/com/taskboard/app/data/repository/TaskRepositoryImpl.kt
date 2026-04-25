package com.taskboard.app.data.repository

import com.taskboard.app.data.local.TaskDao
import com.taskboard.app.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> = dao.observeAll()

    override suspend fun getTaskById(id: Long): Task? {
        return try {
            dao.getById(id)
        } catch (e: Exception) {
            throw IOException("Failed to retrieve task with id=$id", e)
        }
    }

    override suspend fun insertTask(task: Task): Long {
        return try {
            dao.insert(task)
        } catch (e: Exception) {
            throw IOException("Failed to insert task: ${task.title}", e)
        }
    }

    override suspend fun updateTask(task: Task) {
        try {
            dao.update(task)
        } catch (e: Exception) {
            throw IOException("Failed to update task with id=${task.id}", e)
        }
    }

    override suspend fun deleteTask(taskId: Long) {
        try {
            val task = dao.getById(taskId)
                ?: throw IOException("Task with id=$taskId not found for deletion")
            dao.delete(task)
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            throw IOException("Failed to delete task with id=$taskId", e)
        }
    }
}
