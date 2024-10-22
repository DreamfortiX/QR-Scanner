package com.example.qr_scanner

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class QRCodeGenerator {

    fun generateQRForInstagram(username: String): Bitmap? {
        val instagramUrl = "https://instagram.com/$username"
        return generateQRCode(instagramUrl)
    }

    fun generateQRForMobileNumber(phoneNumber: String): Bitmap? {
        val telUrl = "tel:$phoneNumber"
        return generateQRCode(telUrl)
    }

    fun generateQRForTwitter(username: String): Bitmap? {
        val twitterUrl = "https://twitter.com/$username"
        return generateQRCode(twitterUrl)
    }

    fun generateQRForEmail(email: String): Bitmap? {
        val mailtoUrl = "mailto:$email"
        return generateQRCode(mailtoUrl)
    }

    fun generateQRForWhatsApp(phoneNumber: String): Bitmap? {
        val whatsappUrl = "https://wa.me/$phoneNumber"
        return generateQRCode(whatsappUrl)
    }

    fun generateQRLocation(location: String): Bitmap? {
        return generateQRCode(location)
    }

    fun generateQRForCompany(
        companyName: String,
        industryType: String,
        phone: String,
        email: String,
        website: String,
        address: String,
        city: String,
        country: String
    ): Bitmap? {
        val companyData = """
        BEGIN:VCARD
        VERSION:3.0
        FN:$companyName
        ORG:$companyName
        TITLE:$industryType
        TEL:$phone
        EMAIL:$email
        URL:$website
        ADR:;;$address;$city;;;$country
        END:VCARD
    """.trimIndent()
        return generateQRCode(companyData)
    }

    fun generateQRForContact(
        firstName: String,
        lastName: String,
        company: String,
        jobTitle: String,
        phone: String,
        email: String,
        website: String,
        address: String,
        city: String,
        country: String
    ): Bitmap? {
        val contactData = """
        BEGIN:VCARD
        VERSION:3.0
        N:$lastName;$firstName;;;
        FN:$firstName $lastName
        ORG:$company
        TITLE:$jobTitle
        TEL:$phone
        EMAIL:$email
        URL:$website
        ADR:;;$address;$city;;;$country
        END:VCARD
    """.trimIndent()
        return generateQRCode(contactData)
    }

    fun generateQRForEvent(
        eventName: String,
        eventStartDate: String,
        eventEndDate: String,
        eventLocation: String,
        eventDescription: String
    ): Bitmap? {
        val eventData = """
        BEGIN:VEVENT
        SUMMARY:$eventName
        DTSTART:$eventStartDate
        DTEND:$eventEndDate
        LOCATION:$eventLocation
        DESCRIPTION:$eventDescription
        END:VEVENT
    """.trimIndent()
        return generateQRCode(eventData)
    }

    fun generateQRForWiFi(ssid: String, password: String, encryptionType: String = "WPA", hidden: Boolean = false): Bitmap? {
        val wifiConfig = "WIFI:S:$ssid;T:$encryptionType;P:$password;H:${if (hidden) "true" else "false"};"
        return generateQRCode(wifiConfig)
    }

    fun generateQRCode(text: String): Bitmap? {
        val qrCodeWriter = QRCodeWriter()
        return try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    fun generateWebsiteQRCode(websiteUrl: String): Bitmap? {
        return generateQRCode(websiteUrl)
    }
}
