package com.work.taskCatalog.controller

import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.model.Task
import com.work.taskCatalog.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Управление задачами")
class TaskController(

    private val taskService: TaskService,

    ) {

    @GetMapping("/{id}")
    @Operation(summary = "Получить задачу по ID")
    fun findById(@PathVariable id: Long): Mono<ResponseEntity<Task>> =
        taskService.findById(id)
            .map {ResponseEntity.ok(it)}
            .defaultIfEmpty(
                ResponseEntity.status( HttpStatus.NOT_FOUND).build()
            )

    @PostMapping
    @Operation(summary = "Создать задачу")
    fun createTask(@Valid @RequestBody request: TaskCreateDto): Mono<ResponseEntity<Task>> =
        taskService.createTask(request)
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }

//    @GetMapping
//    @Operation(summary = "Получить задачи")
//    fun getTasks(
//        @RequestParam page: Int,
//        @RequestParam size: Int,
//        @RequestParam(required = false) status: TaskStatus?
//    ): Mono<ResponseEntity<SliceTaskDto>> =
//        taskService.getTasks(page, size, status)
//            .map { ResponseEntity.ok(it) }
}