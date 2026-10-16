package com.example.tarot.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.tarot.network.PredictionResponseDto

class TarotViewModel : ViewModel() {
    var prediction by mutableStateOf<PredictionResponseDto?>(null)
    var isLoading by mutableStateOf(false)

    fun getPrediction() {
        isLoading = true
        viewModelScope.launch {
            try {
                // prediction = retrofit.create(TarotApi::class.java).drawCard(1)
            } catch (e: Exception) {
                // Обработка ошибок
            } finally {
                isLoading = false
            }
        }
    }
}

@Composable
fun TarotScreen(viewModel: TarotViewModel = TarotViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (viewModel.isLoading) {
            CircularProgressIndicator()
        } else if (viewModel.prediction != null) {
            Text(text = viewModel.prediction!!.card.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = viewModel.prediction!!.prediction_text)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = { viewModel.getPrediction() }) {
            Text("Вытянуть карту")
        }
    }
}
