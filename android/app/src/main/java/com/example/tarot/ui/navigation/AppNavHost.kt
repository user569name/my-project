package com.example.tarot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tarot.data.TarotRepository
import com.example.tarot.domain.SpreadOption
import com.example.tarot.ui.history.HistoryScreen
import com.example.tarot.ui.history.ReadingDetailScreen
import com.example.tarot.ui.home.HomeScreen
import com.example.tarot.ui.reading.ReadingScreen
import com.example.tarot.ui.spreads.SpreadsScreen

object Routes {
    const val HOME = "home"
    const val SPREADS = "spreads"
    const val HISTORY = "history"
    const val READING = "reading/{readingId}"
    const val NEW_READING = "new-reading/{spreadId}"

    fun reading(readingId: String): String = "reading/$readingId"

    fun newReading(spread: SpreadOption): String = "new-reading/${spread.apiValue}"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    repository: TarotRepository,
    modifier: Modifier = Modifier,
) {
    val startDestination = Routes.HOME

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                repository = repository,
                onOpenSpreads = { navController.navigate(Routes.SPREADS) },
                onOpenSpread = { spread ->
                    navController.navigate(Routes.newReading(spread))
                },
                onOpenReading = { readingId ->
                    navController.navigate(Routes.reading(readingId))
                },
            )
        }

        composable(Routes.SPREADS) {
            SpreadsScreen(
                onOpenSpread = { spread ->
                    navController.navigate(Routes.newReading(spread))
                },
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                repository = repository,
                onOpenReading = { readingId ->
                    navController.navigate(Routes.reading(readingId))
                },
            )
        }

        composable(
            route = Routes.NEW_READING,
            arguments = listOf(navArgument("spreadId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val spread = SpreadOption.fromApiValue(
                backStackEntry.arguments?.getString("spreadId").orEmpty(),
            )
            ReadingScreen(
                repository = repository,
                spread = spread,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.READING,
            arguments = listOf(navArgument("readingId") { type = NavType.StringType }),
        ) { backStackEntry ->
            ReadingDetailScreen(
                repository = repository,
                readingId = backStackEntry.arguments?.getString("readingId").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }
    }
}
