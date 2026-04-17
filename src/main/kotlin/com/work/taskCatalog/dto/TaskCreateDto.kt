package com.work.taskCatalog.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class TaskCreateDto (

    @field:NotNull(message = "Title cannot be null")
    @field:Size(min = 3, max = 100, message = "Title must be 3-100 characters")
    val title: String?,

    val description:  String? = null,
)