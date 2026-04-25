package com.taskboard.app.domain.model

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<ValidationError>) : ValidationResult()
}

enum class ValidationError { TITLE_EMPTY, TITLE_TOO_LONG, DESCRIPTION_TOO_LONG }
