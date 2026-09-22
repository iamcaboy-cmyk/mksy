package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity): Long

    @Query("SELECT * FROM feedbacks ORDER BY createdAt DESC")
    fun getAllFeedbacks(): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedbacks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getFeedbacksForUser(userId: Long): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedbacks WHERE category = :category ORDER BY createdAt DESC")
    fun getFeedbacksByCategory(category: String): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedbacks WHERE id = :id LIMIT 1")
    suspend fun getFeedbackById(id: Long): FeedbackEntity?

    @Query("UPDATE feedbacks SET isReviewedByAdmin = :isReviewed, adminRemarks = :remarks WHERE id = :id")
    suspend fun updateAdminReview(id: Long, isReviewed: Boolean, remarks: String)

    @Query("SELECT COUNT(*) FROM feedbacks")
    suspend fun getFeedbackCount(): Int

    @Query("SELECT AVG(overallRating) FROM feedbacks")
    fun getAverageRating(): Flow<Float?>
}
