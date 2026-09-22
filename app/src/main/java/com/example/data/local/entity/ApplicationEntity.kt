package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "applications",
    indices = [
        Index(value = ["applicationNumber"], unique = true),
        Index(value = ["userId"]),
        Index(value = ["status"]),
        Index(value = ["district"])
    ]
)
data class ApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val applicationNumber: String,
    val userId: Long,
    val status: String, // DRAFT, SUBMITTED, UNDER_REVIEW, DOCUMENT_VERIFICATION, APPROVED, REJECTED, RETURNED_FOR_CORRECTION
    val applicantName: String,
    val parentName: String,
    val dob: String,
    val gender: String,
    val category: String,
    val mobile: String,
    val address: String,
    val district: String,
    val block: String,
    val pinCode: String,
    val eligibilityPassed: Boolean = true,
    val remarks: String = "",
    val correctionNotes: String = "",
    val submittedAt: Long? = null,
    val feeAmount: Double = 280.0,
    val feePaymentStatus: String = "PAID", // PENDING, PAID
    val feeTransactionId: String = "",
    val feePaidAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
