package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bank_details",
    indices = [Index(value = ["applicationId"])]
)
data class BankDetailsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val applicationId: Long,
    val accountHolderName: String,
    val bankName: String,
    val branchName: String,
    val accountNumber: String,
    val ifscCode: String,
    val verificationStatus: String = "VERIFIED" // VERIFIED, PENDING
)
