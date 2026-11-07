package com.example.tarot.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tarot.data.TarotRepository
import com.example.tarot.data.remote.ReadingDto
import com.example.tarot.data.remote.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val readings: List<ReadingDto> = emptyList(),
    val isLoading: Boolean = true,
    val deletingId: String? = null,
    val error: String? = null,
)

class HistoryViewModel(
    private val repository: TarotRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun refresh() {
        if (_uiState.value.deletingId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.readings() }
                .onSuccess { readings ->
                    _uiState.update { it.copy(isLoading = false, readings = readings) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, error = throwable.toUserMessage())
                    }
                }
        }
    }

    fun delete(readingId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(deletingId = readingId, error = null) }
            runCatching { repository.deleteReading(readingId) }
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            deletingId = null,
                            readings = state.readings.filterNot { it.id == readingId },
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(deletingId = null, error = throwable.toUserMessage())
                    }
                }
        }
    }

    companion object {
        fun factory(repository: TarotRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HistoryViewModel(repository) }
        }
    }
}
