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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnuncioDetailScreen(
    annuncioId: Long,
    viewModel: AnnuncioDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val annuncio by viewModel.annuncio.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(annuncioId) {
        viewModel.loadAnnuncio(annuncioId)
    }

    // Indice dell'immagine attualmente visualizzata
    var currentImageIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dettaglio Annuncio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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
                    Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                        // Immagine principale
                        AsyncImage(
                            model = annuncio!!.immagini[currentImageIndex],
                            contentDescription = "Immagine ${currentImageIndex + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        // Frecce per scorrere
                        if (annuncio!!.immagini.size > 1) {
                            // Freccia sinistra
                            if (currentImageIndex > 0) {
                                IconButton(
                                    onClick = { currentImageIndex-- },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(8.dp)
                                        .size(40.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowLeft,
                                        contentDescription = "Precedente",
                                        tint = Color.White
                                    )
                                }
                            }

                            // Freccia destra
                            if (currentImageIndex < annuncio!!.immagini.size - 1) {
                                IconButton(
                                    onClick = { currentImageIndex++ },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(8.dp)
                                        .size(40.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Successiva",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        // Indicatore pagina (pallini)
                        if (annuncio!!.immagini.size > 1) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(annuncio!!.immagini.size) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(if (index == currentImageIndex) 10.dp else 8.dp)
                                            .background(
                                                if (index == currentImageIndex) Color.White
                                                else Color.White.copy(alpha = 0.5f),
                                                CircleShape
                                            )
                                    )
                                }
                            }
                        }

                        // Contatore immagini
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

                    // Miniature in basso (LazyRow orizzontale)
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
                                            if (index == currentImageIndex) Color(0xFF2563EB)
                                            else Color.Gray.copy(alpha = 0.3f)
                                        )
                                        .clickable { currentImageIndex = index }
                                ) {
                                    AsyncImage(
                                        model = immagine,
                                        contentDescription = "Miniatura ${index + 1}",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(if (index == currentImageIndex) 2.dp else 0.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Nessuna immagine
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Nessuna immagine",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Titolo e Prezzo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = annuncio!!.titolo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "€${String.format("%.2f", annuncio!!.prezzo)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = Color(0xFF2563EB)
                        )
                    }

                    // Chip Categoria e Condizioni
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text(annuncio!!.categoria.name) },
                            leadingIcon = {
                                Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(annuncio!!.condizioni.name) },
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }

                    HorizontalDivider()

                    // Descrizione
                    Text("Descrizione", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = annuncio!!.descrizione,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    HorizontalDivider()

                    // Venditore
                    Text("Venditore", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = annuncio!!.venditoreNome,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    HorizontalDivider()

                    // Posizione (se disponibile)
                    if (annuncio!!.latitudine != 0.0 && annuncio!!.longitudine != 0.0) {
                        Text("Posizione", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                        // Informazioni indirizzo
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

                        // Mappa
                        val posizione = LatLng(annuncio!!.latitudine, annuncio!!.longitudine)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(posizione, 15f)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState
                            ) {
                                Marker(
                                    state = MarkerState(position = posizione),
                                    title = annuncio!!.titolo
                                )
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
}