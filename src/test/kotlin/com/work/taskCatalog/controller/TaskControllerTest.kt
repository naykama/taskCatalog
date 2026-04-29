package com.work.taskCatalog.controller

import com.work.taskCatalog.dto.SliceTaskDto
import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.dto.TaskDto
import com.work.taskCatalog.dto.UpdateStatusDto
import com.work.taskCatalog.model.Task
import com.work.taskCatalog.model.TaskStatus
import com.work.taskCatalog.service.TaskService
import org.apache.commons.lang3.RandomStringUtils
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.sql.SQLException
import java.time.LocalDateTime
import kotlin.test.Test

@WebFluxTest(controllers = [TaskController::class])
class TaskControllerTest (
) {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var taskService: TaskService


    @Test
    fun createTaskSuccessTest() {
        val request = TaskCreateDto(title = "Test Task", description = "Test Description")
        val savedTask = createTask(1)
        Mockito.`when`(taskService.createTask(request))
            .thenReturn(Mono.just(TaskDto(savedTask)))

        webTestClient.post()
            .uri("/api/tasks")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Task::class.java)
            .isEqualTo(savedTask)
    }

    @Test
    fun createTaskTitleIsNullTest() {
        val request = TaskCreateDto(title = null, description = "Test Description")
        webTestClient.post()
            .uri("/api/tasks")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Validation failed");
    }

    @Test
    fun createTaskTitleHas2SymbolsTest() {
        val request = TaskCreateDto(title = "Hi", description = "Test Description")
        webTestClient.post()
            .uri("/api/tasks")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Validation failed");
    }

    @Test
    fun createTaskTitleHas0SymbolsTest() {
        val request = TaskCreateDto(title = "", description = "Test Description")
        webTestClient.post()
            .uri("/api/tasks")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Validation failed");
    }

    @Test
    fun createTaskTitleHas101SymbolsTest() {
        val request = TaskCreateDto(
            title = RandomStringUtils.insecure().nextAlphabetic(101),
            description = "Test Description"
        )
        webTestClient.post()
            .uri("/api/tasks")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Validation failed");
    }

    @Test
    fun findByIdSuccessTest() {
        val savedTask = createTask(1)
        Mockito.`when`(taskService.findById(savedTask.id))
            .thenReturn(Mono.just(TaskDto(savedTask)))

        webTestClient.get()
            .uri("/api/tasks/${savedTask.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody(Task::class.java)
            .isEqualTo(savedTask)
    }

    @Test
    fun findByIdNotFoundTest() {
        Mockito.`when`(taskService.findById(any()))
            .thenReturn(Mono
                .fromCallable { null as TaskDto? }
                .subscribeOn(Schedulers.boundedElastic()))

        webTestClient.get()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun findTasksSuccessTest() {
        val page = 1
        val size = 2
        val savedTask = createTask(1)
        Mockito.`when`(taskService.findTasks(page, size, TaskStatus.NEW))
            .thenReturn(Mono
                .fromCallable { SliceTaskDto(listOf(TaskDto(savedTask)), 1, 2, 2, 1) }
                .subscribeOn(Schedulers.boundedElastic()))

        webTestClient.get()
            .uri("/api/tasks?page=$page&size=$size&status=NEW")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun findTasksNotFoundTest() {
        val page = 2
        val size = 2
        Mockito.`when`(taskService.findTasks(page, size, TaskStatus.NEW))
            .thenReturn(Mono
                .fromCallable { null as SliceTaskDto? }
                .subscribeOn(Schedulers.boundedElastic()))

        webTestClient.get()
            .uri("/api/tasks?page=$page&size=$size&status=NEW")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun updateTaskNotCorrectStatusTest() {
        webTestClient.patch()
            .uri("/api/tasks/1")
            .body(BodyInserters.fromValue(UpdateStatusDto("False_status")))
            .exchange()
            .expectStatus().isBadRequest()
    }

    @Test
    fun updateTaskNotFoundTest() {
        val id = 1L
        val status = TaskStatus.CANCELLED
        Mockito.`when`(taskService.updateStatus(id, status))
            .thenReturn(Mono
                .fromCallable { null as TaskDto? }
                .subscribeOn(Schedulers.boundedElastic()))

        webTestClient.patch()
            .uri("/api/tasks/$id/status")
            .body(BodyInserters.fromValue(UpdateStatusDto(status.name)))
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    fun updateTaskSuccessTest() {
        val id = 1L
        val status = TaskStatus.CANCELLED
        Mockito.`when`(taskService.updateStatus(id, status))
            .thenReturn(Mono
                .fromCallable { TaskDto(createTask(id)) }
                .subscribeOn(Schedulers.boundedElastic()))

        webTestClient.patch()
            .uri("/api/tasks/$id/status")
            .body(BodyInserters.fromValue(UpdateStatusDto(status.name)))
            .exchange()
            .expectStatus().isOk()
    }

    @Test
    fun deleteNotFoundTest() {
        val id = 1L
        Mockito.`when`(taskService.deleteById(id))
            .thenReturn(Mono
                .fromCallable { 0 }
                .subscribeOn(Schedulers.boundedElastic()))

        webTestClient.delete()
            .uri("/api/tasks/$id")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun deleteSuccessTest() {
        val id = 1L
        Mockito.`when`(taskService.deleteById(id))
            .thenReturn(Mono
                .fromCallable { 1 }
                .subscribeOn(Schedulers.boundedElastic()))

        webTestClient.delete()
            .uri("/api/tasks/$id")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun internalErrorTest() {
        val id = 1L
        Mockito.`when`(taskService.deleteById(id))
            .thenReturn(Mono.error(RuntimeException("Internal error")))

        webTestClient.delete()
            .uri("/api/tasks/$id")
            .exchange()
            .expectStatus().isEqualTo(500)
    }

    @Test
    fun databaseErrorTest() {
        val id = 1L
        Mockito.`when`(taskService.deleteById(id))
            .thenReturn(Mono.error(SQLException("database error")))

        webTestClient.delete()
            .uri("/api/tasks/$id")
            .exchange()
            .expectStatus().isEqualTo(503)
    }

    private fun createTask(id: Long): Task =
    Task(
        id = id,
        title = "title_$id",
        description = "description_$id",
        status = TaskStatus.NEW,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}