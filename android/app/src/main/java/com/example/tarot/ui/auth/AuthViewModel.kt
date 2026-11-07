package com.example.tarot.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tarot.data.TarotRepository
import com.example.tarot.data.remote.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, REGISTER }

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
)

class AuthViewModel(
    private val repository: TarotRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun onDisplayNameChange(value: String) {
        _uiState.update { it.copy(displayName = value, error = null) }
    }

    fun changeMode(mode: AuthMode) {
        _uiState.update { it.copy(mode = mode, error = null) }
    }

    fun submit() {
        val state = _uiState.value
        if (state.isLoading) return

        val email = state.email.trim()
        if (!email.contains("@") || email.length < 5) {
            _uiState.update { it.copy(error = "Введите корректный email") }
            return
        }
        if (state.password.length < 8) {
            _uiState.update { it.copy(error = "Пароль должен содержать минимум 8 символов") }
            return
        }
        if (state.mode == AuthMode.REGISTER && state.displayName.trim().length < 2) {
            _uiState.update { it.copy(error = "Имя должно содержать минимум 2 символа") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                if (state.mode == AuthMode.LOGIN) {
                    repository.login(email, state.password)
                } else {
                    repository.register(email, state.password, state.displayName)
                }
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(isLoading = false, error = throwable.toUserMessage())
                }
            }
        }
    }

    companion object {
        fun factory(repository: TarotRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { AuthViewModel(repository) }
        }
    }
}
