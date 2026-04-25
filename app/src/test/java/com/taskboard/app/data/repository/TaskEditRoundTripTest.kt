// Feature: android-task-board, Property 5: Task edit round-trip
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
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.robolectric.annotation.Config

/**
 * Validates: Requirements 4.2
 */
@Config(manifest = Config.NONE)
class TaskEditRoundTripTest : StringSpec({

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

    // Property 5: Task edit round-trip
    "updated task retrieved by ID has the edited field values" {
        checkAll(iterations = 100, arbTask(), arbTask()) { original, edited ->
            val insertedId = repo.insertTask(original)
            val updatedTask = edited.copy(id = insertedId)
            repo.updateTask(updatedTask)

            val retrieved = repo.getTaskById(insertedId)

            retrieved shouldNotBe null
            retrieved!!.title shouldBe edited.title
            retrieved.description shouldBe edited.description
            retrieved.priority shouldBe edited.priority
            retrieved.status shouldBe edited.status
            retrieved.dueDate shouldBe edited.dueDate
            retrieved.notificationsEnabled shouldBe edited.notificationsEnabled
        }
    }
})
