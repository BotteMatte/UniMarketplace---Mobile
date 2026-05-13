package com.example.unimarketplace.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.unimarketplace.domain.model.Categoria
import com.example.unimarketplace.domain.model.Condizioni
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

    // TODO: Caricare l'annuncio dal database usando l'annuncioId
    // Per ora mostriamo un esempio

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
                // Immagine di copertina
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (annuncio!!.immagini.isNotEmpty()) {
                        AsyncImage(
                            model = annuncio!!.immagini.first(),
                            contentDescription = "Immagine annuncio",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
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
                            modifier = Modifier.fillMaxWidth().height(250.dp),
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