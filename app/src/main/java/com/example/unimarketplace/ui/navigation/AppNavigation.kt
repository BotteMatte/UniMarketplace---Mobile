package com.example.unimarketplace.ui.navigation

import android.app.Application
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.unimarketplace.data.repository.*
import com.example.unimarketplace.domain.model.BadgeType
import com.example.unimarketplace.ui.auth.AuthScreen
import com.example.unimarketplace.ui.auth.viewmodel.AuthViewModel
import com.example.unimarketplace.ui.cart.CartScreen
import com.example.unimarketplace.ui.cart.viewmodel.CartViewModel
import com.example.unimarketplace.ui.cart.viewmodel.CartViewModelFactory
import com.example.unimarketplace.ui.components.SupportScreen
import com.example.unimarketplace.ui.favorites.FavoritesScreen
import com.example.unimarketplace.ui.favorites.viewmodel.FavoritesViewModel
import com.example.unimarketplace.ui.favorites.viewmodel.FavoritesViewModelFactory
import com.example.unimarketplace.ui.marketplace.*
import com.example.unimarketplace.ui.marketplace.viewmodel.CreateAnnuncioViewModel
import com.example.unimarketplace.ui.marketplace.viewmodel.CreateAnnuncioViewModelFactory
import com.example.unimarketplace.ui.marketplace.viewmodel.MarketplaceViewModel
import com.example.unimarketplace.ui.marketplace.viewmodel.MarketplaceViewModelFactory
import com.example.unimarketplace.ui.profile.ProfileScreen
import com.example.unimarketplace.ui.profile.viewmodel.ProfileViewModel
import com.example.unimarketplace.ui.profile.viewmodel.ProfileViewModelFactory
import com.example.unimarketplace.util.BadgeManager

sealed class Screen(val route: String) {
    object Marketplace : Screen("marketplace")
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object Cart : Screen("cart")
    object Favorites : Screen("favorites")
    object CreateAnnuncio : Screen("create_annuncio")
    object EditAnnuncio : Screen("edit_annuncio/{annuncioId}") {
        fun createRoute(annuncioId: Long) = "edit_annuncio/$annuncioId"
    }
    object AnnuncioDetail : Screen("annuncio/{annuncioId}") {
        fun createRoute(annuncioId: Long) = "annuncio/$annuncioId"
    }
    object Support : Screen("support")
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
    val preferitiRepository = PreferitiRepositoryImpl(uniDatabase.preferitiDao())
    val carrelloRepository = CarrelloRepositoryImpl(uniDatabase.carrelloDao())
    val badgeRepository = BadgeRepositoryImpl(uniDatabase.badgeDao())
    val badgeManager = remember { BadgeManager(badgeRepository, annuncioRepository) }

    // Popup per i badge guadagnati
    var showBadgeDialog by remember { mutableStateOf<BadgeType?>(null) }

    LaunchedEffect(badgeManager) {
        badgeManager.newBadgeEarned.collect { badgeType ->
            showBadgeDialog = badgeType
        }
    }

    if (showBadgeDialog != null) {
        AlertDialog(
            onDismissRequest = { showBadgeDialog = null },
            title = { Text("Nuovo Badge Ricevuto!") },
            text = { Text("Hai ottenuto il badge: ${showBadgeDialog?.titolo}!\nVai a controllare nel tuo profilo.") },
            confirmButton = {
                Button(onClick = { showBadgeDialog = null }) {
                    Text("OK")
                }
            }
        )
    }

    // Check Animale Notturno
    LaunchedEffect(isDarkTheme) {
        val userId = sessionManager.getUserId()
        if (userId != null && isDarkTheme) {
            badgeManager.checkAnimaleNotturno(userId, true)
        }
    }

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

