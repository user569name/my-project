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

data class ReadingDetailUiState(
    val reading: ReadingDto? = null,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null,
)

class ReadingDetailViewModel(
    private val repository: TarotRepository,
    private val readingId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingDetailUiState())
    val uiState: StateFlow<ReadingDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.reading(readingId) }
                .onSuccess { reading ->
                    _uiState.update { it.copy(isLoading = false, reading = reading) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, error = throwable.toUserMessage())
                    }
                }
        }
    }

    fun delete() {
        if (_uiState.value.isDeleting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, error = null) }
            runCatching { repository.deleteReading(readingId) }
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, deleted = true) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isDeleting = false, error = throwable.toUserMessage())
                    }
                }
        }
    }

    companion object {
        fun factory(
            repository: TarotRepository,
            readingId: String,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ReadingDetailViewModel(repository, readingId) }
        }
    }
}
