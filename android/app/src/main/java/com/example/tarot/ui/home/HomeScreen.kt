package com.example.tarot.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tarot.ui.theme.Gold
import com.example.tarot.ui.theme.GoldLight
import com.example.tarot.ui.theme.Lavender
import com.example.tarot.ui.theme.NebulaViolet
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tarot.data.TarotRepository
import com.example.tarot.data.remote.ReadingDto
import com.example.tarot.domain.SpreadOption
import com.example.tarot.ui.components.ErrorBanner
import com.example.tarot.ui.components.SpreadCompactCard
import com.example.tarot.util.formatReadingDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: TarotRepository,
    onOpenSpreads: () -> Unit,
    onOpenPractices: () -> Unit,
    onOpenSpread: (SpreadOption) -> Unit,
    onOpenReading: (String) -> Unit,
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(repository))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "✦",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(9.dp))
                        Text(
                            text = "Mystic Tarot",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Text(
                        text = "Привет, ${state.userName.ifBlank { "путник" }}",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Карты готовы помочь разобраться в том, что важно.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            state.error?.let { message ->
                item {
                    ErrorBanner(message = message)
                }
            }

            item {
                CosmicHero(onOpenSpreads = onOpenSpreads)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Популярные схемы",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onOpenSpreads) {
                        Text("Все расклады")
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(SpreadOption.featured, key = { it.apiValue }) { option ->
                        SpreadCompactCard(
                            option = option,
                            onClick = { onOpenSpread(option) },
                        )
                    }
                }
            }

            item {
                PracticesPromoCard(onClick = onOpenPractices)
            }

            state.latestReading?.let { reading ->
                item {
                    Text(
                        text = "Последний расклад",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                item {
                    LatestReadingCard(
                        reading = reading,
                        onClick = { onOpenReading(reading.id) },
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "Как задать вопрос",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Говорите о себе и своих чувствах. Чем конкретнее вопрос, тем яснее ответ карт.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CosmicHero(onOpenSpreads: () -> Unit) {
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2B1A59),
                        Color(0xFF160D34),
                        Color(0xFF0B0720),
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Gold.copy(alpha = 0.55f),
                        Lavender.copy(alpha = 0.16f),
                        Gold.copy(alpha = 0.18f),
                    )
                ),
                shape = shape,
            ),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val glowCenter = Offset(size.width * 0.86f, size.height * 0.1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NebulaViolet.copy(alpha = 0.5f), Color.Transparent),
                    center = glowCenter,
                    radius = size.width * 0.62f,
                ),
                radius = size.width * 0.62f,
                center = glowCenter,
            )

            val orbitCenter = Offset(size.width * 0.84f, size.height * 0.26f)
            drawCircle(
                color = Gold.copy(alpha = 0.17f),
                radius = size.width * 0.3f,
                center = orbitCenter,
                style = Stroke(width = 1.dp.toPx()),
            )
            drawCircle(
                color = Lavender.copy(alpha = 0.12f),
                radius = size.width * 0.21f,
                center = orbitCenter,
                style = Stroke(width = 1.dp.toPx()),
            )

            repeat(26) { index ->
                val x = ((index * 43 + 9) % 100) / 100f * size.width
                val y = ((index * 67 + 13) % 100) / 100f * size.height
                drawCircle(
                    color = if (index % 7 == 0) Gold.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.22f),
                    radius = if (index % 6 == 0) 1.5.dp.toPx() else 0.8.dp.toPx(),
                    center = Offset(x, y),
                )
            }

            val moonCenter = Offset(size.width * 0.84f, size.height * 0.26f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(GoldLight.copy(alpha = 0.3f), Color.Transparent),
                    center = moonCenter,
                    radius = 44.dp.toPx(),
                ),
                radius = 44.dp.toPx(),
                center = moonCenter,
            )
            drawCircle(
                color = Gold.copy(alpha = 0.72f),
                radius = 25.dp.toPx(),
                center = moonCenter,
            )
            drawCircle(
                color = Color(0xFF241747),
                radius = 23.dp.toPx(),
                center = moonCenter + Offset(8.dp.toPx(), -6.dp.toPx()),
            )
            drawCircle(
                color = GoldLight.copy(alpha = 0.7f),
                radius = 1.3.dp.toPx(),
                center = moonCenter + Offset(-34.dp.toPx(), -24.dp.toPx()),
            )
        }

        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "✦",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "M Y S T I C",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                )
            }
            Text(
                text = "Новый расклад",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Выберите схему из каталога — вопрос зададите перед гаданием. Результат сохранится в истории.",
                modifier = Modifier.fillMaxWidth(0.84f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Button(
                onClick = onOpenSpreads,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Выбрать расклад")
            }
        }
    }
}

@Composable
private fun PracticesPromoCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Lavender.copy(alpha = 0.07f),
        border = BorderStroke(1.dp, Lavender.copy(alpha = 0.24f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Lavender.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✧",
                    style = MaterialTheme.typography.titleLarge,
                    color = Lavender,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Практики и ключи",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "17 практик: нумерология, символическая астрология, архетипы и сны.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "→",
                style = MaterialTheme.typography.titleLarge,
                color = Gold,
            )
        }
    }
}

@Composable
private fun LatestReadingCard(
    reading: ReadingDto,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reading.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildString {
                        append(reading.spreadLabel)
                        append(" · ")
                        append(formatReadingDate(reading.createdAt))
                        reading.theme?.takeIf { it.isNotBlank() }?.let {
                            append(" · ✦ ")
                            append(it)
                        }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "→",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
