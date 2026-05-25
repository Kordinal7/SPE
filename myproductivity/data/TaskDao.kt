package com.example.myproductivity.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE isArchived = 0")
    fun getActiveTasks(): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isArchived = 1")
    fun getArchivedTasks(): LiveData<List<TaskEntity>>

    @Insert
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}