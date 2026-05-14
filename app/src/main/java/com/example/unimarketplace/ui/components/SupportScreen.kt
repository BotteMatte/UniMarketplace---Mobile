package com.example.unimarketplace.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBack: () -> Unit,
    isDarkTheme: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aiuto e Supporto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
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
            // Intestazione
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Come possiamo aiutarti?",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Trova le risposte alle domande più frequenti o contattaci",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Sezione FAQ
            Text(
                "📋 Domande Frequenti",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            FaqItem(
                question = "Come pubblico un annuncio?",
                answer = "Vai alla home page e clicca sul pulsante '+' in alto a destra. Compila tutti i campi: titolo, descrizione, prezzo, categoria e condizioni. Puoi anche aggiungere foto e la tua posizione verrà rilevata automaticamente."
            )

            FaqItem(
                question = "Come acquisto un articolo?",
                answer = "Sfoglia gli annunci nella home page. Quando trovi qualcosa che ti interessa, clicca sull'annuncio per vedere i dettagli. Puoi aggiungerlo al carrello o contattare il venditore."
            )

            FaqItem(
                question = "Come funziona la geolocalizzazione?",
                answer = "Quando crei un annuncio, la tua posizione viene rilevata automaticamente tramite GPS. Questo serve per far sapere agli acquirenti dove si trova l'articolo. Puoi aggiornare la posizione cliccando sull'icona di refresh."
            )

            FaqItem(
                question = "I prezzi sono trattabili?",
                answer = "Sì! I prezzi sono stabiliti dal venditore ma puoi sempre contattarlo per proporre un'offerta diversa. Il prezzo finale è concordato tra acquirente e venditore."
            )

            FaqItem(
                question = "Come funzionano i badge?",
                answer = "I badge sono riconoscimenti che sblocchi utilizzando l'app. Ad esempio: pubblicando annunci, vendendo articoli, aggiungendo foto. Vai al tuo profilo per vedere quali badge hai sbloccato!"
            )

            FaqItem(
                question = "Posso modificare o eliminare un annuncio?",
                answer = "Sì, se sei il proprietario dell'annuncio. Vai sul tuo annuncio dalla home page, e in fondo troverai i pulsanti Modifica ed Elimina."
            )

            FaqItem(
                question = "Come funzionano i preferiti?",
                answer = "Puoi salvare gli annunci che ti interessano cliccando sull'icona a forma di cuore. Li ritroverai nella sezione Preferiti del menu laterale."
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sezione Contatti
            Text(
                "📞 Contattaci",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            ContactCard(
                icon = Icons.Default.Email,
                title = "Email",
                subtitle = "Scrivici per qualsiasi domanda",
                detail = "supporto@unimarketplace.it"
            )

            ContactCard(
                icon = Icons.Default.Info,
                title = "Segnala un problema",
                subtitle = "Hai trovato un bug o un comportamento strano?",
                detail = "Fai uno screenshot e inviacelo via email"
            )

            ContactCard(
                icon = Icons.Default.Schedule,
                title = "Tempi di risposta",
                subtitle = "Cerchiamo di rispondere a tutte le richieste",
                detail = "Entro 24-48 ore lavorative"
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FaqItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    question,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Chiudi" else "Apri",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        answer,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    detail: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF2563EB).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(2.dp))
                Text(detail, fontSize = 13.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Medium)
            }
        }
    }
}