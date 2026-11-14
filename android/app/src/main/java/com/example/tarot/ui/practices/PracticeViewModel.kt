package com.example.tarot.ui.practices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tarot.domain.Practice
import com.example.tarot.domain.PracticeCatalog
import com.example.tarot.domain.PracticeEngine
import com.example.tarot.domain.PracticeFieldType
import com.example.tarot.domain.PracticeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PracticeUiState(
    val practice: Practice,
    val modeId: String,
    val values: Map<String, String> = emptyMap(),
    val result: PracticeResult? = null,
    val error: String? = null,
)

class PracticeViewModel(
    private val practice: Practice,
) : ViewModel() {

    private val initialMode = practice.modes.firstOrNull()?.id ?: "base"

    private val _uiState = MutableStateFlow(
        PracticeUiState(practice = practice, modeId = initialMode),
    )
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    fun onModeChange(modeId: String) {
        if (_uiState.value.modeId == modeId) return
        _uiState.update { it.copy(modeId = modeId, result = null, error = null) }
    }

    fun onValueChange(key: String, value: String) {
        _uiState.update {
            it.copy(values = it.values + (key to value), error = null)
        }
    }

    fun submit() {
        val state = _uiState.value
        val fields = PracticeCatalog.fieldsFor(state.practice, state.modeId)

        val missing = fields.firstOrNull { field ->
            field.required && state.values[field.key].orEmpty().isBlank()
        }
        if (missing != null) {
            _uiState.update { it.copy(error = "Заполните поле «${missing.label}»", result = null) }
            return
        }

        val invalidDate = fields.firstOrNull { field ->
            field.type == PracticeFieldType.DATE &&
                state.values[field.key].orEmpty().isNotBlank() &&
                PracticeEngine.parseDate(state.values[field.key]) == null
        }
        if (invalidDate != null) {
            _uiState.update {
                it.copy(error = "Проверьте дату в поле «${invalidDate.label}»", result = null)
            }
            return
        }

        val result = PracticeEngine.calculate(state.practice.id, state.modeId, state.values)
        if (result == null) {
            _uiState.update {
                it.copy(
                    result = null,
                    error = "Не удалось рассчитать. Проверьте введённые данные.",
                )
            }
            return
        }
        _uiState.update { it.copy(result = result, error = null) }
    }

    fun reset() {
        _uiState.update { it.copy(result = null, error = null) }
    }

    companion object {
        fun factory(practice: Practice): ViewModelProvider.Factory = viewModelFactory {
            initializer { PracticeViewModel(practice) }
        }
    }
}