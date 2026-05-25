package com.example.myproductivity.data

import androidx.lifecycle.LiveData

class TaskRepository(private val dao: TaskDao) {

    val activeTasks: LiveData<List<TaskEntity>> = dao.getActiveTasks()
    val archivedTasks: LiveData<List<TaskEntity>> = dao.getArchivedTasks()

    suspend fun insert(task: TaskEntity) = dao.insert(task)
    suspend fun update(task: TaskEntity) = dao.update(task)
    suspend fun delete(task: TaskEntity) = dao.delete(task)
}