package com.slots.app.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.slots.app.domain.model.Transaction
import com.slots.app.domain.usecase.TransactionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val transactionUseCases: TransactionUseCases
) : ViewModel() {

    val monthlyTransactions = transactionUseCases.getMonthlyTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        .asLiveData()

    val monthlyIncome = transactionUseCases.getMonthlyIncome()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        .asLiveData()

    val monthlyExpenses = transactionUseCases.getMonthlyExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        .asLiveData()

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch { transactionUseCases.insertTransaction(transaction) }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { transactionUseCases.deleteTransaction(transaction) }
    }
}
