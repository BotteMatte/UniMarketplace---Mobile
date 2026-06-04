# UniMarketplace - Mobile

UniMarketplace è una piattaforma mobile di compravendita dedicata agli studenti universitari. L'applicazione permette di pubblicare annunci per oggetti fisici (libri, strumenti di laboratorio) e prodotti digitali (PDF, dispense, esercizi), facilitando lo scambio di materiali didattici all'interno della comunità studentesca.

## Funzionalità Principali

- **Gestione Annunci**: Creazione, modifica ed eliminazione di annunci con supporto per immagini multiple.
- **Prodotti Digitali**: Supporto specifico per il caricamento di file PDF (per categorie come Dispense o Test). Dopo l'acquisto, l'utente può scaricare i file direttamente nella cartella Download del dispositivo.
- **Geolocalizzazione**: Integrazione con Google Maps per associare una posizione agli annunci fisici e facilitare il ritiro a mano.
- **Sistema di Gamification**: Guadagno di "Badge" (obiettivi) in base alle attività svolte (es. primo annuncio pubblicato, utilizzo della modalità scura, numero di vendite effettuate).
- **Carrello e Preferiti**: Gestione degli articoli di interesse e processo di checkout simulato.
- **Centro Notifiche**: Notifiche in-app per vendite effettuate, nuovi badge ottenuti e aggiornamenti sul carrello.
- **Tema Dinamico**: Supporto completo per Modalità Chiara e Scura, con adattamento automatico dell'interfaccia.

## 🛠️ Tecnologie Utilizzate

L'applicazione è sviluppata seguendo i moderni standard di sviluppo Android:

- **Linguaggio**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Architettura**: MVVM (Model-View-ViewModel) con Repository Pattern
- **Database Locale**: Room Persistence Library (con gestione di database separati per autenticazione e dominio)
- **Navigazione**: Navigation Compose
- **Programmazione Asincrona**: Kotlin Coroutines e Flow (StateFlow/SharedFlow)
- **Immagini**: Coil per il caricamento e caching delle immagini
- **Mappe**: Google Maps SDK for Android e Google Play Services Location
- **Permessi**: Accompanist Permissions per la gestione dinamica dei permessi (GPS, Camera)

## 📁 Struttura del Database

Il progetto utilizza un'architettura a doppio database per migliorare la sicurezza e la modularità:
1. **AppDatabase**: Gestisce le credenziali di accesso e i profili utente (`UserEntity`).
2. **UniMarketDatabase**: Gestisce il cuore del marketplace, inclusi Annunci, Carrello, Preferiti, Badge e Notifiche.

## 📦 Installazione e Requisiti

- Android Studio Koala o superiore
- SDK Android 24+ (Android 7.0)
- Chiave API di Google Maps (da inserire nel file `AndroidManifest.xml` per le funzionalità di geolocalizzazione)

---
*Sviluppato per il corso di programmazione di Sistemi mobile - Progetto Universitario di Botteghi Matteo e Crtistian Qorri.*
