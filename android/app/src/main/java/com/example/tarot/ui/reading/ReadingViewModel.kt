package com.example.tarot.ui.reading

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

data class ReadingUiState(
    val question: String = "",
    val isDrawing: Boolean = false,
    val reading: ReadingDto? = null,
    val error: String? = null,
)

class ReadingViewModel(
    private val repository: TarotRepository,
    private val spread: SpreadOption,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingUiState())
    val uiState: StateFlow<ReadingUiState> = _uiState.asStateFlow()

    fun onQuestionChange(value: String) {
        _uiState.update { it.copy(question = value, error = null) }
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
            runCatching { repository.draw(question, spread) }
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
        fun factory(
            repository: TarotRepository,
            spread: SpreadOption,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ReadingViewModel(repository, spread) }
        }
    }
}
