package com.slots.app.ui.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.slots.app.domain.model.ChatMessage
import com.slots.app.domain.usecase.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    val chatHistory = chatUseCases.getChatHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        .asLiveData()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = chatUseCases.sendChatMessage(message)
            result.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Failed to get response"
            }
            _isLoading.value = false
        }
    }

    fun clearHistory() {
        viewModelScope.launch { chatUseCases.clearChatHistory() }
    }
}