    // ViewModel condivisi
    val marketplaceViewModel: MarketplaceViewModel = viewModel(
        factory = MarketplaceViewModelFactory(annuncioRepository, preferitiRepository, carrelloRepository, sessionManager)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Marketplace.route,
        modifier = modifier
    ) {
        composable(Screen.Marketplace.route) {
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
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onNavigateToCreateAnnuncio = { navController.navigate(Screen.CreateAnnuncio.route) },
                onNavigateToDetail = { annuncioId ->
                    navController.navigate(Screen.AnnuncioDetail.createRoute(annuncioId))
                },
                onNavigateToSupport = { navController.navigate(Screen.Support.route) }
            )
        }

        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(annuncioRepository, badgeRepository, badgeManager, sessionManager)
            )
            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditAnnuncio.createRoute(id)) },
                onNavigateToDetail = { id -> navController.navigate(Screen.AnnuncioDetail.createRoute(id)) },
                userName = currentUser ?: ""
            )
        }

        composable(Screen.Cart.route) {
            val cartViewModel: CartViewModel = viewModel(
                factory = CartViewModelFactory(carrelloRepository, annuncioRepository, badgeManager, sessionManager)
            )
            CartScreen(
                viewModel = cartViewModel,
                onNavigateBack = { navController.popBackStack() },
                onContinueShopping = { navController.popBackStack(Screen.Marketplace.route, false) },
                isDarkTheme = isDarkTheme
            )
        }

        composable(Screen.Favorites.route) {
            val favoritesViewModel: FavoritesViewModel = viewModel(
                factory = FavoritesViewModelFactory(preferitiRepository, annuncioRepository, sessionManager)
            )
            FavoritesScreen(
                viewModel = favoritesViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.popBackStack(Screen.Marketplace.route, false) },
                onNavigateToDetail = { annuncioId ->
                    navController.navigate(Screen.AnnuncioDetail.createRoute(annuncioId))
                },
                isDarkTheme = isDarkTheme
            )
        }

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

        composable(Screen.Register.route) {
            AuthScreen(
                viewModel = authViewModel,
                isLoginModeInitial = false,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Screen.AnnuncioDetail.route,
            arguments = listOf(navArgument("annuncioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val annuncioId = backStackEntry.arguments?.getLong("annuncioId") ?: return@composable

            val detailViewModel: AnnuncioDetailViewModel = viewModel(
                factory = AnnuncioDetailViewModelFactory(annuncioRepository, carrelloRepository, preferitiRepository, badgeManager, sessionManager)
            )

            AnnuncioDetailScreen(
                annuncioId = annuncioId,
                viewModel = detailViewModel,
                isDarkTheme = isDarkTheme,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditAnnuncio.createRoute(id)) },
                onAnnuncioDeleted = {
                    marketplaceViewModel.refreshAnnunci()
                    navController.popBackStack()
                }
            )
        }

        // Schermata Modifica Annuncio
        composable(
            Screen.EditAnnuncio.route,
            arguments = listOf(navArgument("annuncioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val annuncioId = backStackEntry.arguments?.getLong("annuncioId") ?: return@composable

            val editViewModel: CreateAnnuncioViewModel = viewModel(
                factory = CreateAnnuncioViewModelFactory(
                    context.applicationContext as Application,
                    annuncioRepository,
                    badgeManager,
                    sessionManager
                )
            )

            LaunchedEffect(annuncioId) {
                editViewModel.loadAnnuncioForEdit(annuncioId)
            }

            CreateAnnuncioScreen(
                viewModel = editViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    marketplaceViewModel.refreshAnnunci()
                    navController.popBackStack()
                },
                isEditMode = true
            )
        }

        // Schermata Crea Annuncio
        composable(Screen.CreateAnnuncio.route) {
            val createAnnuncioViewModel: CreateAnnuncioViewModel = viewModel(
                factory = CreateAnnuncioViewModelFactory(
                    context.applicationContext as Application,
                    annuncioRepository,
                    badgeManager,
                    sessionManager
                )
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

        // Schermata Supporto
        composable(Screen.Support.route) {
            SupportScreen(
                onBack = { navController.popBackStack() },
                isDarkTheme = isDarkTheme
            )
        }
    }
}