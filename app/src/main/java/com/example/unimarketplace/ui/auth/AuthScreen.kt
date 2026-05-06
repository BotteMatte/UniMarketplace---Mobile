package com.example.unimarketplace.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(modifier: Modifier = Modifier) {
    var isLoginMode by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // Slate 50
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar: Torna al marketplace
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Torna al marketplace",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )
        }

        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AuthCard(
                isLoginMode = isLoginMode,
                onSwitchMode = { isLoginMode = !isLoginMode }
            )
        }
    }
}

@Composable
fun AuthCard(
    isLoginMode: Boolean,
    onSwitchMode: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .widthIn(max = 450.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(32.dp), // Angoli molto ampi come in foto
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Ombra gestita da border o molto leggera
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titolo
            Text(
                text = if (isLoginMode) "Accedi a UniboMarket" else "Registrati su UniboMarket",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF0F172A), // Slate 900
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sottotitolo
            Text(
                text = if (isLoginMode)
                    "Benvenuto! Inserisci le tue credenziali per accedere"
                else
                    "Crea il tuo account per iniziare a comprare e vendere",
                fontSize = 16.sp,
                color = Color(0xFF64748B), // Slate 500
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Form
            if (!isLoginMode) {
                AuthTextField(
                    label = "Nome completo",
                    icon = Icons.Default.Person,
                    placeholder = ""
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            AuthTextField(
                label = "Email universitaria",
                icon = Icons.Default.Email,
                placeholder = ""
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "Password",
                icon = Icons.Default.Lock,
                placeholder = "",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pulsante principale
            Button(
                onClick = { /* Azione */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF020617)) // Slate 950
            ) {
                Text(
                    text = if (isLoginMode) "Accedi" else "Registrati",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Footer
            Row(
                modifier = Modifier.clickable { onSwitchMode() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLoginMode) "Non hai un account? " else "Hai già un account? ",
                    fontSize = 16.sp,
                    color = Color(0xFF475569) // Slate 600
                )
                Text(
                    text = if (isLoginMode) "Registrati ora" else "Accedi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB) // Blue 600
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    label: String,
    icon: ImageVector,
    placeholder: String,
    isPassword: Boolean = false
) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 10.dp, start = 2.dp)
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8), // Slate 400
                    modifier = Modifier.size(24.dp)
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF1F5F9), // Slate 100
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFFCBD5E1),
                disabledBorderColor = Color.Transparent
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen()
}
