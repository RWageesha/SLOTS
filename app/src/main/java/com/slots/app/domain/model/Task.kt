package com.slots.app.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: TaskCategory,
    val priority: TaskPriority,
    val deadline: Long? = null,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskCategory { STUDY, WORK, PERSONAL, HEALTH }
enum class TaskPriority { LOW, MEDIUM, HIGH }
enum class TaskStatus { PENDING, COMPLETED }
