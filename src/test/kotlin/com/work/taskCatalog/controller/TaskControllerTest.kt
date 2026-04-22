package com.work.taskCatalog.controller

import com.work.taskCatalog.dto.TaskCreateDto
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
        val savedTask = Task(
            id = 1,
            title = request.title!!,
            description = request.description,
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        Mockito.`when`(taskService.createTask(request))
            .thenReturn(Mono.just(savedTask))

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
        val savedTask = Task(
            id = 1,
            title = "title",
            description = "description",
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        Mockito.`when`(taskService.findById(savedTask.id))
            .thenReturn(Mono.just(savedTask))

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
                .fromCallable { null as Task? }
                .subscribeOn(Schedulers.boundedElastic()))

        webTestClient.get()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNotFound
    }
}