package com.example.unimarketplace.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.ui.cart.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onContinueShopping: () -> Unit,
    isDarkTheme: Boolean
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkoutSuccess.collect { success ->
            if (success) {
                showSuccessDialog = true
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onContinueShopping() // Torna al marketplace dopo l'acquisto
            },
            title = { Text("Acquisto effettuato") },
            text = { Text("Il tuo ordine è stato elaborato con successo. Grazie per il tuo acquisto!") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onContinueShopping()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    val subtotal = cartItems.sumOf { it.prezzo }
    val shipping = 0.0
    val total = subtotal + shipping

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE0E7FF), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Il tuo carrello",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${cartItems.size} articolo${if (cartItems.size != 1) "li" else ""}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (cartItems.isEmpty()) {
                EmptyCartView(onContinueShopping)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(cartItems) { item ->
                            CartItemCard(
                                item = item,
                                isDarkTheme = isDarkTheme,
                                onRemove = { viewModel.rimuoviDalCarrello(item.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OrderSummaryCard(
                        itemCount = cartItems.size,
                        subtotal = subtotal,
                        shipping = shipping,
                        total = total,
                        isDarkTheme = isDarkTheme,
                        onCheckout = { viewModel.procediAlPagamento() },
                        onContinueShopping = onContinueShopping
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCartView(onContinueShopping: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Il tuo carrello è vuoto", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Inizia a esplorare il marketplace!", color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onContinueShopping) {
            Text("Continua lo shopping")
        }
    }
}

@Composable
fun CartItemCard(item: Annuncio, isDarkTheme: Boolean, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)) else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Immagine dell'articolo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                if (item.immagini.isNotEmpty()) {
                    AsyncImage(
                        model = item.immagini.first(),
                        contentDescription = item.titolo,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.titolo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(text = item.descrizione, fontSize = 14.sp, color = Color.Gray, maxLines = 1)
                Text(text = item.venditoreNome, fontSize = 14.sp, color = Color.Gray)
                if (item.isVenduto) {
                    Text(text = "NON PIÙ DISPONIBILE", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "€${item.prezzo.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB)
                )
            }
        }
    }
}

@Composable
fun OrderSummaryCard(
    itemCount: Int,
    subtotal: Double,
    shipping: Double,
    total: Double,
    isDarkTheme: Boolean,
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)) else null
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Riepilogo ordine",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))

            SummaryRow(label = "Subtotale ($itemCount articolo${if (itemCount != 1) "li" else ""})", value = "€${"%.2f".format(subtotal)}", isDarkTheme = isDarkTheme)
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow(label = "Spedizione", value = if (shipping == 0.0) "Gratuita" else "€${"%.2f".format(shipping)}", valueColor = Color(0xFF10B981), isDarkTheme = isDarkTheme)
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(16.dp))

            SummaryRow(label = "Totale", value = "€${"%.2f".format(total)}", isTotal = true, isDarkTheme = isDarkTheme)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    contentColor = if (isDarkTheme) Color.Black else Color.White
                )
            ) {
                Text("Procedi al pagamento", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onContinueShopping,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
            ) {
                Text("Continua lo shopping", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color = Color.Unspecified, isTotal: Boolean = false, isDarkTheme: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.onSurface else Color.Gray
        )
        Text(
            text = value,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold,
            color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor
        )
    }
}
