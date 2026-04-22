package com.work.taskCatalog.service

import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.model.Task
import com.work.taskCatalog.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TaskService(private val taskRepository: TaskRepository) {

    fun createTask(request: TaskCreateDto): Mono<Task> {
        requireNotNull(request.title)
        return taskRepository.save(request.title, request.description)
    }

    fun findById(id: Long): Mono<Task> {
        return taskRepository.findById(id)
    }

//    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<SliceTaskDto> {
//        return taskRepository.findAll(page, size, status)
//    }
}