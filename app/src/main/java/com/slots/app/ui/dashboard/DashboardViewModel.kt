package com.slots.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.slots.app.domain.usecase.GetMonthlyExpensesUseCase
import com.slots.app.domain.usecase.GetMonthlyIncomeUseCase
import com.slots.app.domain.usecase.GetPendingTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getPendingTasksUseCase: GetPendingTasksUseCase,
    getMonthlyIncomeUseCase: GetMonthlyIncomeUseCase,
    getMonthlyExpensesUseCase: GetMonthlyExpensesUseCase
) : ViewModel() {

    val pendingTasks = getPendingTasksUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        .asLiveData()

    val monthlyIncome = getMonthlyIncomeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        .asLiveData()

    val monthlyExpenses = getMonthlyExpensesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        .asLiveData()
}
