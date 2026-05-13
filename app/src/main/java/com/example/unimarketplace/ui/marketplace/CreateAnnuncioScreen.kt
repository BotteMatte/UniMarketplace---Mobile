package com.example.unimarketplace.ui.marketplace

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnnuncioScreen(
    viewModel: CreateAnnuncioViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val createResult by viewModel.createResult.collectAsState(initial = null)

    var titolo by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }
    var prezzo by remember { mutableStateOf("") }
    var categoriaSelezionata by remember { mutableStateOf(Categoria.ALTRO) }
    var condizioniSelezionate by remember { mutableStateOf(Condizioni.USATO) }
    var immaginiUri by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedCondizioni by remember { mutableStateOf(false) }

    // Alert di successo
    var showSuccessDialog by remember { mutableStateOf(false) }

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

    // Launcher per fotocamera - versione corretta con FileProvider
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                // L'URI è già quello permanente, lo aggiungiamo direttamente
                immaginiUri = immaginiUri + uri
            }
        }
    }

    // Gestiamo il risultato della creazione
    LaunchedEffect(createResult) {
        when (createResult) {
            is CreateAnnuncioViewModel.CreateResult.Success -> {
                showSuccessDialog = true
            }
            is CreateAnnuncioViewModel.CreateResult.Error -> {
                // errore già gestito
            }
            null -> {}
        }
    }

    // Dialog di successo
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
                }) {
                    Text("OK")
                }
            },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50)
                )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
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
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Rimuovi",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bottone Galleria
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galleria")
                }

                // Bottone Fotocamera
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crea Annuncio", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Crea URI permanente per la foto (usando FileProvider)
private fun createImageUri(context: Context): Uri {
    val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "UniMarket")
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, "IMG_${UUID.randomUUID()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

// Salva immagine dalla galleria in storage interno permanente
private fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "UniMarket")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "IMG_${UUID.randomUUID()}.jpg")

        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()

        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}