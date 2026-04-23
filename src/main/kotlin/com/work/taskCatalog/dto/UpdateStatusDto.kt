package com.work.taskCatalog.dto

import com.work.taskCatalog.annotation.ValueOfEnum
import com.work.taskCatalog.model.TaskStatus
import jakarta.validation.constraints.NotNull

data class UpdateStatusDto (
    @NotNull
    @ValueOfEnum(enumClass = TaskStatus::class)
    val status: String?
)