import com.work.taskCatalog.dto.TaskCreateDto
import com.work.taskCatalog.model.TaskStatus
import com.work.taskCatalog.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.jdbc.core.simple.JdbcClient
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
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """
            )
                .params(any<List<Any>>())
                .query(Long::class.java)
                .single()
        } returns 1L

        val result = taskRepository.save(dto.title!!, dto.description).block()
        assertNotNull(result)
        assertEquals(dto.title, result.title)
        assertEquals(dto.description, result.description)
        assertEquals(TaskStatus.NEW, result.status)
        assertNotNull(result.updatedAt)
        assertNotNull(result.createdAt)
        assertEquals(1L, result.id)
    }
}