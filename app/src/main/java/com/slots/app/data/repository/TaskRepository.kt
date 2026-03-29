package com.slots.app.data.repository

import com.slots.app.data.local.dao.TaskDao
import com.slots.app.data.local.entity.TaskEntity
import com.slots.app.data.remote.api.SlotsApiService
import com.slots.app.data.remote.model.TaskApiModel
import com.slots.app.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val apiService: SlotsApiService
) {
    fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { entities -> entities.map { it.toDomain() } }

    fun getPendingTasks(): Flow<List<Task>> =
        taskDao.getPendingTasks().map { entities -> entities.map { it.toDomain() } }

    fun getTasksByCategory(category: String): Flow<List<Task>> =
        taskDao.getTasksByCategory(category).map { entities -> entities.map { it.toDomain() } }

    fun getPendingTaskCount(): Flow<Int> = taskDao.getPendingTaskCount()

    fun getTasksDueBetween(start: Long, end: Long): Flow<List<Task>> =
        taskDao.getTasksDueBetween(start, end).map { entities -> entities.map { it.toDomain() } }

    suspend fun insertTask(task: Task): Long =
        taskDao.insertTask(TaskEntity.fromDomain(task))

    suspend fun updateTask(task: Task) =
        taskDao.updateTask(TaskEntity.fromDomain(task))

    suspend fun deleteTask(task: Task) =
        taskDao.deleteTask(TaskEntity.fromDomain(task))

    suspend fun deleteTaskById(id: Long) =
        taskDao.deleteTaskById(id)

    suspend fun syncWithRemote(token: String) {
        try {
            val response = apiService.getTasks("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.tasks?.forEach { apiTask ->
                    val entity = TaskEntity(
                        title = apiTask.title,
                        description = apiTask.description,
                        category = apiTask.category,
                        priority = apiTask.priority,
                        deadline = apiTask.deadline,
                        status = apiTask.status,
                        createdAt = apiTask.createdAt
                    )
                    taskDao.insertTask(entity)
                }
            }
        } catch (e: Exception) {
            // Sync failed silently; local data remains available
        }
    }
}
