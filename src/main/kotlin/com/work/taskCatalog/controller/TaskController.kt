package com.work.taskCatalog.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Управление задачами")
class TaskController {
    @GetMapping
    @Operation(summary = "Получить все задачи")
//    fun getAll(): Flux<Task> = ...
    fun getAll(): String = "Hello"
//    @GetMapping("/{id}")
//    @Operation(summary = "Получить задачу по ID")
//    fun getById(@PathVariable id: Long): Mono<Task> = ...
//    @PostMapping
//    @Operation(summary = "Создать задачу")
//    fun create(@RequestBody task: Task): Mono<Task> = ...
}