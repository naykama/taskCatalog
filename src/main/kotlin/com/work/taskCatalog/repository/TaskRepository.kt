package com.work.taskCatalog.repository

import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.model.Task
import com.work.taskCatalog.model.TaskStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Repository
class TaskRepository(@Autowired private val jdbcClient: JdbcClient) {

    fun save(title: String, description: String?, status: TaskStatus): Mono<Task> = Mono
        .fromCallable { executeInsert(title, description, status) }
        .subscribeOn(Schedulers.boundedElastic())

    fun findById(id: Long): Mono<Task> = Mono
        .fromCallable { executeFindById(id) }          // Блокирующий вызов
        .subscribeOn(Schedulers.boundedElastic())

    private fun executeInsert(title: String, description: String?, status: TaskStatus): Task {
        val now = LocalDateTime.now()
        val id = jdbcClient.sql(
        """
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """
        )
            .params(listOf(title, description, status.name, now, now))
            .query(Long::class.java)
            .single()
        return Task(id, title, description, status, now, now)
    }

    private fun executeFindById(id: Long): Task? =
        jdbcClient.sql(
            "SELECT * FROM tasks WHERE id = ?",
            )
            .params(id)
            .query(Task::class.java)
            .optional().getOrNull()


}