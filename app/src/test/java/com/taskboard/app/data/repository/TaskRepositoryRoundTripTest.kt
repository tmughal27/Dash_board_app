// Feature: android-task-board, Property 4: Task field round-trip through storage
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
 * Validates: Requirements 2.4, 2.5, 2.6, 6.1, 6.2
 */
@Config(manifest = Config.NONE)
class TaskRepositoryRoundTripTest : StringSpec({

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

    // Property 4: Task field round-trip through storage
    "inserted task retrieved by ID has identical fields" {
        val arbTask = Arb.bind(
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

        checkAll(iterations = 100, arbTask) { task ->
            val insertedId = repo.insertTask(task)
            val retrieved = repo.getTaskById(insertedId)

            retrieved shouldNotBe null
            retrieved!!.title shouldBe task.title
            retrieved.description shouldBe task.description
            retrieved.priority shouldBe task.priority
            retrieved.status shouldBe task.status
            retrieved.dueDate shouldBe task.dueDate
            retrieved.notificationsEnabled shouldBe task.notificationsEnabled
        }
    }
})
