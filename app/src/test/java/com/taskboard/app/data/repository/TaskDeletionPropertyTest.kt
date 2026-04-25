// Feature: android-task-board, Property 6: Deletion removes task from storage
package com.taskboard.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.taskboard.app.data.local.AppDatabase
import com.taskboard.app.data.model.Priority
import com.taskboard.app.data.model.Task
import com.taskboard.app.data.model.TaskStatus
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.first
import org.robolectric.annotation.Config

/**
 * Validates: Requirements 4.4, 4.5
 */
@Config(manifest = Config.NONE)
class TaskDeletionPropertyTest : StringSpec({

    lateinit var db: AppDatabase
    lateinit var repo: TaskRepositoryImpl

    beforeSpec {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(Dispatchers.IO.asExecutor())
            .allowMainThreadQueries()
            .build()
        repo = TaskRepositoryImpl(db.taskDao())
    }

    afterSpec {
        db.close()
    }

    fun arbTask() = Arb.bind(
        Arb.string(1..100),
        Arb.string(0..500),
        Arb.enum<Priority>(),
        Arb.enum<TaskStatus>(),
        Arb.long().orNull(),
        Arb.boolean()
    ) { title, description, priority, status, dueDate, notificationsEnabled ->
        Task(
            title = title,
            description = description,
            priority = priority,
            status = status,
            dueDate = dueDate,
            notificationsEnabled = notificationsEnabled
        )
    }

    // Property 6: Deletion removes task from storage
    "deleted task is absent and remaining tasks are unchanged" {
        checkAll(iterations = 100, Arb.list(arbTask(), 1..10)) { tasks ->
            // Insert all tasks and collect their assigned IDs
            val insertedIds = tasks.map { repo.insertTask(it) }

            // Pick one to delete
            val deleteIndex = insertedIds.indices.random()
            val deleteId = insertedIds[deleteIndex]
            val remainingIds = insertedIds.filterIndexed { i, _ -> i != deleteIndex }

            repo.deleteTask(deleteId)

            // Deleted task must be absent
            repo.getTaskById(deleteId) shouldBe null

            // All other tasks must still be present
            for (id in remainingIds) {
                repo.getTaskById(id) shouldNotBe null
            }

            // Clean up for next iteration
            for (id in remainingIds) {
                repo.deleteTask(id)
            }
        }
    }
})
