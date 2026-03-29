package com.slots.app.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.slots.app.data.repository.DebtRepository
import com.slots.app.domain.model.Debt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository
) : ViewModel() {

    val allDebts = debtRepository.getAllDebts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        .asLiveData()

    val totalLent = debtRepository.getTotalLent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        .asLiveData()

    val totalBorrowed = debtRepository.getTotalBorrowed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        .asLiveData()

    fun insertDebt(debt: Debt) {
        viewModelScope.launch { debtRepository.insertDebt(debt) }
    }

    fun updateDebt(debt: Debt) {
        viewModelScope.launch { debtRepository.updateDebt(debt) }
    }

    fun deleteDebt(debt: Debt) {
        viewModelScope.launch { debtRepository.deleteDebt(debt) }
    }

    fun settleDebt(debt: Debt) {
        viewModelScope.launch {
            debtRepository.updateDebt(debt.copy(status = com.slots.app.domain.model.DebtStatus.SETTLED))
        }
    }
}
