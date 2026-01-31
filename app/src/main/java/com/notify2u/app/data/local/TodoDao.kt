package com.notify2u.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<TodoTaskEntity>>

    @Query("SELECT * FROM todo_tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TodoTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TodoTaskEntity): Long

    @Update
    suspend fun updateTask(task: TodoTaskEntity)

    @Delete
    suspend fun deleteTask(task: TodoTaskEntity)

    @Query("UPDATE todo_tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun toggleTaskCompletion(id: Int, isCompleted: Boolean)
}
