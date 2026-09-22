package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["applicationNumber"]),
        Index(value = ["isRead"]),
        Index(value = ["createdAt"])
    ]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val applicationId: Long? = null,
    val applicationNumber: String = "",
    val title: String,
    val message: String,
    val type: String = "STATUS_CHANGE", // STATUS_CHANGE, REMARKS, CORRECTION_REQUIRED, PAYMENT, GENERAL
    val oldStatus: String = "",
    val newStatus: String = "",
    val isRead: Boolean = false,
    val channel: String = "IN_APP", // IN_APP, SMS, EMAIL
    val createdAt: Long = System.currentTimeMillis()
)
