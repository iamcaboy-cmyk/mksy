package com.example.data.repository

import com.example.data.local.dao.FeedbackDao
import com.example.data.local.entity.FeedbackEntity
import kotlinx.coroutines.flow.Flow

class FeedbackRepository(
    private val feedbackDao: FeedbackDao
) {
    fun getAllFeedbacks(): Flow<List<FeedbackEntity>> {
        return feedbackDao.getAllFeedbacks()
    }

    fun getFeedbacksForUser(userId: Long): Flow<List<FeedbackEntity>> {
        return feedbackDao.getFeedbacksForUser(userId)
    }

    fun getAverageRating(): Flow<Float?> {
        return feedbackDao.getAverageRating()
    }

    suspend fun submitFeedback(
        userId: Long,
        userName: String,
        userEmail: String = "iamcaboy@gmail.com",
        userMobile: String = "",
        applicationId: Long? = null,
        applicationNumber: String = "",
        easeOfUseRating: Int,
        clarityOfInfoRating: Int,
        serviceQualityRating: Int,
        category: String,
        comments: String
    ): Result<Long> {
        return try {
            val overall = (easeOfUseRating + clarityOfInfoRating + serviceQualityRating) / 3.0f
            val feedback = FeedbackEntity(
                userId = userId,
                userName = userName,
                userEmail = userEmail.ifBlank { "iamcaboy@gmail.com" },
                userMobile = userMobile,
                applicationId = applicationId,
                applicationNumber = applicationNumber,
                easeOfUseRating = easeOfUseRating,
                clarityOfInfoRating = clarityOfInfoRating,
                serviceQualityRating = serviceQualityRating,
                overallRating = overall,
                category = category,
                comments = comments,
                adminRemarks = "",
                isReviewedByAdmin = false,
                createdAt = System.currentTimeMillis()
            )
            val id = feedbackDao.insertFeedback(feedback)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAdminReview(id: Long, remarks: String, isReviewed: Boolean = true): Result<Boolean> {
        return try {
            feedbackDao.updateAdminReview(id, isReviewed, remarks)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun seedInitialFeedbacksIfNeeded(userId: Long) {
        val count = feedbackDao.getFeedbackCount()
        if (count == 0) {
            val sample1 = FeedbackEntity(
                userId = userId,
                userName = "Suman Devi",
                userEmail = "iamcaboy@gmail.com",
                userMobile = "9876543210",
                applicationNumber = "JS-2026-481902",
                easeOfUseRating = 5,
                clarityOfInfoRating = 4,
                serviceQualityRating = 5,
                overallRating = 4.67f,
                category = "Ease of Use",
                comments = "The application wizard in Hindi was very straightforward. The UPI QR code payment of ₹280 worked instantly without visiting any CSC center!",
                adminRemarks = "Acknowledged. Citizen payment receipt verified through treasury portal.",
                isReviewedByAdmin = true,
                createdAt = System.currentTimeMillis() - 86400000L
            )
            val sample2 = FeedbackEntity(
                userId = userId,
                userName = "Pooja Verma",
                userEmail = "pooja.verma@example.com",
                userMobile = "9812345678",
                applicationNumber = "JS-2026-619284",
                easeOfUseRating = 4,
                clarityOfInfoRating = 4,
                serviceQualityRating = 4,
                overallRating = 4.0f,
                category = "Clarity of Information",
                comments = "Document requirements checklist helped me prepare my income and caste certificates beforehand. Notification alert was received right away.",
                adminRemarks = "",
                isReviewedByAdmin = false,
                createdAt = System.currentTimeMillis() - 43200000L
            )
            feedbackDao.insertFeedback(sample1)
            feedbackDao.insertFeedback(sample2)
        }
    }
}
