package com.example.unimarketplace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.unimarketplace.ui.auth.AuthScreen
import com.example.unimarketplace.ui.marketplace.AnnuncioDetailScreen
import com.example.unimarketplace.ui.marketplace.MarketplaceScreen

/**
 * Screen: Definizione delle rotte dell'applicazione.
 */
sealed class Screen(val route: String) {
    object Marketplace : Screen("marketplace")
    object Login : Screen("login")
    object Register : Screen("register")
    object AnnuncioDetail : Screen("annuncio/{annuncioId}") {
        fun createRoute(annuncioId: Long) = "annuncio/$annuncioId"
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Marketplace.route,
        modifier = modifier
    ) {
        // Schermata Home (Marketplace)
        composable(Screen.Marketplace.route) {
            MarketplaceScreen(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // Schermata Login
        composable(Screen.Login.route) {
            AuthScreen(
                isLoginModeInitial = true,
                onBack = { navController.popBackStack() }
            )
        }

        // Schermata Registrazione
        composable(Screen.Register.route) {
            AuthScreen(
                isLoginModeInitial = false,
                onBack = { navController.popBackStack() }
            )
        }

        // Schermata Dettaglio Annuncio
        composable(
            Screen.AnnuncioDetail.route,
            arguments = listOf(navArgument("annuncioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val annuncioId = backStackEntry.arguments?.getLong("annuncioId") ?: return@composable
            AnnuncioDetailScreen(
                annuncioId = annuncioId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
