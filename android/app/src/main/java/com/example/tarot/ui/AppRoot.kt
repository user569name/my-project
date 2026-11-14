package com.example.tarot.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tarot.TarotApplication
import com.example.tarot.ui.components.MysticBackground
import com.example.tarot.ui.navigation.AppNavHost
import com.example.tarot.ui.navigation.Routes
import com.example.tarot.ui.theme.DeepSurface
import com.example.tarot.ui.theme.Gold
import com.example.tarot.ui.theme.MutedLavender

@Composable
fun AppRoot() {
    val application = LocalContext.current.applicationContext as TarotApplication
    val repository = remember(application) { application.container.repository }
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute == Routes.HOME ||
        currentRoute == Routes.SPREADS ||
        currentRoute == Routes.PRACTICES ||
        currentRoute == Routes.HISTORY

    MysticBackground {
        // Scaffold прозрачный, поэтому задаём цвет текста явно: иначе
        // заголовки без собственного цвета наследуют чёрный.
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onBackground,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar(
                            modifier = Modifier.drawBehind {
                                drawLine(
                                    color = Gold.copy(alpha = 0.16f),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 1.dp.toPx(),
                                )
                            },
                            containerColor = DeepSurface.copy(alpha = 0.96f),
                            tonalElevation = 0.dp,
                        ) {
                            BottomBarItem(
                                selected = currentRoute == Routes.HOME,
                                label = "Главная",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                    )
                                },
                                onClick = { navigateToTab(navController, Routes.HOME) },
                            )
                            BottomBarItem(
                                selected = currentRoute == Routes.SPREADS,
                                label = "Расклады",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                    )
                                },
                                onClick = { navigateToTab(navController, Routes.SPREADS) },
                            )
                            BottomBarItem(
                                selected = currentRoute == Routes.PRACTICES,
                                label = "Практики",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                    )
                                },
                                onClick = { navigateToTab(navController, Routes.PRACTICES) },
                            )
                            BottomBarItem(
                                selected = currentRoute == Routes.HISTORY,
                                label = "История",
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.List,
                                        contentDescription = null,
                                    )
                                },
                                onClick = { navigateToTab(navController, Routes.HISTORY) },
                            )
                        }
                    }
                },
            ) { padding ->
                AppNavHost(
                    navController = navController,
                    repository = repository,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    selected: Boolean,
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Gold,
            selectedTextColor = Gold,
            indicatorColor = Gold.copy(alpha = 0.16f),
            unselectedIconColor = MutedLavender,
            unselectedTextColor = MutedLavender,
        ),
    )
}

private fun navigateToTab(
    navController: androidx.navigation.NavHostController,
    route: String,
) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
