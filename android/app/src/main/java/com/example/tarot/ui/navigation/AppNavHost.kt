package com.example.tarot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tarot.data.TarotRepository
import com.example.tarot.ui.auth.AuthScreen
import com.example.tarot.ui.history.HistoryScreen
import com.example.tarot.ui.history.ReadingDetailScreen
import com.example.tarot.ui.home.HomeScreen

object Routes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val HISTORY = "history"
    const val READING = "reading/{readingId}"

    fun reading(readingId: String): String = "reading/$readingId"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    repository: TarotRepository,
    modifier: Modifier = Modifier,
) {
    val startDestination = if (repository.isLoggedIn) Routes.HOME else Routes.AUTH

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                repository = repository,
                onAuthenticated = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                repository = repository,
                onLoggedOut = {
                    repository.logout()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
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
