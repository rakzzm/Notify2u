package com.notify2u.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notify2u.app.data.local.TodoDao
import com.notify2u.app.data.local.TodoTaskEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(private val todoDao: TodoDao) : ViewModel() {

    val allTasks: StateFlow<List<TodoTaskEntity>> = todoDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertTask(task: TodoTaskEntity) {
        viewModelScope.launch {
            todoDao.insertTask(task)
        }
    }

    fun updateTask(task: TodoTaskEntity) {
        viewModelScope.launch {
            todoDao.updateTask(task)
        }
    }

    fun deleteTask(task: TodoTaskEntity) {
        viewModelScope.launch {
            todoDao.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: TodoTaskEntity) {
        viewModelScope.launch {
            todoDao.toggleTaskCompletion(task.id, !task.isCompleted)
        }
    }
}

class TodoViewModelFactory(private val todoDao: TodoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(todoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
