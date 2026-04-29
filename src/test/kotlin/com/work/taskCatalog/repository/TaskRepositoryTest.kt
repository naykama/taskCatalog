import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.model.Task
import com.work.taskCatalog.model.TaskStatus
import com.work.taskCatalog.repository.TaskRepository
import com.work.taskCatalog.repository.TaskRepository.TaskWithTotal
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.springframework.jdbc.core.simple.JdbcClient
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class TaskRepositoryTest {

    private lateinit var mockJdbcClient: JdbcClient
    private lateinit var taskRepository: TaskRepository

    @BeforeEach
    fun setUp() {
        mockJdbcClient = mockk()
        taskRepository = TaskRepository(mockJdbcClient)
    }

    @Test
    fun saveTest() {
        val dto = TaskCreateDto("test", "test descr")
        every {
            mockJdbcClient.sql(
                """
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (:title, :description, :status, :created_at, :updated_at)
            RETURNING id
        """.trimIndent()
            )
                .params(any<Map<String, Any>>())
                .query(Long::class.java)
                .single()
        } returns 1L

        val result = taskRepository.save(dto).block()
        assertNotNull(result)
        assertEquals(dto.title, result.title)
        assertEquals(dto.description, result.description)
        assertEquals(TaskStatus.NEW, result.status)
        assertNotNull(result.updatedAt)
        assertNotNull(result.createdAt)
        assertEquals(1L, result.id)
    }

    @Test
    fun findByIdTest() {
        val savedTask = createTask(1L)
        every {
            mockJdbcClient.sql(
                """
                    SELECT * FROM tasks WHERE id = :id
                """.trimIndent()
            )
                .param("id", savedTask.id)
                .query(Task::class.java)
                .optional()
        } returns Optional.of(savedTask)
        val result = taskRepository.findById(savedTask.id).block()
        assertNotNull(result)
        assertEquals(savedTask.title, result.title)
        assertEquals(savedTask.description, result.description)
        assertEquals(savedTask.status, result.status)
        assertNotNull(result.updatedAt)
        assertNotNull(result.createdAt)
        assertEquals(savedTask.id, result.id)
    }

    @Test
    fun findTasksWithStatusTest() {
        val savedTask = createTask(1L)
        val savedTask2 = createTask(2L)
        val page = 1
        val size = 2
        val offset = page * size
        // Создаем моки для всех типов в цепочке
        val mockStatementSpec = mockk<JdbcClient.StatementSpec>()
        val mockMappedQuerySpec = mockk<JdbcClient.MappedQuerySpec<TaskWithTotal>>()

        val sql = """
        SELECT 
            t.id,
            t.title,
            t.description,
            t.status,
            t.created_at,
            t.updated_at,
            COUNT(*) OVER() AS total_count
        FROM tasks t
        WHERE t.status = :status
        ORDER BY t.created_at DESC
        LIMIT :limit OFFSET :offset
    """.trimIndent()

        // sql() возвращает StatementSpec
        every { mockJdbcClient.sql(sql) } returns mockStatementSpec

        // param() возвращает StatementSpec для цепочки вызовов
        every { mockStatementSpec.param("limit", size) } returns mockStatementSpec
        every { mockStatementSpec.param("offset", offset) } returns mockStatementSpec
        every { mockStatementSpec.param("status", TaskStatus.NEW.name) } returns mockStatementSpec

        // query() с RowMapper возвращает MappedQuerySpec
        every {
            mockStatementSpec.query(any<org.springframework.jdbc.core.RowMapper<TaskWithTotal>>())
        } returns mockMappedQuerySpec

        // list() на MappedQuerySpec возвращает результат
        every { mockMappedQuerySpec.list() } returns listOf(
            TaskWithTotal(savedTask, 2),
            TaskWithTotal(savedTask2, 2)
        )

        val result = taskRepository.findTasks(page, size, TaskStatus.NEW).block()
        assertNotNull(result)
        assertEquals(2, result.content?.size)
        assertEquals(2L, result.totalElements)
    }

    @Test
    fun updateStatusTest() {
        val savedTask = createTask(1L)
        val newStatus = TaskStatus.CANCELLED
        val changedTask = Task(savedTask.id, savedTask.title, savedTask.description, newStatus, savedTask.createdAt, savedTask.updatedAt)
        every {
            mockJdbcClient.sql(
                """
                update tasks
                set status = :status, updated_at = now()
                where id = :id
                returning *
                """.trimIndent()
            )
                .params(mapOf("id" to savedTask.id, "status" to newStatus.name))
                .query(Task::class.java)
                .optional()
        } returns Optional.of(changedTask)
        val result = taskRepository.updateStatus(savedTask.id, newStatus).block()
        assertNotNull(result)
    }

    @Test
    fun deleteByIdTest() {
        val id = 1L
        every {
            mockJdbcClient.sql(
                "DELETE FROM tasks WHERE id = :id"
            )
                .param("id", id)
                .update()
        } returns 1
        val result = taskRepository.deleteById(id).block()
        assertEquals(1, result)
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