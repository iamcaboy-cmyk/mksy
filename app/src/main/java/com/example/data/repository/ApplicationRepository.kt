package com.example.data.repository

import com.example.data.local.dao.AdminDao
import com.example.data.local.dao.ApplicationDao
import com.example.data.local.entity.ApplicationEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BankDetailsEntity
import com.example.data.local.entity.BeneficiaryEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.StatusHistoryEntity
import com.example.data.model.ApplicationStatus
import com.example.data.model.ApplicationWithDetails
import com.example.util.SecurityUtil
import kotlinx.coroutines.flow.Flow

class ApplicationRepository(
    private val applicationDao: ApplicationDao,
    private val adminDao: AdminDao,
    private val notificationRepository: NotificationRepository? = null
) {

    fun getUserApplications(userId: Long): Flow<List<ApplicationEntity>> {
        return applicationDao.getApplicationsByUserId(userId)
    }

    fun getUserDrafts(userId: Long): Flow<List<ApplicationEntity>> {
        return applicationDao.getDraftsByUserId(userId)
    }

    fun getUserSubmitted(userId: Long): Flow<List<ApplicationEntity>> {
        return applicationDao.getSubmittedByUserId(userId)
    }

    suspend fun getApplicationWithDetails(appId: Long): ApplicationWithDetails? {
        val app = applicationDao.getApplicationById(appId) ?: return null
        val beneficiary = applicationDao.getBeneficiaryByApplicationId(appId)
        val bank = applicationDao.getBankDetailsByApplicationId(appId)
        val docs = applicationDao.getDocumentsByApplicationId(appId)
        val history = applicationDao.getStatusHistory(appId)
        return ApplicationWithDetails(
            application = app,
            beneficiary = beneficiary,
            bankDetails = bank,
            documents = docs,
            statusHistory = history
        )
    }

    suspend fun getApplicationByNumber(appNumber: String): ApplicationWithDetails? {
        val app = applicationDao.getApplicationByNumber(appNumber.trim()) ?: return null
        return getApplicationWithDetails(app.id)
    }

    suspend fun saveApplication(
        existingAppId: Long? = null,
        userId: Long,
        applicantName: String,
        parentName: String,
        dob: String,
        gender: String,
        category: String,
        mobile: String,
        address: String,
        district: String,
        block: String,
        pinCode: String,
        beneficiaryName: String,
        beneficiaryDob: String,
        beneficiaryGender: String,
        birthRegNumber: String,
        relationship: String,
        schoolDetails: String,
        accountHolderName: String,
        bankName: String,
        branchName: String,
        accountNumber: String,
        ifscCode: String,
        isDraft: Boolean,
        documents: List<DocumentEntity>,
        feePaymentStatus: String = "PAID",
        feeTransactionId: String = ""
    ): Result<ApplicationEntity> {
        return try {
            val appNumber = if (existingAppId != null) {
                val existing = applicationDao.getApplicationById(existingAppId)
                existing?.applicationNumber ?: SecurityUtil.generateApplicationNumber()
            } else {
                SecurityUtil.generateApplicationNumber()
            }

            val status = if (isDraft) ApplicationStatus.DRAFT.code else ApplicationStatus.SUBMITTED.code
            val submissionTime = if (!isDraft) System.currentTimeMillis() else null
            val actualTxId = if (!isDraft) {
                if (feeTransactionId.isNotBlank()) feeTransactionId else "UPI/2026/0922/" + (10000000..99999999).random()
            } else ""

            val appEntity = ApplicationEntity(
                id = existingAppId ?: 0,
                applicationNumber = appNumber,
                userId = userId,
                status = status,
                applicantName = applicantName,
                parentName = parentName,
                dob = dob,
                gender = gender,
                category = category,
                mobile = mobile,
                address = address,
                district = district,
                block = block,
                pinCode = pinCode,
                eligibilityPassed = true,
                remarks = if (isDraft) "Application saved as draft." else "Application submitted online by citizen.",
                correctionNotes = "",
                submittedAt = submissionTime,
                feeAmount = 280.0,
                feePaymentStatus = if (isDraft) "PENDING" else feePaymentStatus,
                feeTransactionId = actualTxId,
                feePaidAt = if (!isDraft) submissionTime else null,
                updatedAt = System.currentTimeMillis()
            )

            val actualAppId = if (existingAppId != null && existingAppId > 0) {
                applicationDao.updateApplication(appEntity)
                // Clean old related records to replace cleanly
                applicationDao.deleteBeneficiaryByAppId(existingAppId)
                applicationDao.deleteBankDetailsByAppId(existingAppId)
                applicationDao.deleteDocumentsByAppId(existingAppId)
                existingAppId
            } else {
                applicationDao.insertApplication(appEntity)
            }

            // Insert Beneficiary
            val beneficiary = BeneficiaryEntity(
                applicationId = actualAppId,
                childName = beneficiaryName,
                dob = beneficiaryDob,
                gender = beneficiaryGender,
                birthRegNumber = birthRegNumber,
                relationship = relationship,
                schoolDetails = schoolDetails,
                gradeOrClass = "Class 1-12 Scheme"
            )
            applicationDao.insertBeneficiary(beneficiary)

            // Insert Bank Details
            val bank = BankDetailsEntity(
                applicationId = actualAppId,
                accountHolderName = accountHolderName,
                bankName = bankName,
                branchName = branchName,
                accountNumber = accountNumber,
                ifscCode = ifscCode.uppercase(),
                verificationStatus = "VERIFIED"
            )
            applicationDao.insertBankDetails(bank)

            // Insert Documents
            val docEntities = documents.map { it.copy(id = 0, applicationId = actualAppId) }
            applicationDao.insertDocuments(docEntities)

            // Add Status History
            val history = StatusHistoryEntity(
                applicationId = actualAppId,
                status = status,
                changedBy = "Citizen ($applicantName)",
                remarks = if (isDraft) "Application saved in draft mode." else "Application formally submitted for initial scrutiny.",
                timestamp = System.currentTimeMillis()
            )
            applicationDao.insertStatusHistory(history)

            // Audit log
            adminDao.insertAuditLog(
                AuditLogEntity(
                    applicationNumber = appNumber,
                    action = if (isDraft) "DRAFT_SAVED" else "APPLICATION_SUBMITTED",
                    performedBy = "Citizen ($applicantName)",
                    details = "Application $appNumber status set to $status."
                )
            )

            if (!isDraft && notificationRepository != null) {
                notificationRepository.dispatchNotification(
                    userId = userId,
                    applicationId = actualAppId,
                    applicationNumber = appNumber,
                    title = "Application Submitted (₹280 Fee Paid)",
                    message = "Your Jan Sahayata application $appNumber has been submitted. E-Challan: $actualTxId. Status: SUBMITTED.",
                    type = "PAYMENT",
                    oldStatus = "",
                    newStatus = "SUBMITTED"
                )
            }

            Result.success(appEntity.copy(id = actualAppId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDraft(appId: Long) {
        applicationDao.deleteBeneficiaryByAppId(appId)
        applicationDao.deleteBankDetailsByAppId(appId)
        applicationDao.deleteDocumentsByAppId(appId)
        applicationDao.deleteApplicationById(appId)
    }

    fun observeStatusHistory(appId: Long): Flow<List<StatusHistoryEntity>> {
        return applicationDao.observeStatusHistory(appId)
    }
}
