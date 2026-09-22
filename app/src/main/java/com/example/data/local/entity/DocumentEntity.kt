package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "documents",
    indices = [Index(value = ["applicationId"])]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val applicationId: Long,
    val docType: String, // IDENTITY, BIRTH_CERT, ADDRESS, BANK_PASSBOOK, PHOTO, INCOME_CERT
    val fileName: String,
    val fileSize: String,
    val fileFormat: String,
    val uploadedAt: Long = System.currentTimeMillis(),
    val verificationStatus: String = "PENDING", // PENDING, VERIFIED, REJECTED
    val officerRemarks: String = ""
)
