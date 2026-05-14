package com.example.unimarketplace.domain.model

enum class BadgeType(
    val id: String,
    val titolo: String,
    val descrizione: String,
    val iconName: String
) {
    NOVIZIO("novizio", "Novizio", "Hai pubblicato il tuo primo annuncio!", "Stars"),
    FOTOGRAFO("fotografo", "Fotografo", "Hai creato un annuncio con un'immagine!", "PhotoCamera"),
    ANIMALE_NOTTURNO("animale_notturno", "Animale Notturno", "Hai usato l'app in modalità scura!", "DarkMode"),
    MERCANTE("mercante", "Mercante", "Hai venduto il tuo primo articolo!", "Store"),
    RABAZZIERE("rabazziere", "Rabazziere", "Hai effettuato 2 vendite e 2 acquisti!", "Handshake"),
    ROMPIGHIACCIO("rompighiaccio", "Rompighiaccio", "Hai effettuato il tuo primo acquisto!", "IceSkating"),
    DILETTANTE("dilettante", "Dilettante", "Hai pubblicato due annunci!", "Work"),
    AFFARISTA("affarista", "Affarista", "Hai venduto 2 articoli!", "TrendingUp")
}

data class Badge(
    val type: BadgeType,
    val earnedTimestamp: Long
)
