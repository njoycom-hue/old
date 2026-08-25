package com.dunoetoktok.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dunoetoktok.app.ui.games.math.MathGameScreen
import com.dunoetoktok.app.ui.games.memory.MemoryGameScreen
import com.dunoetoktok.app.ui.games.oddword.OddWordGameScreen
import com.dunoetoktok.app.ui.games.sequence.SequenceGameScreen
import com.dunoetoktok.app.ui.home.HomeScreen
import com.dunoetoktok.app.ui.settings.SettingsScreen
import com.dunoetoktok.app.ui.settings.SettingsViewModel
import com.dunoetoktok.app.ui.stats.StatsScreen

@Composable
fun NavGraph(navController: NavHostController, settingsViewModel: SettingsViewModel) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigate = { route -> navController.navigate(route) },
            )
        }
        composable(Routes.MEMORY_GAME) {
            MemoryGameScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SEQUENCE_GAME) {
            SequenceGameScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.MATH_GAME) {
            MathGameScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.ODD_WORD_GAME) {
            OddWordGameScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.STATS) {
            StatsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() }, viewModel = settingsViewModel)
        }
    }
}
