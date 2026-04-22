package com.work.taskCatalog.repository

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

    fun save(title: String, description: String?): Mono<Task> = Mono
        .fromCallable { executeInsert(title, description) }
        .subscribeOn(Schedulers.boundedElastic())

        fun findById(id: Long): Mono<Task> = Mono
        .fromCallable { executeFindById(id) }          // Блокирующий вызов
        .subscribeOn(Schedulers.boundedElastic())

    private fun executeInsert(title: String, description: String?): Task {
        val now = LocalDateTime.now()
        val status = TaskStatus.NEW
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

//    private fun executeFindAll(page: Int, size: Int, status: TaskStatus?): SliceTaskDto {
//            jdbcClient.sql(
//                """
//                    SELECT * FROM tasks
//                    WHERE id = ?
//                """
//
//            )
//    }

}