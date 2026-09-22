package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "beneficiaries",
    indices = [Index(value = ["applicationId"])]
)
data class BeneficiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val applicationId: Long,
    val childName: String,
    val dob: String,
    val gender: String,
    val birthRegNumber: String,
    val relationship: String,
    val schoolDetails: String = "",
    val gradeOrClass: String = ""
)
