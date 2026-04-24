package com.work.taskCatalog.service

import com.work.taskCatalog.dto.SliceTaskDto
import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.dto.TaskDto
import com.work.taskCatalog.model.TaskStatus
import com.work.taskCatalog.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TaskService(private val taskRepository: TaskRepository) {

    fun createTask(request: TaskCreateDto): Mono<TaskDto> {
        return taskRepository.save(request)
    }

    fun findById(id: Long): Mono<TaskDto> {
        return taskRepository.findById(id)
    }

    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<SliceTaskDto> {
        return taskRepository.findAll(page, size, status)
    }

    fun updateStatus(id: Long, newStatus: TaskStatus): Mono<TaskDto> {
        return taskRepository.updateStatus(id, newStatus)
    }

    fun deleteById(id: Long): Mono<Int> {
        return taskRepository.deleteById(id)
    }
}