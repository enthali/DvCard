package de.drachenfels.dvcard.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.util.logger.Log
import de.drachenfels.dvcard.util.logger.LogConfig
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

/**
 * Generiert einen vCard-String im Format 3.0 aus einer BusinessCard
 */
fun generateVCardString(card: BusinessCard): String {
    val vCardBuilder = StringBuilder()
    
    // vCard Header
    vCardBuilder.append("BEGIN:VCARD\n")
    vCardBuilder.append("VERSION:3.0\n")
    
    // Name im Format: Nachname;Vorname;Weitere Namen;Titel;Suffix
    vCardBuilder.append("N:${card.familyName};${card.givenName};;;\n")
    vCardBuilder.append("FN:${card.getFullName()}\n")
    
    // Beruf und Firma
    if (card.position.isNotEmpty() || card.company.isNotEmpty()) {
        vCardBuilder.append("ORG:${card.company}\n")
        if (card.position.isNotEmpty()) {
            vCardBuilder.append("TITLE:${card.position}\n")
        }
    }
    
    // Adresse
    if (card.street.isNotEmpty() || card.city.isNotEmpty() || card.postalCode.isNotEmpty() || card.country.isNotEmpty()) {
        val type = if (card.isPrivate) "HOME" else "WORK"
        vCardBuilder.append("ADR;TYPE=$type:;;${card.street};${card.city};;${card.postalCode};${card.country}\n")
    }
    
    // Kontaktdaten
    if (card.phone.isNotEmpty()) {
        val type = if (card.isPrivate) "HOME" else "WORK"
        vCardBuilder.append("TEL;TYPE=$type:${card.phone}\n")
    }
    
    if (card.email.isNotEmpty()) {
        val type = if (card.isPrivate) "HOME" else "WORK"
        vCardBuilder.append("EMAIL;TYPE=$type:${card.email}\n")
    }
    
    if (card.website.isNotEmpty()) {
        vCardBuilder.append("URL:${card.website}\n")
    }
    
    // vCard Footer
    vCardBuilder.append("END:VCARD")
    
    return vCardBuilder.toString()
}

/**
 * Generiert einen QR-Code aus einer BusinessCard im vCard-Format
 */
fun generateVCardQrCode(card: BusinessCard, size: Int = 512): Bitmap {
    val vCardContent = generateVCardString(card)
    Log.d(LogConfig.TAG_UTILS, "Generated vCard content: $vCardContent")
    
    val hints = hashMapOf<EncodeHintType, Any>().apply {
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
        put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
        put(EncodeHintType.MARGIN, 2)
    }
    
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(vCardContent, BarcodeFormat.QR_CODE, size, size, hints)
    
    val bitmap = createBitmap(size, size)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
        }
    }
    
    return bitmap
}

/**
 * Generiert einen QR-Code für eine URL
 */
fun generateUrlQrCode(url: String, size: Int = 512): Bitmap {
    Log.d(LogConfig.TAG_UTILS, "Generating URL QR code for: $url")
    
    val hints = hashMapOf<EncodeHintType, Any>().apply {
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
        put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
        put(EncodeHintType.MARGIN, 2)
    }
    
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, size, size, hints)
    
    val bitmap = createBitmap(size, size)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
        }
    }
    
    return bitmap
}