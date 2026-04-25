package com.taskboard.app.domain

import com.taskboard.app.domain.model.TaskForm
import com.taskboard.app.domain.model.ValidationError
import com.taskboard.app.domain.model.ValidationResult

object TaskValidator {

    fun validate(form: TaskForm): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        when {
            form.title.isEmpty() -> errors.add(ValidationError.TITLE_EMPTY)
            form.title.length > 100 -> errors.add(ValidationError.TITLE_TOO_LONG)
        }

        if (form.description.length > 500) {
            errors.add(ValidationError.DESCRIPTION_TOO_LONG)
        }

        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}
