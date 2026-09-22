package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.ApplicationEntity
import com.example.data.local.entity.BankDetailsEntity
import com.example.data.local.entity.BeneficiaryEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.StatusHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {

    // Applications
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: ApplicationEntity): Long

    @Update
    suspend fun updateApplication(application: ApplicationEntity)

    @Query("SELECT * FROM applications WHERE id = :id")
    suspend fun getApplicationById(id: Long): ApplicationEntity?

    @Query("SELECT * FROM applications WHERE applicationNumber = :appNumber LIMIT 1")
    suspend fun getApplicationByNumber(appNumber: String): ApplicationEntity?

    @Query("SELECT * FROM applications WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getApplicationsByUserId(userId: Long): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE userId = :userId AND status = 'DRAFT' ORDER BY updatedAt DESC")
    fun getDraftsByUserId(userId: Long): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE userId = :userId AND status != 'DRAFT' ORDER BY updatedAt DESC")
    fun getSubmittedByUserId(userId: Long): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications ORDER BY updatedAt DESC")
    fun getAllApplications(): Flow<List<ApplicationEntity>>

    @Query("DELETE FROM applications WHERE id = :id")
    suspend fun deleteApplicationById(id: Long)

    // Beneficiary
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeneficiary(beneficiary: BeneficiaryEntity): Long

    @Query("SELECT * FROM beneficiaries WHERE applicationId = :applicationId LIMIT 1")
    suspend fun getBeneficiaryByApplicationId(applicationId: Long): BeneficiaryEntity?

    @Query("DELETE FROM beneficiaries WHERE applicationId = :applicationId")
    suspend fun deleteBeneficiaryByAppId(applicationId: Long)

    // Bank Details
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankDetails(bankDetails: BankDetailsEntity): Long

    @Query("SELECT * FROM bank_details WHERE applicationId = :applicationId LIMIT 1")
    suspend fun getBankDetailsByApplicationId(applicationId: Long): BankDetailsEntity?

    @Query("DELETE FROM bank_details WHERE applicationId = :applicationId")
    suspend fun deleteBankDetailsByAppId(applicationId: Long)

    // Documents
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<DocumentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Query("SELECT * FROM documents WHERE applicationId = :applicationId")
    suspend fun getDocumentsByApplicationId(applicationId: Long): List<DocumentEntity>

    @Query("DELETE FROM documents WHERE applicationId = :applicationId")
    suspend fun deleteDocumentsByAppId(applicationId: Long)

    // Status History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusHistory(history: StatusHistoryEntity): Long

    @Query("SELECT * FROM status_history WHERE applicationId = :applicationId ORDER BY timestamp ASC")
    suspend fun getStatusHistory(applicationId: Long): List<StatusHistoryEntity>

    @Query("SELECT * FROM status_history WHERE applicationId = :applicationId ORDER BY timestamp ASC")
    fun observeStatusHistory(applicationId: Long): Flow<List<StatusHistoryEntity>>

    // Status Updates
    @Query("UPDATE applications SET status = :status, remarks = :remarks, correctionNotes = :correctionNotes, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, remarks: String, correctionNotes: String, updatedAt: Long)
}
