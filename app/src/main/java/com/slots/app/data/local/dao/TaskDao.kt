package com.slots.app.data.local.dao

import androidx.room.*
import com.slots.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = 'PENDING' ORDER BY deadline ASC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY created_at DESC")
    fun getTasksByCategory(category: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY created_at DESC")
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'PENDING'")
    fun getPendingTaskCount(): Flow<Int>

    @Query("SELECT * FROM tasks WHERE deadline BETWEEN :start AND :end ORDER BY deadline ASC")
    fun getTasksDueBetween(start: Long, end: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)
}
