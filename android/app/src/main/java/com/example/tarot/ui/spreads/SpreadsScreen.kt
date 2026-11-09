package com.example.tarot.ui.spreads

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tarot.domain.SpreadCategory
import com.example.tarot.domain.SpreadOption
import com.example.tarot.ui.components.EmptyState
import com.example.tarot.ui.components.SpreadCatalogCard
import com.example.tarot.ui.theme.Gold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpreadsScreen(
    onOpenSpread: (SpreadOption) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }

    val category = selectedCategory?.let { value ->
        SpreadCategory.entries.firstOrNull { it.name == value }
    }

    val filteredSpreads = remember(query, category) {
        val normalized = query.trim()
        SpreadOption.entries.filter { option ->
            val matchesCategory = category == null || option.category == category
            val matchesQuery = normalized.isBlank() ||
                option.title.contains(normalized, ignoreCase = true) ||
                option.description.contains(normalized, ignoreCase = true) ||
                option.category.label.contains(normalized, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Расклады",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 156.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "КАТАЛОГ СХЕМ",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Найдите свою схему",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Двенадцать раскладов для разных вопросов — от короткого знака до глубокого разбора.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("Поиск по названию или теме") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                            )
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Очистить поиск",
                                    )
                                }
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = category == null,
                            onClick = { selectedCategory = null },
                            label = { Text("Все") },
                            colors = categoryChipColors(),
                        )
                        SpreadCategory.entries.forEach { item ->
                            FilterChip(
                                selected = category == item,
                                onClick = {
                                    selectedCategory = if (category == item) null else item.name
                                },
                                label = { Text(item.label) },
                                colors = categoryChipColors(),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Найдено: ${filteredSpreads.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            if (filteredSpreads.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(
                        title = "Ничего не нашлось",
                        subtitle = "Попробуйте другое слово или сбросьте фильтр категории.",
                    )
                }
            } else {
                items(filteredSpreads, key = { it.apiValue }) { option ->
                    SpreadCatalogCard(
                        option = option,
                        onClick = { onOpenSpread(option) },
                    )
                }
            }
        }
    }
}

@Composable
private fun categoryChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Gold.copy(alpha = 0.18f),
    selectedLabelColor = Gold,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
)
