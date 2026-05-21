package com.example.unimarketplace.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.unimarketplace.domain.model.Annuncio
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ReceiptGenerator(private val context: Context) {

    data class ReceiptItem(
        val annuncio: Annuncio,
        val venditoreNome: String
    )

    data class ReceiptData(
        val acquirenteNome: String,
        val acquirenteEmail: String,
        val items: List<ReceiptItem>,
        val totale: Double,
        val dataAcquisto: Date = Date()
    )

    /**
     * Genera il PDF e restituisce l'URI pubblico (Download) su Android 10+,
     * altrimenti un File privato.
     */
    fun generateReceipt(data: ReceiptData): Uri? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = android.graphics.Color.parseColor("#1E293B")
        }

        val subtitlePaint = Paint().apply {
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = android.graphics.Color.parseColor("#334155")
        }

        val normalPaint = Paint().apply {
            textSize = 14f
            color = android.graphics.Color.parseColor("#475569")
        }

        val smallPaint = Paint().apply {
            textSize = 12f
            color = android.graphics.Color.parseColor("#64748B")
        }

        val linePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#E2E8F0")
            strokeWidth = 2f
        }

        val totalPaint = Paint().apply {
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = android.graphics.Color.parseColor("#2563EB")
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        var y = 60f

        // Titolo
        canvas.drawText("UniMarketplace - Ricevuta d'Acquisto", 40f, y, titlePaint)
        y += 40f

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 30f

        canvas.drawText("Data: ${dateFormat.format(data.dataAcquisto)}", 40f, y, normalPaint)
        y += 25f

        // Acquirente
        canvas.drawText("Acquirente:", 40f, y, subtitlePaint)
        y += 20f
        canvas.drawText("  Nome: ${data.acquirenteNome}", 40f, y, normalPaint)
        y += 18f
        canvas.drawText("  Email: ${data.acquirenteEmail}", 40f, y, smallPaint)
        y += 30f

        // Elenco articoli
        canvas.drawText("Articoli acquistati:", 40f, y, subtitlePaint)
        y += 22f

        data.items.forEach { item ->
            canvas.drawText("  - ${item.annuncio.titolo} (€${String.format("%.2f", item.annuncio.prezzo)})", 40f, y, normalPaint)
            y += 18f
            canvas.drawText("    Venditore: ${item.venditoreNome}", 40f, y, smallPaint)
            y += 18f
        }

        y += 10f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 30f

        // Riepilogo
        canvas.drawText("Riepilogo Pagamento:", 40f, y, subtitlePaint)
        y += 22f
        canvas.drawText("  Totale articoli: ${data.items.size}", 40f, y, normalPaint)
        y += 18f
        canvas.drawText("  Spedizione: Gratuita", 40f, y, normalPaint)
        y += 18f
        canvas.drawText("  Metodo di pagamento: Contrassegno", 40f, y, smallPaint)
        y += 25f

        canvas.drawText("  TOTALE: €${String.format("%.2f", data.totale)}", 40f, y, totalPaint)
        y += 50f

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 25f

        canvas.drawText("Grazie per aver usato UniMarketplace!", 40f, y, smallPaint)
        y += 15f
        canvas.drawText("Ricevuta generata automaticamente - ${dateFormat.format(Date())}", 40f, y, smallPaint)

        pdfDocument.finishPage(page)

        // Salvataggio
        val fileName = "Ricevuta_${System.currentTimeMillis()}.pdf"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // MediaStore per Download pubblico
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            }
            pdfDocument.close()
            uri
        } else {
            // Per Android 9 e inferiori, cartella Download pubblica
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            android.net.Uri.fromFile(file)
        }
    }
}