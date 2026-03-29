package com.slots.app.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.slots.app.domain.model.Task
import com.slots.app.domain.model.TaskStatus
import com.slots.app.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {

    private val _filterCategory = MutableStateFlow<String?>(null)
    private val _filterStatus = MutableStateFlow<String?>(null)

    private val _allTasks = taskUseCases.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks = combine(_allTasks, _filterCategory, _filterStatus) { tasks, category, status ->
        tasks.filter { task ->
            (category == null || task.category.name == category) &&
                    (status == null || task.status.name == status)
        }
    }.asLiveData()

    fun setFilterCategory(category: String?) {
        _filterCategory.value = category
    }

    fun setFilterStatus(status: String?) {
        _filterStatus.value = status
    }

    fun insertTask(task: Task) {
        viewModelScope.launch { taskUseCases.insertTask(task) }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { taskUseCases.updateTask(task) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { taskUseCases.deleteTask(task) }
    }

    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch { taskUseCases.toggleTaskStatus(task) }
    }
}
