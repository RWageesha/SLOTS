package com.slots.app.domain.usecase

import com.slots.app.data.repository.TaskRepository
import com.slots.app.domain.model.Task
import com.slots.app.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.getAllTasks()
}

class GetPendingTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.getPendingTasks()
}

class GetTasksByCategoryUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(category: String): Flow<List<Task>> = repository.getTasksByCategory(category)
}

class InsertTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Long = repository.insertTask(task)
}

class UpdateTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.updateTask(task)
}

class DeleteTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.deleteTask(task)
}

class ToggleTaskStatusUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) {
        val newStatus = if (task.status == TaskStatus.PENDING) TaskStatus.COMPLETED else TaskStatus.PENDING
        repository.updateTask(task.copy(status = newStatus))
    }
}

data class TaskUseCases(
    val getAllTasks: GetAllTasksUseCase,
    val getPendingTasks: GetPendingTasksUseCase,
    val getTasksByCategory: GetTasksByCategoryUseCase,
    val insertTask: InsertTaskUseCase,
    val updateTask: UpdateTaskUseCase,
    val deleteTask: DeleteTaskUseCase,
    val toggleTaskStatus: ToggleTaskStatusUseCase
)
