package com.work.taskCatalog.service

import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.model.Task
import com.work.taskCatalog.model.TaskStatus
import com.work.taskCatalog.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TaskService  (private val taskRepository: TaskRepository) {
    fun createTask(request: TaskCreateDto): Mono<Task> {
        val status = TaskStatus.NEW
        return taskRepository.save(
            title = request.title,
            description = request.description,
            status = status
        )
    }
}