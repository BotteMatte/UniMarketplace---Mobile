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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unimarketplace.ui.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    isLoginModeInitial: Boolean = true,
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    var isLoginMode by remember { mutableStateOf(isLoginModeInitial) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Stati per i campi
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Osserva i risultati dell'autenticazione
    LaunchedEffect(viewModel.authResult) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthViewModel.AuthResult.Success -> {
                    if (isLoginMode) {
                        onSuccess()
                    } else {
                        // Mostra il messaggio di registrazione e passa al login
                        scope.launch { snackbarHostState.showSnackbar(result.message) }
                        delay(500)
                        isLoginMode = true
                    }
                }
                is AuthViewModel.AuthResult.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC)) // Slate 50
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar: Torna al marketplace
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
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
                    fullName = fullName,
                    email = email,
                    password = password,
                    onFullNameChange = { fullName = it },
                    onEmailChange = { email = it },
                    onPasswordChange = { password = it },
                    onSwitchMode = { isLoginMode = !isLoginMode },
                    onAction = {
                        if (isLoginMode) {
                            viewModel.login(email, password)
                        } else {
                            viewModel.register(fullName, email, password)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AuthCard(
    isLoginMode: Boolean,
    fullName: String,
    email: String,
    password: String,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSwitchMode: () -> Unit,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .widthIn(max = 450.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLoginMode) "Accedi a UniboMarket" else "Registrati su UniboMarket",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isLoginMode)
                    "Benvenuto! Inserisci le tue credenziali per accedere"
                else
                    "Crea il tuo account per iniziare a comprare e vendere",
                fontSize = 16.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (!isLoginMode) {
                AuthTextField(
                    label = "Nome completo",
                    value = fullName,
                    onValueChange = onFullNameChange,
                    icon = Icons.Default.Person,
                    placeholder = ""
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            AuthTextField(
                label = "Email universitaria",
                value = email,
                onValueChange = onEmailChange,
                icon = Icons.Default.Email,
                placeholder = ""
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "Password",
                value = password,
                onValueChange = onPasswordChange,
                icon = Icons.Default.Lock,
                placeholder = "",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF020617))
            ) {
                Text(
                    text = if (isLoginMode) "Accedi" else "Registrati",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.clickable { onSwitchMode() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLoginMode) "Non hai un account? " else "Hai già un account? ",
                    fontSize = 16.sp,
                    color = Color(0xFF475569)
                )
                Text(
                    text = if (isLoginMode) "Registrati ora" else "Accedi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    placeholder: String,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 10.dp, start = 2.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF1F5F9),
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
