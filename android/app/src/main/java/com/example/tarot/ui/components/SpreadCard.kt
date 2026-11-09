package com.example.tarot.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tarot.domain.SpreadCategory
import com.example.tarot.domain.SpreadOption
import com.example.tarot.ui.theme.Gold
import com.example.tarot.ui.theme.Lavender
import com.example.tarot.ui.theme.Rose

fun spreadAccent(category: SpreadCategory): Color = when (category) {
    SpreadCategory.GENERAL -> Gold
    SpreadCategory.LOVE -> Rose
    SpreadCategory.CAREER -> Color(0xFF9CC7E8)
    SpreadCategory.MONEY -> Color(0xFF9FD8B4)
    SpreadCategory.FUTURE -> Lavender
    SpreadCategory.DECISION -> Color(0xFFE8C48A)
}

fun cardCountLabel(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    return when {
        mod10 == 1 && mod100 != 11 -> "$count карта"
        mod10 in 2..4 && mod100 !in 12..14 -> "$count карты"
        else -> "$count карт"
    }
}

@Composable
fun SpreadCatalogCard(
    option: SpreadOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = spreadAccent(option.category)

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 196.dp),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(accent.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = accent,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cardCountLabel(option.positions.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = option.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 2,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = option.category.label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = accent,
            )
        }
    }
}

@Composable
fun SpreadCompactCard(
    option: SpreadOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = spreadAccent(option.category)

    Surface(
        onClick = onClick,
        modifier = modifier
            .width(220.dp)
            .heightIn(min = 168.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(accent.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        color = accent,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = option.category.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = option.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 2,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cardCountLabel(option.positions.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
