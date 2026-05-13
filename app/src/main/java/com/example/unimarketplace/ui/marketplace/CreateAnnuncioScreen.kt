package com.example.unimarketplace.ui.marketplace

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.unimarketplace.domain.model.Categoria
import com.example.unimarketplace.domain.model.Condizioni
import com.example.unimarketplace.ui.marketplace.viewmodel.CreateAnnuncioViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateAnnuncioScreen(
    viewModel: CreateAnnuncioViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val createResult by viewModel.createResult.collectAsState(initial = null)
    val posizione by viewModel.posizione.collectAsState()
    val isLoadingLocation by viewModel.isLoadingLocation.collectAsState()

    // Richiesta permesso di localizzazione
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    var titolo by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }
    var prezzo by remember { mutableStateOf("") }
    var categoriaSelezionata by remember { mutableStateOf(Categoria.ALTRO) }
    var condizioniSelezionate by remember { mutableStateOf(Condizioni.USATO) }
    var immaginiUri by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedCondizioni by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }

    // Quando il permesso viene concesso, rileva la posizione
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            viewModel.getCurrentLocation()
        }
    }

    // Launcher per galleria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val savedUri = saveImageToInternalStorage(context, it)
            if (savedUri != null) {
                immaginiUri = immaginiUri + savedUri
            }
        }
    }

    // Launcher per fotocamera
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                immaginiUri = immaginiUri + uri
            }
        }
    }

    LaunchedEffect(createResult) {
        when (createResult) {
            is CreateAnnuncioViewModel.CreateResult.Success -> {
                showSuccessDialog = true
            }
            is CreateAnnuncioViewModel.CreateResult.Error -> {}
            null -> {}
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onSuccess()
            },
            title = { Text("Successo!") },
            text = { Text("Annuncio creato con successo!") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onSuccess()
                }) { Text("OK") }
            },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crea Annuncio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Titolo
            OutlinedTextField(
                value = titolo,
                onValueChange = { titolo = it },
                label = { Text("Titolo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Descrizione
            OutlinedTextField(
                value = descrizione,
                onValueChange = { descrizione = it },
                label = { Text("Descrizione") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            // Prezzo
            OutlinedTextField(
                value = prezzo,
                onValueChange = { prezzo = it },
                label = { Text("Prezzo (€)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Text("€", fontWeight = FontWeight.Bold) }
            )

            // Categoria
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoriaSelezionata.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    Categoria.entries.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.name) },
                            onClick = {
                                categoriaSelezionata = categoria
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Condizioni
            ExposedDropdownMenuBox(
                expanded = expandedCondizioni,
                onExpandedChange = { expandedCondizioni = !expandedCondizioni }
            ) {
                OutlinedTextField(
                    value = condizioniSelezionate.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Condizioni") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCondizioni) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedCondizioni,
                    onDismissRequest = { expandedCondizioni = false }
                ) {
                    Condizioni.entries.forEach { condizioni ->
                        DropdownMenuItem(
                            text = { Text(condizioni.name) },
                            onClick = {
                                condizioniSelezionate = condizioni
                                expandedCondizioni = false
                            }
                        )
                    }
                }
            }

            // Immagini
            Text("Immagini", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            if (immaginiUri.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(immaginiUri.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Gray.copy(alpha = 0.2f))
                        ) {
                            AsyncImage(
                                model = immaginiUri[index],
                                contentDescription = "Immagine $index",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = {
                                    immaginiUri = immaginiUri.toMutableList().also { it.removeAt(index) }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Rimuovi", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galleria")
                }
                Button(
                    onClick = {
                        val photoUri = createImageUri(context)
                        currentPhotoUri = photoUri
                        cameraLauncher.launch(photoUri)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Fotocamera")
                }
            }

            // =============================================
            // SEZIONE POSIZIONE CON RICHIESTA PERMESSI
            // =============================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2563EB))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Posizione rilevata", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        if (isLoadingLocation) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else if (locationPermissionState.status.isGranted) {
                            IconButton(
                                onClick = { viewModel.getCurrentLocation() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Aggiorna", modifier = Modifier.size(18.dp), tint = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Se il permesso non è concesso, mostra il pulsante per richiederlo
                    if (!locationPermissionState.status.isGranted) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LocationOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Permesso di localizzazione richiesto", color = Color.Gray, fontSize = 14.sp)
                            Text("Serve per associare una posizione al tuo annuncio", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { locationPermissionState.launchPermissionRequest() },
                                modifier = Modifier.height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("Consenti accesso alla posizione", fontSize = 14.sp)
                            }
                        }
                    }
                    // Se il permesso è concesso e la posizione è disponibile
                    else if (posizione != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Latitudine", fontSize = 11.sp, color = Color.Gray)
                                Text(String.format("%.6f", posizione!!.latitudine), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Longitudine", fontSize = 11.sp, color = Color.Gray)
                                Text(String.format("%.6f", posizione!!.longitudine), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        if (posizione!!.indirizzo.isNotEmpty() || posizione!!.citta.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(12.dp))

                            if (posizione!!.indirizzo.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(posizione!!.indirizzo, fontSize = 14.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            if (posizione!!.citta.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        posizione!!.citta + (if (posizione!!.cap.isNotEmpty()) " - ${posizione!!.cap}" else ""),
                                        fontSize = 14.sp, color = Color.Gray
                                    )
                                }
                            }

                            if (posizione!!.provincia.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Provincia: ${posizione!!.provincia}", fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    }
                    // Permesso concesso ma posizione non ancora rilevata
                    else if (!isLoadingLocation) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LocationOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Posizione non disponibile", color = Color.Gray, fontSize = 14.sp)
                            Text("Assicurati che il GPS sia attivo", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { viewModel.getCurrentLocation() },
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Riprova", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottone Crea
            Button(
                onClick = {
                    val prezzoDouble = prezzo.toDoubleOrNull() ?: 0.0
                    viewModel.createAnnuncio(
                        titolo = titolo,
                        descrizione = descrizione,
                        prezzo = prezzoDouble,
                        categoria = categoriaSelezionata,
                        condizioni = condizioniSelezionate,
                        immagini = immaginiUri.map { it.toString() }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crea Annuncio", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Funzioni helper
private fun createImageUri(context: Context): Uri {
    val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "UniMarket")
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, "IMG_${UUID.randomUUID()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

private fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "UniMarket")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "IMG_${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { outputStream -> inputStream.copyTo(outputStream) }
        inputStream.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}