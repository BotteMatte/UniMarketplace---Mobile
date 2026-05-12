package com.example.unimarketplace.ui.marketplace

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.unimarketplace.domain.model.Categoria
import com.example.unimarketplace.domain.model.Condizioni
import com.example.unimarketplace.ui.marketplace.viewmodel.CreateAnnuncioViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnnuncioScreen(
    viewModel: CreateAnnuncioViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Stati per i campi
    var titolo by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }
    var prezzo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Categoria.ALTRO) }
    var condizioni by remember { mutableStateOf(Condizioni.USATO) }
    var immagini by remember { mutableStateOf<List<String>>(emptyList()) }

    // Dialog state
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Fotocamera
    val cameraUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri.value != null) {
            immagini = immagini + cameraUri.value.toString()
        }
    }

    // Galleria
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5 - immagini.size)
    ) { uris ->
        immagini = immagini + uris.map { it.toString() }
    }

    // Permessi
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Crea URI e lancia fotocamera
            val uri = createImageUri(context)
            cameraUri.value = uri
            cameraLauncher.launch(uri)
        }
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Lancia galleria
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    // Osserva risultati
    LaunchedEffect(viewModel.createResult) {
        viewModel.createResult.collect { result ->
            when (result) {
                is CreateAnnuncioViewModel.CreateResult.Success -> {
                    scope.launch { snackbarHostState.showSnackbar(result.message) }
                    onSuccess()
                }
                is CreateAnnuncioViewModel.CreateResult.Error -> {
                    scope.launch { snackbarHostState.showSnackbar(result.message) }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Crea Annuncio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Titolo
            OutlinedTextField(
                value = titolo,
                onValueChange = { titolo = it },
                label = { Text("Titolo") },
                modifier = Modifier.fillMaxWidth()
            )

            // Descrizione
            OutlinedTextField(
                value = descrizione,
                onValueChange = { descrizione = it },
                label = { Text("Descrizione") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Prezzo
            OutlinedTextField(
                value = prezzo,
                onValueChange = { prezzo = it },
                label = { Text("Prezzo (€)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Categoria
            var expandedCategoria by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = it }
            ) {
                OutlinedTextField(
                    value = categoria.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    Categoria.values().forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                categoria = cat
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Condizioni
            var expandedCondizioni by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCondizioni,
                onExpandedChange = { expandedCondizioni = it }
            ) {
                OutlinedTextField(
                    value = condizioni.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Condizioni") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCondizioni) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCondizioni,
                    onDismissRequest = { expandedCondizioni = false }
                ) {
                    Condizioni.values().forEach { cond ->
                        DropdownMenuItem(
                            text = { Text(cond.name.replace("_", " ")) },
                            onClick = {
                                condizioni = cond
                                expandedCondizioni = false
                            }
                        )
                    }
                }
            }

            // Immagini
            Text("Immagini (${immagini.size}/5)", fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Aggiungi immagine
                if (immagini.size < 5) {
                    Card(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                // Mostra dialog per scegliere fotocamera o galleria
                                showImageSourceDialog = true
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "Aggiungi immagine")
                        }
                    }
                }

                // Mostra immagini selezionate
                immagini.forEachIndexed { index, uri ->
                    Card(modifier = Modifier.size(80.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Immagine ${index + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Pulsante Crea
            Button(
                onClick = {
                    val prezzoDouble = prezzo.toDoubleOrNull()
                    if (prezzoDouble != null) {
                        viewModel.createAnnuncio(titolo, descrizione, prezzoDouble, categoria, condizioni, immagini)
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Prezzo non valido") }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crea Annuncio")
            }
        }
    }

    // Dialog per scegliere fonte immagine
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Scegli fonte") },
            text = {
                Column {
                    TextButton(onClick = {
                        // Fotocamera
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        showImageSourceDialog = false
                    }) {
                        Icon(Icons.Default.Camera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fotocamera")
                    }
                    TextButton(onClick = {
                        // Galleria
                        storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        showImageSourceDialog = false
                    }) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Galleria")
                    }
                }
            },
            confirmButton = {}
        )
    }
}

// Funzione per creare URI per fotocamera
fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "temp_image.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
