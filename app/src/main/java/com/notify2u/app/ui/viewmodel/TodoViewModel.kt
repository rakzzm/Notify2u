package com.notify2u.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notify2u.app.data.local.TodoDao
import com.notify2u.app.data.local.TodoTaskEntity
import com.notify2u.app.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val todoDao: TodoDao,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    val allTasks: StateFlow<List<TodoTaskEntity>> = todoDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertTask(task: TodoTaskEntity) {
        viewModelScope.launch {
            val insertedId = todoDao.insertTask(task)
            firestoreRepository.syncTodoTask(task.copy(id = insertedId.toInt()))
        }
    }

    fun updateTask(task: TodoTaskEntity) {
        viewModelScope.launch {
            todoDao.updateTask(task)
            firestoreRepository.syncTodoTask(task)
        }
    }

    fun deleteTask(task: TodoTaskEntity) {
        viewModelScope.launch {
            todoDao.deleteTask(task)
            firestoreRepository.deleteTodoTask(task.id)
        }
    }

    fun toggleTaskCompletion(task: TodoTaskEntity) {
        viewModelScope.launch {
            val nextState = !task.isCompleted
            todoDao.toggleTaskCompletion(task.id, nextState)
            firestoreRepository.syncTodoTask(task.copy(isCompleted = nextState))
        }
    }
}

class TodoViewModelFactory(
    private val todoDao: TodoDao,
    private val firestoreRepository: FirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(todoDao, firestoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
