package com.work.taskCatalog.service

import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.model.Task
import com.work.taskCatalog.model.TaskStatus
import com.work.taskCatalog.repository.TaskRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import java.time.LocalDateTime
import kotlin.test.assertNull

class TaskServiceTest (
) {

    @Mock
    private lateinit var taskRepository: TaskRepository

    @InjectMocks
    private lateinit var taskService: TaskService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun createTaskTest() {
        val request = TaskCreateDto(title = "Test Task", description = "Test Description")
        val savedTask = initTask(request.title!!, request.description)
        whenever(taskRepository.save(request.title, request.description))
            .thenReturn(Mono.just(savedTask))

        val result = taskService.createTask(request).block()
        assertNotNull(result)
        assertEquals(request.title, result.title)
        assertEquals(request.description, result.description)
        assertEquals(savedTask.id, result.id)
    }


    @Test
    fun findByIdSuccessTest() {
        val savedTask = initTask(title = "Test Task", description = "Test Description")
        whenever(taskRepository.findById(savedTask.id))
            .thenReturn(Mono.just(savedTask))

        val result = taskService.findById(savedTask.id).block()
        assertNotNull(result)
        assertEquals(savedTask.title, result.title)
        assertEquals(savedTask.description, result.description)
        assertEquals(savedTask.status, result.status)
        assertEquals(savedTask.createdAt, result.createdAt)
        assertEquals(savedTask.updatedAt, result.updatedAt)
        assertEquals(savedTask.id, result.id)
    }

    @Test
    fun findByIdNotFoundTest() {
        whenever(taskRepository.findById(any()))
            .thenReturn(Mono
                .fromCallable { null as Task? }
                .subscribeOn(Schedulers.boundedElastic()))

        val result = taskService.findById(1)
        StepVerifier.create(result)
            .expectNextCount(0)  // Ожидаем 0 элементов
            .verifyComplete()
        assertNull(result.block())
    }

    private fun initTask(title: String, description: String?): Task {
        val savedTask = Task(
            id = 1,
            title = title,
            description = description,
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return savedTask
    }


}


