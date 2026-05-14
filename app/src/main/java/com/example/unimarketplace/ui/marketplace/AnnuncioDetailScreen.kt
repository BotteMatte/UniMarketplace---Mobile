package com.example.unimarketplace.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.unimarketplace.domain.model.Annuncio
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.animation.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnuncioDetailScreen(
    annuncioId: Long,
    viewModel: AnnuncioDetailViewModel,
    isDarkTheme: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit = {},
    onAnnuncioDeleted: () -> Unit = {}
) {
    val annuncio by viewModel.annuncio.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isAddedToCart by viewModel.isAddedToCart.collectAsState()
    val isInCart by viewModel.isInCart.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val isOwnAnnuncio by viewModel.isOwnAnnuncio.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Stato per il dialog di conferma eliminazione
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(annuncioId) {
        viewModel.loadAnnuncio(annuncioId)
    }

    LaunchedEffect(isAddedToCart) {
        if (isAddedToCart) {
            snackbarHostState.showSnackbar("Articolo aggiunto al carrello")
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    var currentImageIndex by remember { mutableIntStateOf(0) }
    var fullScreenImage by remember { mutableStateOf(false) }

    // Dialog di conferma eliminazione
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Elimina annuncio") },
            text = { Text("Sei sicuro di voler eliminare questo annuncio? L'operazione è irreversibile.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminaAnnuncio()
                        showDeleteDialog = false
                        onAnnuncioDeleted()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annulla")
                }
            },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Dettaglio Annuncio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Preferiti",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (annuncio != null && !isLoading) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    if (isOwnAnnuncio) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (annuncio?.isVenduto == true) {
                                Text(
                                    "Questo articolo è stato venduto",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                OutlinedButton(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Elimina annuncio")
                                }
                            } else {
                                Text(
                                    "Questo è un tuo annuncio",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                // Pulsante Modifica
                                Button(
                                    onClick = { onNavigateToEdit(annuncio!!.id) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Modifica annuncio")
                                }
                                // Pulsante Segna come venduto
                                Button(
                                    onClick = { viewModel.segnaComeVenduto() },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Segna come venduto")
                                }
                                // Pulsante Elimina
                                OutlinedButton(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Elimina annuncio")
                                }
                            }
                        }
                    } else {
                        if (annuncio?.isVenduto == true) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "ARTICOLO VENDUTO",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Red,
                                    fontSize = 18.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (isInCart) viewModel.rimuoviDalCarrello()
                                    else viewModel.aggiungiAlCarrello()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:${annuncio!!.venditoreEmail}")
                                            putExtra(Intent.EXTRA_SUBJECT, "Interesse per: ${annuncio!!.titolo}")
                                            putExtra(Intent.EXTRA_TEXT, "Ciao ${annuncio!!.venditoreNome}, sono interessato al tuo annuncio '${annuncio!!.titolo}'.")
                                        }
                                        try {
                                            context.startActivity(Intent.createChooser(intent, "Invia email..."))
                                        } catch (e: Exception) {
                                            // Fallback se non ci sono app email
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFEA4335), // Gmail Red
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Contatta il venditore", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                Button(
                                    onClick = { 
                                        if (isInCart) viewModel.rimuoviDalCarrello() 
                                        else viewModel.aggiungiAlCarrello() 
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isInCart) Color(0xFFEF4444) else (if (isDarkTheme) Color.White else Color(0xFF0F172A)),
                                        contentColor = if (isInCart) Color.White else (if (isDarkTheme) Color.Black else Color.White)
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isInCart) Icons.Default.RemoveShoppingCart else Icons.Default.AddShoppingCart, 
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isInCart) "Rimuovi dal carrello" else "Aggiungi al carrello", 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (annuncio != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // CAROSELLO IMMAGINI
                if (annuncio!!.immagini.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(350.dp).clickable { fullScreenImage = true }) {
                        AsyncImage(
                            model = annuncio!!.immagini[currentImageIndex],
                            contentDescription = "Immagine ${currentImageIndex + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        if (annuncio!!.immagini.size > 1) {
                            if (currentImageIndex > 0) {
                                IconButton(
                                    onClick = { currentImageIndex-- },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(8.dp)
                                        .size(40.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Precedente", tint = Color.White)
                                }
                            }
                            if (currentImageIndex < annuncio!!.immagini.size - 1) {
                                IconButton(
                                    onClick = { currentImageIndex++ },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(8.dp)
                                        .size(40.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Successiva", tint = Color.White)
                                }
                            }
                        }

                        if (annuncio!!.immagini.size > 1) {
                            Row(
                                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(annuncio!!.immagini.size) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(if (index == currentImageIndex) 10.dp else 8.dp)
                                            .background(
                                                if (index == currentImageIndex) Color.White else Color.White.copy(alpha = 0.5f),
                                                CircleShape
                                            )
                                    )
                                }
                            }
                        }

                        if (annuncio!!.immagini.size > 1) {
                            Text(
                                text = "${currentImageIndex + 1}/${annuncio!!.immagini.size}",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (annuncio!!.immagini.size > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(annuncio!!.immagini) { index, immagine ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (index == currentImageIndex) Color(0xFF2563EB) else Color.Gray.copy(alpha = 0.3f)
                                        )
                                        .clickable { currentImageIndex = index }
                                ) {
                                    AsyncImage(
                                        model = immagine,
                                        contentDescription = "Miniatura ${index + 1}",
                                        modifier = Modifier.fillMaxSize().padding(if (index == currentImageIndex) 2.dp else 0.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(250.dp).background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Nessuna immagine", tint = Color.Gray, modifier = Modifier.size(64.dp))
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = annuncio!!.titolo, fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.weight(1f))
                        Text(text = "€${String.format("%.2f", annuncio!!.prezzo)}", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF2563EB))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text(annuncio!!.categoria.displayName) },
                            leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(annuncio!!.condizioni.name) },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }

                    HorizontalDivider()

                    Text("Descrizione", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = annuncio!!.descrizione, fontSize = 16.sp, color = Color.Gray)

                    HorizontalDivider()

                    Text("Venditore", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = annuncio!!.venditoreNome, fontSize = 16.sp, color = Color.Gray)
                    }

                    HorizontalDivider()

                    if (annuncio!!.latitudine != 0.0 && annuncio!!.longitudine != 0.0) {
                        Text("Posizione", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (annuncio!!.indirizzo.isNotEmpty()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(annuncio!!.indirizzo, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                if (annuncio!!.citta.isNotEmpty()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(annuncio!!.citta, fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val posizione = LatLng(annuncio!!.latitudine, annuncio!!.longitudine)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(posizione, 15f)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState
                            ) {
                                Marker(state = MarkerState(position = posizione), title = annuncio!!.titolo)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Annuncio non trovato", color = Color.Gray)
            }
        }
    }

    // ==================== DIALOG FULLSCREEN IMMAGINE ====================
    if (fullScreenImage && annuncio != null && annuncio!!.immagini.isNotEmpty()) {
        val fullPagerState = rememberPagerState(
            initialPage = currentImageIndex,
            pageCount = { annuncio!!.immagini.size }
        )

        Dialog(
            onDismissRequest = { fullScreenImage = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                HorizontalPager(state = fullPagerState, modifier = Modifier.fillMaxSize()) { page ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = annuncio!!.immagini[page],
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                IconButton(
                    onClick = { fullScreenImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Chiudi", tint = Color.White)
                }

                if (annuncio!!.immagini.size > 1) {
                    Row(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(annuncio!!.immagini.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == fullPagerState.currentPage) 10.dp else 7.dp)
                                    .background(
                                        if (index == fullPagerState.currentPage) Color.White else Color.White.copy(alpha = 0.4f),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${fullPagerState.currentPage + 1} / ${annuncio!!.immagini.size}",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}