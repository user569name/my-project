package com.example.tarot.ui.practices

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tarot.domain.Practice
import com.example.tarot.domain.PracticeCatalog
import com.example.tarot.domain.PracticeCategory
import com.example.tarot.ui.theme.Gold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticesScreen(
    onOpenPractice: (String) -> Unit,
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Практики",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PracticesIntro()
            }

            PracticeCategory.entries.forEach { category ->
                val practices = PracticeCatalog.byCategory(category)
                item(span = { GridItemSpan(maxLineSpan) }, key = "head-${category.name}") {
                    PracticeGroupHeader(category = category, count = practices.size)
                }
                items(practices, key = { it.id }) { practice ->
                    PracticeCatalogCard(
                        practice = practice,
                        onClick = { onOpenPractice(practice.id) },
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Практики носят символический и развлекательный характер. " +
                        "Все расчёты выполняются на устройстве и остаются приватными.",
                    modifier = Modifier.padding(top = 10.dp, start = 4.dp, end = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PracticesIntro() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.28f)),
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF241747),
                        Color(0xFF120C29),
                        Color(0xFF0B0719),
                    ),
                ),
            ),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "СИМВОЛИЧЕСКИЕ ПРАКТИКИ",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Практики и ключи",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Нумерология, символическая астрология и язык архетипов. " +
                        "Семнадцать инструментов, чтобы услышать себя внимательнее.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniStat(value = "17", label = "практик")
                    MiniStat(value = "3", label = "раздела")
                    MiniStat(value = "офлайн", label = "расчёты")
                }
            }
        }
    }
}

@Composable
private fun MiniStat(value: String, label: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Gold.copy(alpha = 0.09f),
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.18f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PracticeGroupHeader(category: PracticeCategory, count: Int) {
    val accent = Color(category.accent)

    Column(modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = category.label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accent.copy(alpha = 0.5f), Color.Transparent),
                        ),
                    ),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "$count практик",
                style = MaterialTheme.typography.labelSmall,
                color = accent,
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = category.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PracticeCatalogCard(
    practice: Practice,
    onClick: () -> Unit,
) {
    val accent = Color(practice.accent)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 190.dp),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.26f)),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(accent.copy(alpha = 0.13f), CircleShape)
                        .border(1.dp, accent.copy(alpha = 0.24f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = practice.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = accent,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "✦",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent.copy(alpha = 0.55f),
                )
            }

            Spacer(modifier = Modifier.height(11.dp))

            Text(
                text = practice.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = practice.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = practice.chips.take(2).joinToString(" · "),
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = modesLabel(practice.modes.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleMedium,
                    color = accent.copy(alpha = 0.85f),
                )
            }
        }
    }
}

private fun modesLabel(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    val word = when {
        mod10 == 1 && mod100 != 11 -> "режим"
        mod10 in 2..4 && mod100 !in 12..14 -> "режима"
        else -> "режимов"
    }
    return "$count $word"
}