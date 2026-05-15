package com.example.unimarketplace.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.model.Badge
import com.example.unimarketplace.ui.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    userName: String
) {
    val stats by viewModel.stats.collectAsState()
    val userAnnunci by viewModel.userAnnunci.collectAsState()
    val userBadges by viewModel.userBadges.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    var showNotifications by remember { mutableStateOf(false) }

    // Dialog notifiche
    if (showNotifications) {
        AlertDialog(
            onDismissRequest = { showNotifications = false },
            title = { Text("Notifiche") },
            text = {
                if (notifications.isEmpty()) {
                    Text("Nessuna notifica")
                } else {
                    LazyColumn {
                        items(notifications.size) { index ->
                            val n = notifications[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = when (n.type) {
                                        "sale" -> Icons.Default.ShoppingCart
                                        "badge" -> Icons.Default.Star
                                        else -> Icons.Default.Notifications
                                    },
                                    contentDescription = null,
                                    tint = if (n.isRead) Color.Gray else Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(n.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(n.message, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        viewModel.markAllNotificationsAsRead()
                        showNotifications = false
                    }) { Text("Segna tutte lette") }
                    TextButton(onClick = { showNotifications = false }) { Text("Chiudi") }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Il Mio Profilo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showNotifications = true }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifiche")
                        }
                        if (unreadCount > 0) {
                            Badge(
                                modifier = Modifier.align(Alignment.TopEnd),
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text(
                                    if (unreadCount > 99) "99+" else unreadCount.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // User Info Section
                item {
                    UserInfoSection(userName)
                }

                if (userBadges.isNotEmpty()) {
                    item {
                        BadgesSection(userBadges)
                    }
                }

                item {
                    Text(
                        text = "Le Tue Statistiche",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Stats Cards Section
                item {
                    StatsOverviewSection(
                        totalAds = stats.totalAds,
                        soldAds = stats.soldAds,
                        earnings = stats.totalEarnings
                    )
                }

                // Charts Section
                if (stats.totalAds > 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Stato Annunci",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                AdsStatusPieChart(
                                    soldCount = stats.soldAds,
                                    activeCount = stats.activeAds
                                )
                            }
                        }
                    }
                }

                if (stats.adsByCategory.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Annunci per Categoria",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                CategoryBarChart(
                                    data = stats.adsByCategory
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "I Miei Annunci",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                if (userAnnunci.isEmpty()) {
                    item {
                        EmptyAdsPlaceholder()
                    }
                } else {
                    items(userAnnunci) { annuncio ->
                        MyAnnuncioCard(
                            annuncio = annuncio,
                            onEdit = { onNavigateToEdit(annuncio.id) },
                            onDelete = { viewModel.eliminaAnnuncio(annuncio) },
                            onMarkAsSold = { viewModel.segnaComeVenduto(annuncio) },
                            onClick = { onNavigateToDetail(annuncio.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BadgesSection(badges: List<Badge>) {
    Column {
        Text(
            text = "I Tuoi Badge",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            badges.forEach { badge ->
                BadgeItem(badge, modifier = Modifier.width(105.dp))
            }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFB8860B),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = badge.type.titolo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB8860B),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MyAnnuncioCard(
    annuncio: Annuncio,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkAsSold: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = annuncio.titolo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "€${annuncio.prezzo.toInt()}",
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                if (annuncio.isVenduto) {
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "VENDUTO",
                            color = Color(0xFF10B981),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!annuncio.isVenduto) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Modifica", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onMarkAsSold,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Venduto", fontSize = 12.sp)
                    }
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = if (annuncio.isVenduto) Modifier.fillMaxWidth() else Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Elimina", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyAdsPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Non hai ancora pubblicato nessun annuncio.", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun UserInfoSection(userName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF2563EB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (userName.isNotEmpty()) userName.take(1).uppercase() else "?",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun StatsOverviewSection(totalAds: Int, soldAds: Int, earnings: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(modifier = Modifier.weight(1f), label = "Totale Annunci", value = totalAds.toString(), color = Color(0xFF3B82F6))
        StatCard(modifier = Modifier.weight(1f), label = "Venduti", value = soldAds.toString(), color = Color(0xFF10B981))
        StatCard(modifier = Modifier.weight(1f), label = "Guadagno", value = "€${earnings.toInt()}", color = Color(0xFFF59E0B))
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, label: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 20.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdsStatusPieChart(soldCount: Int, activeCount: Int) {
    val total = soldCount + activeCount
    if (total == 0) return
    val soldAngle = (soldCount.toFloat() / total) * 360f
    val activeAngle = (activeCount.toFloat() / total) * 360f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(150.dp)) {
            drawArc(
                color = Color(0xFF10B981),
                startAngle = -90f,
                sweepAngle = soldAngle,
                useCenter = true,
                size = Size(size.width, size.height)
            )
            drawArc(
                color = Color(0xFF3B82F6),
                startAngle = -90f + soldAngle,
                sweepAngle = activeAngle,
                useCenter = true,
                size = Size(size.width, size.height)
            )
        }
        Spacer(modifier = Modifier.width(32.dp))
        Column {
            LegendItem(color = Color(0xFF10B981), label = "Venduti ($soldCount)")
            Spacer(modifier = Modifier.height(8.dp))
            LegendItem(color = Color(0xFF3B82F6), label = "Attivi ($activeCount)")
        }
    }
}

@Composable
fun CategoryBarChart(data: Map<String, Int>) {
    val maxValue = data.values.maxOrNull() ?: 1
    val barColor = Color(0xFF6366F1)

    Column(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (category, count) ->
                val barHeightFraction = count.toFloat() / maxValue
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(text = count.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .fillMaxHeight(barHeightFraction * 0.8f)
                            .background(barColor, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = category, fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp)
    }
}