package com.example.tarot.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tarot.data.TarotRepository
import com.example.tarot.data.remote.ReadingDto
import com.example.tarot.data.remote.toUserMessage
import com.example.tarot.domain.SpreadOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val question: String = "",
    val spread: SpreadOption = SpreadOption.THREE,
    val isDrawing: Boolean = false,
    val reading: ReadingDto? = null,
    val error: String? = null,
)

class HomeViewModel(
    private val repository: TarotRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            runCatching { repository.currentUser() }
                .onSuccess { user ->
                    _uiState.update { it.copy(userName = user.displayName) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.toUserMessage()) }
                }
        }
    }

    fun onQuestionChange(value: String) {
        _uiState.update { it.copy(question = value, error = null) }
    }

    fun onSpreadChange(spread: SpreadOption) {
        _uiState.update { it.copy(spread = spread, error = null) }
    }

    fun draw() {
        val state = _uiState.value
        if (state.isDrawing) return

        val question = state.question.trim()
        when {
            question.length < 3 -> {
                _uiState.update { it.copy(error = "Сформулируйте вопрос (минимум 3 символа)") }
                return
            }
            question.length > 500 -> {
                _uiState.update { it.copy(error = "Вопрос слишком длинный (максимум 500 символов)") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isDrawing = true, error = null, reading = null) }
            runCatching { repository.draw(question, state.spread) }
                .onSuccess { reading ->
                    _uiState.update { it.copy(isDrawing = false, reading = reading) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isDrawing = false, error = throwable.toUserMessage())
                    }
                }
        }
    }

    fun reset() {
        _uiState.update { it.copy(reading = null, question = "", error = null) }
    }

    companion object {
        fun factory(repository: TarotRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(repository) }
        }
    }
}
