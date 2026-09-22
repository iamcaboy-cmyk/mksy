package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "status_history",
    indices = [Index(value = ["applicationId"])]
)
data class StatusHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val applicationId: Long,
    val status: String,
    val changedBy: String,
    val remarks: String,
    val timestamp: Long = System.currentTimeMillis()
)
