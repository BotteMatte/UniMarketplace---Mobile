package com.example.unimarketplace.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun MarketplaceScreenPreview() {
    MaterialTheme {
        MarketplaceScreen(
            isDarkTheme = false,
            onThemeToggle = {},
            userName = "Mario Rossi",
            onLogout = {},
            onNavigateToLogin = {},
            onNavigateToRegister = {},
            onNavigateToProfile = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    userName: String? = null,
    onLogout: () -> Unit = {},
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Stati per i filtri selezionabili
    var selectedFacolta by remember { mutableStateOf("Tutte") }
    var selectedCondizione by remember { mutableStateOf("Tutte") }
    var maxPrice by remember { mutableFloatStateOf(100f) }

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
                                // Disegna solo la linea sul lato sinistro del drawer (che è a destra nello schermo)
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
                    // All'interno del drawer torniamo a LTR per il contenuto
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        DrawerContent(
                            onClose = { scope.launch { drawerState.close() } },
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = onThemeToggle,
                            userName = userName,
                            onLogout = onLogout,
                            onNavigateToLogin = onNavigateToLogin,
                            onNavigateToRegister = onNavigateToRegister,
                            onNavigateToProfile = onNavigateToProfile
                        )
                    }
                }
            }
        ) {
            // Contenuto principale in LTR
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    modifier = modifier,
                    topBar = {
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
                                    onClick = { /* TODO */ },
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
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Search Bar
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            placeholder = { Text("Cerca libri, appunti, corsi...", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                focusedContainerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Filters Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = "Filtri di ricerca",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                // Facoltà Dropdown (Interattivo)
                                var expandedFacolta by remember { mutableStateOf(false) }
                                Box {
                                    FilterDropdown(
                                        label = "Facoltà",
                                        value = selectedFacolta,
                                        isDarkTheme = isDarkTheme,
                                        onClick = { expandedFacolta = true }
                                    )
                                    DropdownMenu(
                                        expanded = expandedFacolta,
                                        onDismissRequest = { expandedFacolta = false }
                                    ) {
                                        listOf("Tutte", "Ingegneria", "Economia", "Medicina", "Lettere").forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    selectedFacolta = option
                                                    expandedFacolta = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Condizione Dropdown (Interattivo)
                                var expandedCondizione by remember { mutableStateOf(false) }
                                Box {
                                    FilterDropdown(
                                        label = "Condizione",
                                        value = selectedCondizione,
                                        isDarkTheme = isDarkTheme,
                                        onClick = { expandedCondizione = true }
                                    )
                                    DropdownMenu(
                                        expanded = expandedCondizione,
                                        onDismissRequest = { expandedCondizione = false }
                                    ) {
                                        listOf("Tutte", "Nuovo", "Ottimo", "Buono", "Usato").forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    selectedCondizione = option
                                                    expandedCondizione = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Prezzo massimo: €${maxPrice.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Slider(
                                    value = maxPrice,
                                    onValueChange = { maxPrice = it },
                                    valueRange = 0f..200f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        activeTrackColor = if (isDarkTheme) Color(0xFF3B82F6) else Color(0xFF0F172A),
                                        inactiveTrackColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "5 annunci trovati",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Item List (Mock)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(5) {
                                MarketplaceItemCard(isDarkTheme)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(label: String, value: String, isDarkTheme: Boolean, onClick: () -> Unit) {
    Column {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp),
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = value, color = if (isDarkTheme) Color.LightGray else Color.Gray)
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
fun MarketplaceItemCard(isDarkTheme: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
    ) {
        Column {
            // Placeholder for Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0))
            ) {
                Text("Immagine Libro", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Analisi Matematica 1",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "€25.00",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "G. Bramanti, C. Pagani, S. Salsa",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                SuggestionChip(
                    onClick = { },
                    label = { Text("Ottime condizioni") }
                )
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
    onNavigateToProfile: () -> Unit
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
            DrawerMenuItem(icon = Icons.Outlined.FavoriteBorder, label = "Preferiti")
            DrawerMenuItem(icon = Icons.Outlined.ShoppingCart, label = "Carrello")
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Rimosso Pannello Admin come richiesto
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
fun DrawerMenuItem(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* TODO */ }
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
