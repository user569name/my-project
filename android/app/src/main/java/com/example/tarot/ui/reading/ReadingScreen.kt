package com.example.tarot.ui.reading

import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tarot.data.TarotRepository
import com.example.tarot.domain.SpreadOption
import com.example.tarot.ui.components.ErrorBanner
import com.example.tarot.ui.components.ReadingCardView
import com.example.tarot.ui.components.cardCountLabel
import com.example.tarot.ui.components.spreadAccent
import com.example.tarot.ui.theme.Gold
import com.example.tarot.ui.theme.GoldLight
import com.example.tarot.util.formatReadingDate
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReadingScreen(
    repository: TarotRepository,
    spread: SpreadOption,
    onBack: () -> Unit,
) {
    val viewModel: ReadingViewModel = viewModel(
        factory = ReadingViewModel.factory(repository, spread),
        key = "reading-${spread.apiValue}",
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.reading?.id) {
        if (state.reading != null) focusManager.clearFocus()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = spread.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                SpreadOverview(spread = spread)
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Ваш вопрос",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        OutlinedTextField(
                            value = state.question,
                            onValueChange = viewModel::onQuestionChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("О чём вы хотите спросить карты?") },
                            minLines = 2,
                            maxLines = 4,
                            enabled = !state.isDrawing,
                            supportingText = {
                                Text(
                                    text = "${state.question.length}/500",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                        )
                    }
                }
            }

            state.error?.let { message ->
                item {
                    ErrorBanner(message = message)
                }
            }

            item {
                Button(
                    onClick = viewModel::draw,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = !state.isDrawing,
                ) {
                    if (state.isDrawing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Карты перемешиваются...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Разложить карты", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            if (state.isDrawing) {
                item {
                    ShufflePanel()
                }
            }

            state.reading?.let { reading ->
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "Ваш расклад",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = reading.question,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${reading.spreadLabel} · ${formatReadingDate(reading.createdAt)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        if (!reading.theme.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(7.dp))
                            ThemeChip(theme = reading.theme.orEmpty())
                        }
                    }
                }

                itemsIndexed(
                    items = reading.orderedCards,
                    key = { _, readingCard -> "${reading.id}-${readingCard.position}" },
                ) { index, readingCard ->
                    DealCard(index = index, readingId = reading.id) {
                        ReadingCardView(readingCard = readingCard)
                    }
                }

                item {
                    Text(
                        text = "Расклад сохранён в истории",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item {
                    OutlinedButton(
                        onClick = viewModel::reset,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                    ) {
                        Text("Новый вопрос")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpreadOverview(spread: SpreadOption) {
    val accent = spreadAccent(spread.category)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f)),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(accent.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = spread.symbol,
                        style = MaterialTheme.typography.headlineSmall,
                        color = accent,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = spread.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = spread.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                    )
                }
                Text(
                    text = cardCountLabel(spread.positions.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = spread.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "СХЕМА РАСКЛАДА",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                spread.positions.forEachIndexed { index, position ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = accent.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
                    ) {
                        Text(
                            text = "${index + 1}. $position",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeChip(theme: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f),
        ),
    ) {
        Text(
            text = "✦ $theme",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun DealCard(
    index: Int,
    readingId: String,
    content: @Composable () -> Unit,
) {
    val reducedMotion = rememberReducedMotion()
    var started by remember(readingId) { mutableStateOf(reducedMotion) }

    LaunchedEffect(readingId, index) {
        if (!reducedMotion) {
            if (index > 0) delay(index * 95L)
            started = true
        }
    }

    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (reducedMotion) 0 else 520,
            easing = FastOutSlowInEasing,
        ),
        label = "deal-$readingId-$index",
    )

    Box(
        modifier = Modifier.graphicsLayer {
            alpha = if (reducedMotion) 1f else progress
            translationY = (1f - progress) * 54.dp.toPx()
            rotationY = (1f - progress) * -72f
            cameraDistance = 16f * density
            transformOrigin = TransformOrigin(0.5f, 0.5f)
        },
    ) {
        content()
    }
}

@Composable
private fun ShufflePanel() {
    val transition = rememberInfiniteTransition(label = "shuffle")
    val lift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 680, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "lift",
    )
    val tilt by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "tilt",
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(96.dp),
                contentAlignment = Alignment.Center,
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .width(62.dp)
                            .height(90.dp)
                            .offset(
                                x = ((index - 1) * 18).dp,
                                y = ((index * 2) - lift * 6f).dp,
                            )
                            .rotate((index - 1) * 7f + tilt * 2f)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFF2B1A59), Color(0xFF0D0820)),
                                ),
                                shape = RoundedCornerShape(10.dp),
                            )
                            .border(
                                width = 1.dp,
                                color = Gold.copy(alpha = 0.34f),
                                shape = RoundedCornerShape(10.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "☾",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Gold.copy(alpha = 0.75f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Вселенная перемешивает колоду",
                style = MaterialTheme.typography.titleMedium,
                color = GoldLight,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Прислушайтесь к вопросу — карты уже выбирают ответ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            ) == 0f
        }.getOrDefault(false)
    }
}
