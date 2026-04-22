package com.work.taskCatalog.dto

data class SliceTaskDto(
    val content: List<TaskDto>?,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)