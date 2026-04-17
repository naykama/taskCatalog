package com.work.taskCatalog.repository

import com.work.taskCatalog.model.Task
import com.work.taskCatalog.model.TaskStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.simple.JdbcClient

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class TaskRepository(@Autowired private val jdbcClient: JdbcClient) {

    fun save(title: String, description: String?, status: TaskStatus): Mono<Task> = Mono
        .fromCallable { executeInsert(title, description, status) }
        .subscribeOn(Schedulers.boundedElastic())

    private fun executeInsert(
        title: String,
        description: String?,
        status: TaskStatus
    ): Task {
        val now = LocalDateTime.now()
        val id = jdbcClient.sql("""
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """)
            .params(listOf(title, description, status.name, now, now))
            .query(Long::class.java)
            .single()
        return Task(
            id = id,
            title = title,
            description = description,
            status = status,
            createdAt = now,
            updatedAt = now
        )
    }


}