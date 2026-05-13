package com.example.unimarketplace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.unimarketplace.data.local.AppDatabase
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.data.local.UniMarketDatabase
import com.example.unimarketplace.data.repository.AnnuncioRepositoryImpl
import com.example.unimarketplace.data.repository.UserRepositoryImpl
import com.example.unimarketplace.ui.auth.AuthScreen
import com.example.unimarketplace.ui.auth.viewmodel.AuthViewModel
import com.example.unimarketplace.ui.marketplace.AnnuncioDetailScreen
import com.example.unimarketplace.ui.marketplace.CreateAnnuncioScreen
import com.example.unimarketplace.ui.marketplace.MarketplaceScreen
import com.example.unimarketplace.ui.marketplace.viewmodel.CreateAnnuncioViewModel
import com.example.unimarketplace.ui.marketplace.viewmodel.CreateAnnuncioViewModelFactory
import com.example.unimarketplace.ui.marketplace.viewmodel.MarketplaceViewModel
import com.example.unimarketplace.ui.marketplace.viewmodel.MarketplaceViewModelFactory
import com.example.unimarketplace.ui.profile.ProfileScreen
import com.example.unimarketplace.ui.cart.CartScreen
import android.app.Application
import com.example.unimarketplace.ui.marketplace.AnnuncioDetailViewModel
import com.example.unimarketplace.ui.marketplace.AnnuncioDetailViewModelFactory

/**
 * Screen: Definizione delle rotte dell'applicazione.
 */
sealed class Screen(val route: String) {
    object Marketplace : Screen("marketplace")
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object Cart : Screen("cart")
    object CreateAnnuncio : Screen("create_annuncio")
    object AnnuncioDetail : Screen("annuncio/{annuncioId}") {
        fun createRoute(annuncioId: Long) = "annuncio/$annuncioId"
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val sessionManager = SessionManager(context)
    val uniDatabase = UniMarketDatabase.getInstance(context)
    val annuncioRepository = AnnuncioRepositoryImpl(uniDatabase.annuncioDao())

    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(
                        UserRepositoryImpl(database.userDao(), uniDatabase.utenteDao()),
                        sessionManager
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    val currentUser by authViewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Marketplace.route,
        modifier = modifier
    ) {
        // Schermata Home (Marketplace)
        // Schermata Home (Marketplace)
        composable(Screen.Marketplace.route) {
            val marketplaceViewModel: MarketplaceViewModel = viewModel(
                factory = MarketplaceViewModelFactory(annuncioRepository)
            )
            MarketplaceScreen(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                marketplaceViewModel = marketplaceViewModel,
                userName = currentUser,
                onLogout = { authViewModel.logout() },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onNavigateToCreateAnnuncio = { navController.navigate(Screen.CreateAnnuncio.route) },
                onNavigateToDetail = { annuncioId ->
                    navController.navigate(Screen.AnnuncioDetail.createRoute(annuncioId))
                }
            )
        }

        // Schermata Profilo
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                userName = currentUser ?: ""
            )
        }

        // Schermata Carrello
        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onContinueShopping = { navController.popBackStack(Screen.Marketplace.route, false) },
                isDarkTheme = isDarkTheme
            )
        }

        // Schermata Login
        composable(Screen.Login.route) {
            AuthScreen(
                viewModel = authViewModel,
                isLoginModeInitial = true,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.popBackStack(Screen.Marketplace.route, false)
                }
            )
        }

        // Schermata Registrazione
        composable(Screen.Register.route) {
            AuthScreen(
                viewModel = authViewModel,
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

            val detailViewModel: AnnuncioDetailViewModel = viewModel(
                factory = AnnuncioDetailViewModelFactory(annuncioRepository)
            )

            AnnuncioDetailScreen(
                annuncioId = annuncioId,
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Schermata Crea Annuncio
        composable(Screen.CreateAnnuncio.route) {
            val createAnnuncioViewModel: CreateAnnuncioViewModel = viewModel(
                factory = CreateAnnuncioViewModelFactory(
                    context.applicationContext as Application,
                    annuncioRepository,
                    sessionManager
                )
            )

            val marketplaceViewModel: MarketplaceViewModel = viewModel(
                factory = MarketplaceViewModelFactory(annuncioRepository)
            )

            CreateAnnuncioScreen(
                viewModel = createAnnuncioViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    marketplaceViewModel.refreshAnnunci()
                    navController.popBackStack()
                }
            )
        }
    }
}