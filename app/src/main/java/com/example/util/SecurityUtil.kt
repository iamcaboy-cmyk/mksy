package com.example.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SecurityUtil {
    private const val SALT = "JanSahayataSecureSalt2026#"

    fun hashPassword(password: String): String {
        val input = password + SALT
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        return hashPassword(password) == storedHash
    }

    fun formatMaskedAadhaar(last4: String): String {
        val clean = last4.filter { it.isDigit() }
        val digits = if (clean.length > 4) clean.takeLast(4) else clean
        return if (digits.length == 4) "XXXX-XXXX-$digits" else "XXXX-XXXX-${digits.padEnd(4, 'X')}"
    }

    fun isValidPhone(phone: String): Boolean {
        val clean = phone.trim().filter { it.isDigit() }
        return clean.length == 10 && (clean.startsWith("6") || clean.startsWith("7") || clean.startsWith("8") || clean.startsWith("9"))
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    fun isValidPinCode(pin: String): Boolean {
        val clean = pin.trim().filter { it.isDigit() }
        return clean.length == 6
    }

    fun isValidIfsc(ifsc: String): Boolean {
        val clean = ifsc.trim().uppercase()
        // Standard IFSC: 4 letters, 0, 6 letters/digits
        val regex = Regex("^[A-Z]{4}0[A-Z0-9]{6}$")
        return clean.matches(regex)
    }

    fun isValidAadhaarLast4(aadhaar: String): Boolean {
        val clean = aadhaar.trim().filter { it.isDigit() }
        return clean.length == 4
    }

    fun generateApplicationNumber(): String {
        val randomNum = (100000..999999).random()
        val year = SimpleDateFormat("yyyy", Locale.US).format(Date())
        return "JS-$year-$randomNum"
    }

    fun generateMockOtp(): String {
        return (100000..999999).random().toString()
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        return sdf.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        return sdf.format(Date(timestamp))
    }
}
