package com.example.unimarketplace.ui.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unimarketplace.R
import com.example.unimarketplace.UniMarketApp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnuncioDetailScreen(
    annuncioId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as UniMarketApp
    val getAnnuncioByIdUseCase = app.getAnnuncioByIdUseCase

    val viewModel: AnnuncioDetailViewModel = viewModel(
        factory = AnnuncioDetailViewModelFactory(getAnnuncioByIdUseCase)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(annuncioId) {
        viewModel.loadAnnuncio(annuncioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dettaglio Annuncio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Errore sconosciuto",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAnnuncio(annuncioId) }) {
                            Text("Riprova")
                        }
                    }
                }
                uiState.annuncio != null -> {
                    val annuncio = uiState.annuncio!!
                    val scrollState = rememberScrollState()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Immagine segnaposto
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Usa un'icona o immagine segnaposto
                            contentDescription = "Immagine annuncio",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        // Titolo
                        Text(
                            text = annuncio.titolo,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Prezzo
                        Text(
                            text = "€${String.format("%.2f", annuncio.prezzo)}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Chip categoria e condizioni
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuggestionChip(
                                onClick = { },
                                label = { Text(annuncio.categoria.name) }
                            )
                            SuggestionChip(
                                onClick = { },
                                label = { Text(annuncio.condizioni.name) }
                            )
                        }

                        // Descrizione
                        Text(
                            text = annuncio.descrizione,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Venditore
                        Text(
                            text = "Venditore: ${annuncio.venditoreNome}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Data pubblicazione
                        Text(
                            text = "Pubblicato: ${dateFormat.format(Date(annuncio.dataPubblicazione))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Pulsanti
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { /* Toggle preferiti */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (uiState.isPreferito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Preferiti"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Salva")
                            }

                            Button(
                                onClick = { /* Contatta venditore */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Contatta"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Contatta")
                            }
                        }
                    }
                }
            }
        }
    }
}
