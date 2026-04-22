package com.work.taskCatalog.dto

import com.work.taskCatalog.model.Task
import com.work.taskCatalog.model.TaskStatus
import java.time.LocalDateTime

data class TaskDto (
    val id: Long,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    constructor(task: Task) : this(task.id, task.title, task.description, task.status, task.createdAt, task.updatedAt)
}