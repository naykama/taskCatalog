package com.work.taskCatalog.repository

import com.work.taskCatalog.dto.SliceTaskDto
import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.dto.TaskDto
import com.work.taskCatalog.model.Task
import com.work.taskCatalog.model.TaskStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime

@Repository
class TaskRepository(@Autowired private val jdbcClient: JdbcClient) {

    data class TaskWithTotal(
        val task: Task,
        val totalCount: Long
    )

    fun save(dto: TaskCreateDto): Mono<TaskDto> = Mono
        .fromCallable { executeInsert(dto.title!!, dto.description) }
        .subscribeOn(Schedulers.boundedElastic())

        fun findById(id: Long): Mono<TaskDto> = Mono
        .fromCallable { executeFindById(id) }          // Блокирующий вызов
        .subscribeOn(Schedulers.boundedElastic())

    fun findAll(page: Int, size: Int, status: TaskStatus?): Mono<SliceTaskDto> = Mono
        .fromCallable { executeFindAll(page, size, status) }
        .subscribeOn(Schedulers.boundedElastic())

    fun updateStatus(id: Long, newStatus: TaskStatus): Mono<TaskDto> {
        val taskDto = executeUpdateStatus(id, newStatus)
        return Mono
            .fromCallable { taskDto }
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun deleteById(id: Long): Mono<Int> = Mono
        .fromCallable { executeDeleteById(id) }
        .subscribeOn(Schedulers.boundedElastic())

    private fun executeInsert(title: String, description: String?): TaskDto {
        val now = LocalDateTime.now()
        val status = TaskStatus.NEW
        val id = jdbcClient.sql(
            """
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """.trimIndent()
        )
            .params(listOf(title, description, status.name, now, now))
            .query(Long::class.java)
            .single()
        val task = Task(id, title, description, status, now, now)
        return TaskDto(task)
    }

    private fun executeFindById(id: Long): TaskDto? {
        return jdbcClient.sql(
            "SELECT * FROM tasks WHERE id = ?",
        )
            .params(id)
            .query(Task::class.java)
            .optional()
            .map { TaskDto(it) }
            .orElse(null)
    }


    private fun executeFindAll(page: Int, size: Int, status: TaskStatus?): SliceTaskDto? {
        val offset = page * size

        val sql = buildString {
            append("""
            SELECT 
                t.id,
                t.title,
                t.description,
                t.status,
                t.created_at,
                t.updated_at,
                COUNT(*) OVER() AS total_count
            FROM tasks t
        """.trimIndent())
            if (status != null) {
                append("\nWHERE t.status = :status")
            }

            append("\nORDER BY t.created_at DESC")
            append("\nLIMIT :limit OFFSET :offset")
        }

        val spec = jdbcClient.sql(sql)
            .param("limit", size)
            .param("offset", offset)

        if (status != null) {
            spec.param("status", status.name)
        }

        val tasksWithTotal = spec.query { rs, _ ->
            TaskWithTotal(
                task = Task(
                    id = rs.getLong("id"),
                    title = rs.getString("title"),
                    description = rs.getString("description"),
                    status = TaskStatus.valueOf(rs.getString("status")),
                    createdAt = rs.getObject("created_at", LocalDateTime::class.java),
                    updatedAt = rs.getObject("updated_at", LocalDateTime::class.java)
                ),
                totalCount = rs.getLong("total_count")
            )
        }.list()
        val tasks = tasksWithTotal.map { it.task }.map { TaskDto(it) }
        val totalElements = tasksWithTotal.firstOrNull()?.totalCount ?: 0L

        val totalPages = if (totalElements > 0) {
            ((totalElements + size - 1) / size).toInt()
        } else {
            0
        }
        return if (tasks.isEmpty()) {
            null
        } else {
            SliceTaskDto(
                content = tasks,
                page = page,
                size = size,
                totalElements = totalElements,
                totalPages = totalPages
            )
        }

    }

    private fun executeUpdateStatus(id: Long, newStatus: TaskStatus): TaskDto? {
        val taskDto = jdbcClient.sql(
            """
                update tasks
                set status = :status, updated_at = now()
                where id = :id
                returning *
            """.trimIndent()
        )
            .params(mapOf("id" to id, "status" to newStatus.name))
            .query(Task::class.java)
            .optional()
            .map { TaskDto(it) }
            .orElse(null)
        return taskDto
    }

    private fun executeDeleteById(id: Long): Int {
        return jdbcClient.sql("DELETE FROM tasks WHERE id = :id")
            .param("id", id)
            .update()
    }

}