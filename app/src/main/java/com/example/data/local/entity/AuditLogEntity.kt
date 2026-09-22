package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audit_logs",
    indices = [Index(value = ["applicationNumber"])]
)
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val applicationNumber: String,
    val action: String, // CREATE, SUBMIT, STATUS_CHANGE, DOCUMENT_VERIFY, RETURN_CORRECTION, EDIT
    val performedBy: String,
    val details: String,
    val ipAddress: String = "10.14.20.1",
    val timestamp: Long = System.currentTimeMillis()
)
