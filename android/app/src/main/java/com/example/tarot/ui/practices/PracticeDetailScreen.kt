package com.example.tarot.ui.practices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tarot.domain.Practice
import com.example.tarot.domain.PracticeBlock
import com.example.tarot.domain.PracticeChart
import com.example.tarot.domain.PracticeEngine
import com.example.tarot.domain.PracticeField
import com.example.tarot.domain.PracticeFieldType
import com.example.tarot.domain.PracticeMode
import com.example.tarot.domain.PracticeResult
import com.example.tarot.ui.components.EmptyState
import com.example.tarot.ui.components.ErrorBanner
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val pickedDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeDetailScreen(
    practiceId: String,
    onBack: () -> Unit,
) {
    val practice = remember(practiceId) { com.example.tarot.domain.PracticeCatalog.byId(practiceId) }
    if (practice == null) {
        MissingPractice(onBack = onBack)
        return
    }
    PracticeDetailContent(practice = practice, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MissingPractice(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Практика", style = MaterialTheme.typography.titleLarge) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            EmptyState(
                title = "Практика не найдена",
                subtitle = "Вернитесь в каталог и выберите другой инструмент.",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PracticeDetailContent(
    practice: Practice,
    onBack: () -> Unit,
) {
    val viewModel: PracticeViewModel = viewModel(
        key = "practice-${practice.id}",
        factory = PracticeViewModel.factory(practice),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val accent = Color(practice.accent)
    val listState = rememberLazyListState()
    val resultIndex = 2 + if (practice.modes.size > 1) 1 else 0

    LaunchedEffect(state.result) {
        if (state.result != null) {
            runCatching { listState.animateScrollToItem(resultIndex) }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = practice.title,
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
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                PracticeHero(practice = practice, accent = accent)
            }

            if (practice.modes.size > 1) {
                item {
                    ModeSelector(
                        modes = practice.modes,
                        selectedId = state.modeId,
                        accent = accent,
                        onSelect = viewModel::onModeChange,
                    )
                }
            }

            item {
                PracticeForm(
                    practice = practice,
                    state = state,
                    accent = accent,
                    onValueChange = viewModel::onValueChange,
                    onSubmit = viewModel::submit,
                )
            }

            state.error?.let { message ->
                item { ErrorBanner(message = message) }
            }

            state.result?.let { result ->
                item {
                    PracticeResultCard(result = result, accent = accent)
                }
            }
        }
    }
}

@Composable
private fun PracticeHero(practice: Practice, accent: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.32f)),
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    listOf(
                        accent.copy(alpha = 0.16f),
                        Color(0xFF120C29),
                        Color(0xFF0B0719),
                    ),
                ),
            ),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(accent.copy(alpha = 0.14f), CircleShape)
                            .border(1.dp, accent.copy(alpha = 0.28f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = practice.symbol,
                            style = MaterialTheme.typography.headlineSmall,
                            color = accent,
                        )
                    }
                    Spacer(modifier = Modifier.width(13.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = practice.category.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = practice.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = practice.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    practice.chips.forEach { chip ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = accent.copy(alpha = 0.09f),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.2f)),
                        ) {
                            Text(
                                text = chip,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = accent,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeSelector(
    modes: List<PracticeMode>,
    selectedId: String,
    accent: Color,
    onSelect: (String) -> Unit,
) {
    Column {
        Text(
            text = "РЕЖИМ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(7.dp))
        Row(
            modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            modes.forEach { mode ->
                FilterChip(
                    selected = mode.id == selectedId,
                    onClick = { onSelect(mode.id) },
                    label = { Text(mode.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accent.copy(alpha = 0.18f),
                        selectedLabelColor = accent,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@Composable
private fun PracticeForm(
    practice: Practice,
    state: PracticeUiState,
    accent: Color,
    onValueChange: (String, String) -> Unit,
    onSubmit: () -> Unit,
) {
    val fields = remember(practice.id, state.modeId) {
        com.example.tarot.domain.PracticeCatalog.fieldsFor(practice, state.modeId)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Column {
                Text(
                    text = if (fields.isEmpty()) "Символический бросок" else "Данные для расчёта",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (fields.isEmpty()) {
                        "Нажмите кнопку — и система сделает символический выбор за вас."
                    } else {
                        "Всё считается на устройстве. Данные никуда не отправляются."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            fields.forEach { field ->
                val value = state.values[field.key].orEmpty()
                when (field.type) {
                    PracticeFieldType.DATE -> DateInput(
                        field = field,
                        value = value,
                        accent = accent,
                        onChange = { onValueChange(field.key, it) },
                    )
                    PracticeFieldType.TIME -> TimeInput(
                        field = field,
                        value = value,
                        onChange = { onValueChange(field.key, it) },
                    )
                    PracticeFieldType.SELECT -> SelectInput(
                        field = field,
                        value = value,
                        accent = accent,
                        onChange = { onValueChange(field.key, it) },
                    )
                    PracticeFieldType.TEXTAREA -> TextInput(
                        field = field,
                        value = value,
                        multiline = true,
                        onChange = { onValueChange(field.key, it) },
                    )
                    PracticeFieldType.TEXT -> TextInput(
                        field = field,
                        value = value,
                        multiline = false,
                        onChange = { onValueChange(field.key, it) },
                    )
                }
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(practice.action, style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = "Практика носит символический характер и не заменяет консультацию специалиста.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DateInput(
    field: PracticeField,
    value: String,
    accent: Color,
    onChange: (String) -> Unit,
) {
    var pickerOpen by remember { mutableStateOf(false) }

    Surface(
        onClick = { pickerOpen = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = field.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value.ifBlank { field.placeholder.ifBlank { "Выбрать дату" } },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
            }
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Выбрать дату",
                tint = accent.copy(alpha = 0.85f),
                modifier = Modifier.size(21.dp),
            )
        }
    }

    if (pickerOpen) {
        val initialMillis = remember(value) {
            PracticeEngine.parseDate(value)
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli()
        }
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { pickerOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            onChange(formatPickedDate(millis))
                        }
                        pickerOpen = false
                    },
                ) {
                    Text("Готово")
                }
            },
            dismissButton = {
                TextButton(onClick = { pickerOpen = false }) {
                    Text("Отмена")
                }
            },
        ) {
            DatePicker(
                state = pickerState,
                title = {
                    Text(
                        text = field.label,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
            )
        }
    }
}

private fun formatPickedDate(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(pickedDateFormat)

@Composable
private fun TimeInput(
    field: PracticeField,
    value: String,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            val digits = raw.filter { it.isDigit() }.take(4)
            val formatted = when {
                digits.isEmpty() -> ""
                digits.length <= 2 -> digits
                else -> digits.take(2) + ":" + digits.drop(2)
            }
            onChange(formatted)
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(field.label) },
        placeholder = { Text(field.placeholder.ifBlank { "ЧЧ:ММ" }) },
        supportingText = if (!field.hint.isNullOrBlank()) {
            { Text(field.hint.orEmpty()) }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(14.dp),
    )
}

@Composable
private fun TextInput(
    field: PracticeField,
    value: String,
    multiline: Boolean,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = !multiline,
        minLines = if (multiline) 4 else 1,
        maxLines = if (multiline) 8 else 1,
        label = { Text(field.label) },
        placeholder = { Text(field.placeholder) },
        supportingText = if (!field.hint.isNullOrBlank()) {
            { Text(field.hint.orEmpty()) }
        } else {
            null
        },
        shape = RoundedCornerShape(14.dp),
    )
}

@Composable
private fun SelectInput(
    field: PracticeField,
    value: String,
    accent: Color,
    onChange: (String) -> Unit,
) {
    Column {
        Text(
            text = field.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            field.options.forEach { (optionValue, optionLabel) ->
                FilterChip(
                    selected = value == optionValue,
                    onClick = { onChange(optionValue) },
                    label = { Text(optionLabel) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accent.copy(alpha = 0.18f),
                        selectedLabelColor = accent,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@Composable
private fun PracticeResultCard(result: PracticeResult, accent: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.36f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = result.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                    )
                }
                Text(
                    text = "✦",
                    style = MaterialTheme.typography.titleMedium,
                    color = accent,
                )
            }

            result.chart?.let { chart ->
                PracticeChartView(chart = chart, accent = accent)
            }

            result.blocks.forEach { block ->
                PracticeBlockView(block = block, accent = accent)
            }

            if (result.note.isNotBlank()) {
                Text(
                    text = result.note,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PracticeBlockView(block: PracticeBlock, accent: Color) {
    Column {
        Text(
            text = block.label.uppercase(Locale.forLanguageTag("ru")),
            style = MaterialTheme.typography.labelSmall,
            color = accent,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = block.value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        if (block.text.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = block.text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PracticeChartView(chart: PracticeChart, accent: Color) {
    when (chart) {
        is PracticeChart.Pythagoras -> PythagorasChart(chart = chart, accent = accent)
        is PracticeChart.Fate -> FateChart(chart = chart, accent = accent)
        is PracticeChart.Hexagram -> HexagramChart(chart = chart, accent = accent)
        is PracticeChart.Pillars -> PillarsChart(chart = chart, accent = accent)
        is PracticeChart.Cities -> CitiesChart(chart = chart, accent = accent)
        is PracticeChart.Spheres -> SpheresChart(chart = chart, accent = accent)
    }
}

@Composable
private fun PythagorasChart(chart: PracticeChart.Pythagoras, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        chart.cells.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { cell ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = accent.copy(alpha = 0.07f),
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.2f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = cell.digit.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = accent,
                            )
                            Text(
                                text = "×${cell.count}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FateChart(chart: PracticeChart.Fate, accent: Color) {
    val series = chart.series
    Column {
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp),
        ) {
            if (series.points.isEmpty()) return@Canvas
            val left = 8.dp.toPx()
            val top = 10.dp.toPx()
            val bottom = size.height - 10.dp.toPx()
            val chartWidth = size.width - left * 2
            val maxValue = (series.points.maxOrNull() ?: 9).coerceAtLeast(1).toFloat()
            val horizontalStep = if (series.points.size > 1) {
                chartWidth / (series.points.size - 1)
            } else {
                chartWidth
            }

            repeat(3) { index ->
                val y = top + (bottom - top) * index / 2f
                drawLine(
                    color = accent.copy(alpha = 0.12f),
                    start = Offset(left, y),
                    end = Offset(size.width - left, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            val coords = series.points.mapIndexed { index, value ->
                Offset(
                    x = left + horizontalStep * index,
                    y = bottom - (bottom - top) * (value / maxValue),
                )
            }

            val path = Path().apply {
                moveTo(coords.first().x, coords.first().y)
                coords.drop(1).forEach { point -> lineTo(point.x, point.y) }
            }
            drawPath(
                path = path,
                color = accent,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
            coords.forEach { point ->
                drawCircle(color = accent, radius = 4.dp.toPx(), center = point)
                drawCircle(
                    color = Color.White.copy(alpha = 0.75f),
                    radius = 1.5.dp.toPx(),
                    center = point,
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            series.labels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        lineHeight = 10.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun HexagramChart(chart: PracticeChart.Hexagram, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        chart.lines.asReversed().forEach { line ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (line.yang) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(accent, RoundedCornerShape(4.dp)),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(accent, RoundedCornerShape(4.dp)),
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(accent, RoundedCornerShape(4.dp)),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (line.changing) "○" else " ",
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "○ — меняющаяся линия",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PillarsChart(chart: PracticeChart.Pillars, accent: Color) {
    Row(
        modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chart.pillars.forEach { pillar ->
            Surface(
                modifier = Modifier.width(104.dp),
                shape = RoundedCornerShape(16.dp),
                color = accent.copy(alpha = 0.07f),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.2f)),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = pillar.label.uppercase(Locale.forLanguageTag("ru")),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pillar.stem,
                        style = MaterialTheme.typography.titleLarge,
                        color = accent,
                    )
                    Text(
                        text = pillar.branch,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = pillar.element,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun CitiesChart(chart: PracticeChart.Cities, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        chart.items.forEach { city ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = accent.copy(alpha = 0.06f),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.16f)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = city.name, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = city.country,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = accent.copy(alpha = 0.15f),
                    ) {
                        Text(
                            text = city.score.toString(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = accent,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpheresChart(chart: PracticeChart.Spheres, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        chart.items.forEach { sphere ->
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sphere.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = sphere.value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .background(
                            color = accent.copy(alpha = 0.13f),
                            shape = RoundedCornerShape(50),
                        ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((sphere.value / 9f).coerceIn(0.04f, 1f))
                            .fillMaxHeight()
                            .background(
                                color = accent.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(50),
                            ),
                    )
                }
            }
        }
    }
}