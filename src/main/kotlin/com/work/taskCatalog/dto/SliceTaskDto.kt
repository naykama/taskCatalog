package com.work.taskCatalog.dto

class SliceTaskDto(
    val content: MutableList<TaskDto?>?,
    val size: Int,
    val number: Int,
    var isNext: Boolean,
) {
    fun hasPrevious(): Boolean = number > 0

    fun hasNext(): Boolean = isNext
}