package com.example.unimarketplace.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(modifier: Modifier) {
    // Stato per gestire il tab attivo: 0 = Login, 1 = Registrazione
    var selectedTab by remember { mutableStateOf(0) }

    // Contenitore principale centrato
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 400.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Componente TABS (Ispirato a tabs.tsx)
            ShadcnTabs(
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Componente CARD (Ispirato a card.tsx)
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (selectedTab == 0) {
                        LoginContent()
                    } else {
                        RegisterContent()
                    }
                }
            }
        }
    }
}

@Composable
fun LoginContent() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Bentornato",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Inserisci le tue credenziali per accedere al marketplace.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        ShadcnTextField(
            label = "Email universitaria",
            value = email,
            onValueChange = { email = it },
            placeholder = "mario.rossi@studio.unibo.it"
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShadcnTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "••••••••",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Componente BUTTON (Ispirato a button.tsx)
        Button(
            onClick = { /* TODO: Logica di Login */ },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Accedi")
        }
    }
}

@Composable
fun RegisterContent() {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Crea un account",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Unisciti agli altri studenti per comprare e vendere appunti e libri.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        ShadcnTextField(
            label = "Nome completo",
            value = nome,
            onValueChange = { nome = it },
            placeholder = "Mario Rossi"
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShadcnTextField(
            label = "Email universitaria",
            value = email,
            onValueChange = { email = it },
            placeholder = "mario.rossi@studio.unibo.it"
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShadcnTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "••••••••",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO: Logica di Registrazione */ },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Registrati")
        }
    }
}

// --- COMPONENTI UI RIUTILIZZABILI ---

@Composable
fun ShadcnTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    // Simula lo stile del componente TabsList di shadcn
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
    ) {
        val tabs = listOf("Accedi", "Registrati")
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ShadcnTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    // Simula l'unione di Label.tsx e Input.tsx
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}