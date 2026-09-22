package com.example.data.repository

import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.NotificationPreferencesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationRepository(
    private val notificationDao: NotificationDao,
    private val userDao: UserDao
) {
    // Immediate real-time alert trigger for in-app alert banner & simulated SMS alert
    private val _realtimeNotificationEvent = MutableSharedFlow<NotificationEntity>(extraBufferCapacity = 10)
    val realtimeNotificationEvent: SharedFlow<NotificationEntity> = _realtimeNotificationEvent.asSharedFlow()

    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsForUser(userId)
    }

    fun getUnreadCount(userId: Long): Flow<Int> {
        return notificationDao.getUnreadCount(userId)
    }

    fun getPreferences(userId: Long): Flow<NotificationPreferencesEntity?> {
        return notificationDao.getPreferences(userId)
    }

    suspend fun getOrCreatePreferences(userId: Long): NotificationPreferencesEntity {
        val existing = notificationDao.getPreferencesSync(userId)
        if (existing != null) return existing

        val user = userDao.getUserById(userId)
        val defaultPrefs = NotificationPreferencesEntity(
            userId = userId,
            inAppAlertsEnabled = true,
            smsAlertsEnabled = true,
            emailAlertsEnabled = true,
            statusChangeAlerts = true,
            correctionAlerts = true,
            paymentAlerts = true,
            notificationEmail = user?.email?.ifBlank { "iamcaboy@gmail.com" } ?: "iamcaboy@gmail.com",
            notificationMobile = user?.mobileNumber ?: "",
            soundVibrationEnabled = true
        )
        notificationDao.savePreferences(defaultPrefs)
        return defaultPrefs
    }

    suspend fun updatePreferences(preferences: NotificationPreferencesEntity) {
        notificationDao.savePreferences(preferences)
    }

    suspend fun dispatchNotification(
        userId: Long,
        applicationId: Long?,
        applicationNumber: String,
        title: String,
        message: String,
        type: String,
        oldStatus: String = "",
        newStatus: String = ""
    ) {
        val prefs = getOrCreatePreferences(userId)

        // Check if user has enabled alerts for this type
        val isStatusChange = type == "STATUS_CHANGE"
        val isCorrection = type == "CORRECTION_REQUIRED"
        val isPayment = type == "PAYMENT"

        if (isStatusChange && !prefs.statusChangeAlerts) return
        if (isCorrection && !prefs.correctionAlerts) return
        if (isPayment && !prefs.paymentAlerts) return

        // Channels active
        val channels = mutableListOf<String>()
        if (prefs.inAppAlertsEnabled) channels.add("IN_APP")
        if (prefs.smsAlertsEnabled) channels.add("SMS")
        if (prefs.emailAlertsEnabled) channels.add("EMAIL")

        if (channels.isEmpty()) return

        val notification = NotificationEntity(
            userId = userId,
            applicationId = applicationId,
            applicationNumber = applicationNumber,
            title = title,
            message = message,
            type = type,
            oldStatus = oldStatus,
            newStatus = newStatus,
            isRead = false,
            channel = channels.joinToString(", "),
            createdAt = System.currentTimeMillis()
        )

        notificationDao.insertNotification(notification)
        _realtimeNotificationEvent.tryEmit(notification)
    }

    suspend fun markAsRead(id: Long) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllAsRead(userId: Long) {
        notificationDao.markAllAsRead(userId)
    }

    suspend fun clearAll(userId: Long) {
        notificationDao.clearAllNotifications(userId)
    }
}
