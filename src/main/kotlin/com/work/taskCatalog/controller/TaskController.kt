package com.work.taskCatalog.controller

import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.model.Task
import com.work.taskCatalog.repository.TaskRepository
import com.work.taskCatalog.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Управление задачами")
class TaskController(

    private val taskService: TaskService,

    ) {

    @GetMapping
    @Operation(summary = "Получить все задачи")
//    fun getAll(): Flux<Task> = ...
    fun getAll(): String = "Hello"
//    @GetMapping("/{id}")
//    @Operation(summary = "Получить задачу по ID")
//    fun getById(@PathVariable id: Long): Mono<Task> = ...

    @PostMapping
    @Operation(summary = "Создать задачу")
    fun createTask(@Valid @RequestBody request: TaskCreateDto): Mono<ResponseEntity<Task>> =
        taskService.createTask(request)
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
}