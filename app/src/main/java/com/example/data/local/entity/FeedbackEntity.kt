package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "feedbacks",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["category"]),
        Index(value = ["createdAt"])
    ]
)
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userEmail: String = "iamcaboy@gmail.com",
    val userMobile: String = "",
    val applicationId: Long? = null,
    val applicationNumber: String = "",
    val easeOfUseRating: Int, // 1 to 5
    val clarityOfInfoRating: Int, // 1 to 5
    val serviceQualityRating: Int, // 1 to 5
    val overallRating: Float, // Calculated average
    val category: String, // 'Ease of Use', 'Clarity of Information', 'Service Quality', 'General Suggestions', 'Fee & Payment Process'
    val comments: String,
    val adminRemarks: String = "",
    val isReviewedByAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
