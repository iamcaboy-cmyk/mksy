package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_preferences")
data class NotificationPreferencesEntity(
    @PrimaryKey
    val userId: Long,
    val inAppAlertsEnabled: Boolean = true,
    val smsAlertsEnabled: Boolean = true,
    val emailAlertsEnabled: Boolean = true,
    val statusChangeAlerts: Boolean = true,
    val correctionAlerts: Boolean = true,
    val paymentAlerts: Boolean = true,
    val notificationEmail: String = "iamcaboy@gmail.com",
    val notificationMobile: String = "",
    val soundVibrationEnabled: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
