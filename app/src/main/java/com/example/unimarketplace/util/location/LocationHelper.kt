package com.example.unimarketplace.util.location

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class Posizione(
    val latitudine: Double,
    val longitudine: Double,
    val indirizzo: String = "",
    val citta: String = "",
    val cap: String = "",
    val provincia: String = ""
)

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentPosition(): Posizione {
        return suspendCancellableCoroutine { continuation ->
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val indirizzoInfo = reverseGeocode(location)
                            continuation.resume(
                                Posizione(
                                    latitudine = location.latitude,
                                    longitudine = location.longitude,
                                    indirizzo = indirizzoInfo.first,
                                    citta = indirizzoInfo.second,
                                    cap = indirizzoInfo.third,
                                    provincia = indirizzoInfo.fourth
                                )
                            )
                        } else {
                            continuation.resumeWithException(
                                Exception("Impossibile ottenere la posizione")
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
            } catch (e: SecurityException) {
                continuation.resumeWithException(e)
            }
        }
    }

    private fun reverseGeocode(location: Location): Quadruple<String, String, String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val indirizzo = address.thoroughfare ?: address.featureName ?: ""
                val numeroCivico = address.subThoroughfare ?: ""
                val indirizzoCompleto = if (numeroCivico.isNotEmpty()) "$indirizzo $numeroCivico" else indirizzo
                val citta = address.locality ?: ""
                val cap = address.postalCode ?: ""
                val provincia = address.adminArea ?: ""

                Quadruple(indirizzoCompleto, citta, cap, provincia)
            } else {
                Quadruple("", "", "", "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Quadruple("", "", "", "")
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)