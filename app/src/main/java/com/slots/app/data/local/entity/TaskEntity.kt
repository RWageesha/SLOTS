package com.slots.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slots.app.domain.model.Task
import com.slots.app.domain.model.TaskCategory
import com.slots.app.domain.model.TaskPriority
import com.slots.app.domain.model.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "priority") val priority: String,
    @ColumnInfo(name = "deadline") val deadline: Long? = null,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Task = Task(
        id = id,
        title = title,
        description = description,
        category = TaskCategory.valueOf(category),
        priority = TaskPriority.valueOf(priority),
        deadline = deadline,
        status = TaskStatus.valueOf(status),
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(task: Task): TaskEntity = TaskEntity(
            id = task.id,
            title = task.title,
            description = task.description,
            category = task.category.name,
            priority = task.priority.name,
            deadline = task.deadline,
            status = task.status.name,
            createdAt = task.createdAt
        )
    }
}
