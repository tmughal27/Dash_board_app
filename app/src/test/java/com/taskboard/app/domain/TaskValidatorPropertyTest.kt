// Feature: android-task-board, Property 3: Task form validation accepts only valid inputs
package com.taskboard.app.domain

import com.taskboard.app.data.model.Priority
import com.taskboard.app.domain.model.TaskForm
import com.taskboard.app.domain.model.ValidationError
import com.taskboard.app.domain.model.ValidationResult
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * Validates: Requirements 2.2, 2.3, 2.7
 */
class TaskValidatorPropertyTest : StringSpec({

    // Property 3: Form validation accepts only valid inputs
    "validator accepts form iff title length in 1..100 and description length <= 500" {
        checkAll(iterations = 100, Arb.string(), Arb.string()) { title, description ->
            val form = TaskForm(
                title = title,
                description = description,
                priority = Priority.MEDIUM,
                dueDate = null,
                notificationsEnabled = false
            )
            val result = TaskValidator.validate(form)
            val isValid = title.length in 1..100 && description.length <= 500

            if (isValid) {
                result shouldBe ValidationResult.Valid
            } else {
                result.shouldBeInstanceOf<ValidationResult.Invalid>()
                val errors = (result as ValidationResult.Invalid).errors

                if (title.isEmpty()) {
                    errors.contains(ValidationError.TITLE_EMPTY) shouldBe true
                }
                if (title.length > 100) {
                    errors.contains(ValidationError.TITLE_TOO_LONG) shouldBe true
                }
                if (description.length > 500) {
                    errors.contains(ValidationError.DESCRIPTION_TOO_LONG) shouldBe true
                }
            }
        }
    }
})
