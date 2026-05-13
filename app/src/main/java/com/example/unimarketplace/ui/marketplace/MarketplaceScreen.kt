package com.example.unimarketplace.ui.marketplace

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.ui.marketplace.viewmodel.MarketplaceViewModel
import com.example.unimarketplace.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    marketplaceViewModel: MarketplaceViewModel,
    userName: String? = null,
    onLogout: () -> Unit = {},
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCreateAnnuncio: () -> Unit,
    onNavigateToDetail: (Long) -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Stati dai filtri del ViewModel
    val categoriaSelezionata by marketplaceViewModel.categoriaSelezionata.collectAsState()
    val condizioniSelezionate by marketplaceViewModel.condizioniSelezionate.collectAsState()
    val prezzoMassimo by marketplaceViewModel.prezzoMassimo.collectAsState()

    // Lista filtrata dal ViewModel
    val annunci by marketplaceViewModel.annunciFiltrati.collectAsState()
    val preferitiIds by marketplaceViewModel.preferitiIds.collectAsState()
    val carrelloIds by marketplaceViewModel.carrelloIds.collectAsState()
    val errorEvent by marketplaceViewModel.errorEvent.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorEvent) {
        errorEvent?.let {
            snackbarHostState.showSnackbar(it)
            marketplaceViewModel.clearError()
        }
    }

    // Testo della barra di ricerca
    var testoRicerca by remember { mutableStateOf("") }

    // Stato per compattare i filtri durante lo scroll
    val isScrolled = listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100
    val filtriCompatti by animateDpAsState(if (isScrolled) 0.dp else 1.dp, label = "filtri")

    // Per far apparire il drawer a destra, forziamo RTL
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            scrimColor = if (isDarkTheme) Color.Black.copy(alpha = 0.7f) else DrawerDefaults.scrimColor,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .width(320.dp)
                        .then(
                            if (isDarkTheme) Modifier.drawBehind {
                                drawLine(
                                    color = Color(0xFF334155),
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, size.height),
                                    strokeWidth = 2.dp.toPx()
                                )
                            } else Modifier
                        ),
                    drawerShape = RoundedCornerShape(0.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        DrawerContent(
                            onClose = { scope.launch { drawerState.close() } },
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = onThemeToggle,
                            userName = userName,
                            onLogout = onLogout,
                            onNavigateToLogin = onNavigateToLogin,
                            onNavigateToRegister = onNavigateToRegister,
                            onNavigateToProfile = onNavigateToProfile,
                            onNavigateToCart = onNavigateToCart,
                            onNavigateToFavorites = onNavigateToFavorites
                        )
                    }
                }
            }
        ) {
            // Contenuto principale in LTR
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    modifier = modifier,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        Column {
                            // TopBar fissa
                            TopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(Color(0xFF2563EB), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "UniboMarket",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = {
                                            if (userName != null) {
                                                onNavigateToCreateAnnuncio()
                                            } else {
                                                onNavigateToLogin()
                                            }
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            // Barra di ricerca sempre visibile (compatta)
                            OutlinedTextField(
                                value = testoRicerca,
                                onValueChange = {
                                    testoRicerca = it
                                    marketplaceViewModel.setQueryRicerca(it)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = if (isScrolled) 8.dp else 12.dp)
                                    .height(if (isScrolled) 48.dp else 56.dp),
                                placeholder = { Text(
                                    text = if (isScrolled) "Cerca..." else "Cerca libri, appunti, corsi...",
                                    color = Color.Gray,
                                    fontSize = if (isScrolled) 14.sp else 16.sp
                                ) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(if (isScrolled) 18.dp else 24.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (testoRicerca.isNotEmpty()) {
                                        IconButton(onClick = {
                                            testoRicerca = ""
                                            marketplaceViewModel.setQueryRicerca("")
                                        }) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Cancella",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(if (isScrolled) 16.dp else 20.dp)
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = if (isScrolled) 13.sp else 16.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                    focusedContainerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color(0xFFCBD5E1)
                                )
                            )

                            // Filtri compatti o espansi
                            if (!isScrolled) {
                                // FILTRI ESPANSI
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Categoria
                                            var expandedFacolta by remember { mutableStateOf(false) }
                                            Box(modifier = Modifier.weight(1f)) {
                                                FilterChipCompact(
                                                    label = "Categoria",
                                                    value = if (categoriaSelezionata == "Tutte") "Tutte" else categoriaSelezionata,
                                                    isDarkTheme = isDarkTheme,
                                                    onClick = { expandedFacolta = true }
                                                )
                                                DropdownMenu(
                                                    expanded = expandedFacolta,
                                                    onDismissRequest = { expandedFacolta = false }
                                                ) {
                                                    val opzioni = listOf("Tutte") + com.example.unimarketplace.domain.model.Categoria.entries.map { it.displayName }
                                                    opzioni.forEach { option ->
                                                        DropdownMenuItem(
                                                            text = { Text(option, fontSize = 13.sp) },
                                                            onClick = {
                                                                marketplaceViewModel.setCategoria(option)
                                                                expandedFacolta = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            // Condizioni
                                            var expandedCondizione by remember { mutableStateOf(false) }
                                            Box(modifier = Modifier.weight(1f)) {
                                                FilterChipCompact(
                                                    label = "Condizione",
                                                    value = if (condizioniSelezionate == "Tutte") "Tutte" else condizioniSelezionate,
                                                    isDarkTheme = isDarkTheme,
                                                    onClick = { expandedCondizione = true }
                                                )
                                                DropdownMenu(
                                                    expanded = expandedCondizione,
                                                    onDismissRequest = { expandedCondizione = false }
                                                ) {
                                                    listOf("Tutte", "NUOVO", "OTTIMO", "BUONO", "USATO").forEach { option ->
                                                        DropdownMenuItem(
                                                            text = { Text(option, fontSize = 13.sp) },
                                                            onClick = {
                                                                marketplaceViewModel.setCondizioni(option)
                                                                expandedCondizione = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Prezzo slider
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Max €${prezzoMassimo.toInt()}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.width(70.dp)
                                            )
                                            Slider(
                                                value = prezzoMassimo,
                                                onValueChange = { marketplaceViewModel.setPrezzoMassimo(it) },
                                                valueRange = 0f..200f,
                                                modifier = Modifier.weight(1f),
                                                colors = SliderDefaults.colors(
                                                    thumbColor = Color.White,
                                                    activeTrackColor = if (isDarkTheme) Color(0xFF3B82F6) else Color(0xFF0F172A),
                                                    inactiveTrackColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                                )
                                            )
                                        }
                                    }
                                }
                            } else {
                                // FILTRI COMPATTI (solo chips orizzontali)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (categoriaSelezionata != "Tutte") {
                                        AssistChip(
                                            onClick = { marketplaceViewModel.setCategoria("Tutte") },
                                            label = { Text(categoriaSelezionata, fontSize = 11.sp) },
                                            trailingIcon = {
                                                Icon(Icons.Default.Close, contentDescription = "Rimuovi", modifier = Modifier.size(14.dp))
                                            },
                                            modifier = Modifier.height(28.dp)
                                        )
                                    }
                                    if (condizioniSelezionate != "Tutte") {
                                        AssistChip(
                                            onClick = { marketplaceViewModel.setCondizioni("Tutte") },
                                            label = { Text(condizioniSelezionate, fontSize = 11.sp) },
                                            trailingIcon = {
                                                Icon(Icons.Default.Close, contentDescription = "Rimuovi", modifier = Modifier.size(14.dp))
                                            },
                                            modifier = Modifier.height(28.dp)
                                        )
                                    }
                                    if (prezzoMassimo < 200f) {
                                        AssistChip(
                                            onClick = { marketplaceViewModel.setPrezzoMassimo(200f) },
                                            label = { Text("Max €${prezzoMassimo.toInt()}", fontSize = 11.sp) },
                                            trailingIcon = {
                                                Icon(Icons.Default.Close, contentDescription = "Rimuovi", modifier = Modifier.size(14.dp))
                                            },
                                            modifier = Modifier.height(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        // Numero annunci trovati
                        Text(
                            text = "${annunci.size} annunci trovati",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        // Lista annunci
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
                        ) {
                            items(annunci.size) { index ->
                                val annuncio = annunci[index]
                                val isFavorite = preferitiIds.contains(annuncio.id)
                                val isInCart = carrelloIds.contains(annuncio.id)
                                MarketplaceItemCard(
                                    isDarkTheme = isDarkTheme,
                                    annuncio = annuncio,
                                    isFavorite = isFavorite,
                                    isInCart = isInCart,
                                    onToggleFavorite = { marketplaceViewModel.togglePreferito(annuncio.id) },
                                    onClick = { onNavigateToDetail(annuncio.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipCompact(label: String, value: String, isDarkTheme: Boolean, onClick: () -> Unit) {
    Column {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 4.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9).copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp)
                )
                .border(1.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun MarketplaceItemCard(
    isDarkTheme: Boolean,
    annuncio: Annuncio,
    isFavorite: Boolean = false,
    isInCart: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
        )
    ) {
        Column {
            // Immagine di copertina + Cuoricino
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                if (annuncio.immagini.isNotEmpty()) {
                    AsyncImage(
                        model = annuncio.immagini.first(),
                        contentDescription = "Immagine annuncio",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Nessuna immagine",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Badge "Già nel carrello"
                if (isInCart) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        color = Color(0xFF10B981),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Nel carrello",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Tasto Preferiti in alto a destra
                IconButton(
                    onClick = { onToggleFavorite() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.7f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Preferiti",
                        tint = if (isFavorite) Color.Red else Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = annuncio.titolo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "€${String.format("%.2f", annuncio.prezzo)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = annuncio.descrizione,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(annuncio.condizioni.name, fontSize = 11.sp) },
                        modifier = Modifier.height(24.dp)
                    )
                    SuggestionChip(
                        onClick = { },
                        label = { Text(annuncio.categoria.displayName, fontSize = 11.sp) },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    onClose: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    userName: String?,
    onLogout: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 0.dp, bottom = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "UniboMarket",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0), thickness = 1.dp)

        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 16.dp)) {
            if (userName != null) {
                // User info and Logout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onNavigateToProfile()
                            onClose()
                        }
                        .padding(bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF2563EB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = userName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Studente",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Button(
                    onClick = {
                        onLogout()
                        onClose()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.1f),
                        contentColor = Color.Red
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Logout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else {
                // Auth Buttons
                Button(
                    onClick = {
                        onClose()
                        onNavigateToLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Color.White else Color(0xFF020617),
                        contentColor = if (isDarkTheme) Color.Black else Color.White
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Login", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        onClose()
                        onNavigateToRegister()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Registrati",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Menu Items
            DrawerMenuItem(
                icon = Icons.Outlined.FavoriteBorder, 
                label = "Preferiti",
                onClick = {
                    onNavigateToFavorites()
                    onClose()
                }
            )
            DrawerMenuItem(
                icon = Icons.Outlined.ShoppingCart,
                label = "Carrello",
                onClick = {
                    onNavigateToCart()
                    onClose()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            DrawerMenuItem(icon = Icons.AutoMirrored.Outlined.HelpOutline, label = "Aiuto e Supporto")

            Spacer(modifier = Modifier.weight(1f))

            // Theme Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onThemeToggle() }
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = if (isDarkTheme) Color(0xFF3B82F6) else Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (isDarkTheme) "Modalità Scura" else "Modalità Chiara",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onThemeToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2563EB)
                    )
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}