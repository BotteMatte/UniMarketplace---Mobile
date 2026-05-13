package com.example.unimarketplace.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unimarketplace.ui.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    userName: String
) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profilo Utente", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User Info Section
                item {
                    UserInfoSection(userName)
                }

                // Stats Cards Section
                item {
                    StatsOverviewSection(
                        totalAds = stats.totalAds,
                        soldAds = stats.soldAds,
                        earnings = stats.totalEarnings
                    )
                }

                // Pie Chart Section
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

                // Bar Chart Section
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
            }
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
            Text(
                text = "Studente Unibo",
                color = Color.Gray,
                fontSize = 16.sp
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
