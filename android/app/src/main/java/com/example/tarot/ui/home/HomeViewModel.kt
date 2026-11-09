package com.example.tarot.ui.home

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

data class HomeUiState(
    val userName: String = "",
    val latestReading: ReadingDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class HomeViewModel(
    private val repository: TarotRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        loadLatestReading()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { repository.currentUser() }
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(userName = user.displayName, isLoading = false)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, error = throwable.toUserMessage())
                    }
                }
        }
    }

    private fun loadLatestReading() {
        viewModelScope.launch {
            runCatching { repository.readings(limit = 1).firstOrNull() }
                .onSuccess { reading ->
                    _uiState.update { it.copy(latestReading = reading) }
                }
        }
    }

    companion object {
        fun factory(repository: TarotRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(repository) }
        }
    }
}
