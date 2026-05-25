package com.example.myproductivity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myproductivity.data.AppDatabase
import com.example.myproductivity.data.TaskEntity
import com.example.myproductivity.data.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val activeTasks: LiveData<List<TaskEntity>>
    val archivedTasks: LiveData<List<TaskEntity>>

    private val _taskMillis = MutableLiveData<Map<Int, Long>>(emptyMap())
    val taskMillis: LiveData<Map<Int, Long>> = _taskMillis

    private val _totalMillis = MutableLiveData(0L)
    val totalMillis: LiveData<Long> = _totalMillis

    private val _activeTaskId = MutableLiveData<Int?>(null)
    val activeTaskId: LiveData<Int?> = _activeTaskId

    private var taskTimerJob: Job? = null
    private var saveJob: Job? = null

    init {
        val dao = AppDatabase.getInstance(application).taskDao()
        repository = TaskRepository(dao)
        activeTasks = repository.activeTasks
        archivedTasks = repository.archivedTasks

        // Загружаем сохранённое время из БД при старте
        viewModelScope.launch {
            activeTasks.observeForever { tasks ->
                val current = _taskMillis.value?.toMutableMap() ?: mutableMapOf()
                tasks.forEach { task ->
                    // Загружаем только если ещё не загружено
                    if (!current.containsKey(task.id)) {
                        current[task.id] = task.elapsedMillis
                    }
                }
                _taskMillis.value = current
                recalculateTotal()
            }
        }
    }

    private fun recalculateTotal() {
        val total = _taskMillis.value?.values?.sum() ?: 0L
        _totalMillis.value = total
    }

    fun startTask(taskId: Int) {
        stopCurrentTask()
        _activeTaskId.value = taskId

        taskTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _taskMillis.value?.toMutableMap() ?: mutableMapOf()
                current[taskId] = (current[taskId] ?: 0L) + 1000L
                _taskMillis.value = current
                recalculateTotal()
            }
        }

        // Сохраняем в БД каждые 5 секунд
        saveJob = viewModelScope.launch {
            while (true) {
                delay(5000L)
                saveTaskTime(taskId)
            }
        }
    }

    private suspend fun saveTaskTime(taskId: Int) {
        val millis = _taskMillis.value?.get(taskId) ?: return
        val tasks = activeTasks.value ?: return
        val task = tasks.find { it.id == taskId } ?: return
        repository.update(task.copy(elapsedMillis = millis))
    }

    fun stopCurrentTask() {
        val currentId = _activeTaskId.value

        taskTimerJob?.cancel()
        saveJob?.cancel()
        taskTimerJob = null
        saveJob = null
        _activeTaskId.value = null

        // Сохраняем время при остановке
        if (currentId != null) {
            viewModelScope.launch {
                saveTaskTime(currentId)
            }
        }
    }

    fun toggleTask(taskId: Int) {
        if (_activeTaskId.value == taskId) {
            stopCurrentTask()
        } else {
            startTask(taskId)
        }
    }

    fun insertTask(task: TaskEntity) {
        viewModelScope.launch { repository.insert(task) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            if (_activeTaskId.value == task.id) {
                stopCurrentTask()
            }
            val current = _taskMillis.value?.toMutableMap() ?: mutableMapOf()
            current.remove(task.id)
            _taskMillis.value = current
            recalculateTotal()
            repository.delete(task)
        }
    }

    fun archiveTask(task: TaskEntity) {
        viewModelScope.launch {
            if (_activeTaskId.value == task.id) {
                stopCurrentTask()
            }
            val current = _taskMillis.value?.toMutableMap() ?: mutableMapOf()
            current.remove(task.id)
            _taskMillis.value = current
            recalculateTotal()
            repository.update(task.copy(isArchived = true))
        }
    }

    fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / 60000) % 60
        val hours = millis / 3600000
        return "%d:%02d:%02d".format(hours, minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        stopCurrentTask()
    }
}