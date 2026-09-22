package com.example.data.repository

import com.example.data.local.dao.AdminDao
import com.example.data.local.dao.ApplicationDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AdminEntity
import com.example.data.local.entity.ApplicationEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BankDetailsEntity
import com.example.data.local.entity.BeneficiaryEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.StatusHistoryEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.ApplicationStatus
import com.example.data.model.ApplicationWithDetails
import com.example.util.SecurityUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminRepository(
    private val adminDao: AdminDao,
    private val applicationDao: ApplicationDao,
    private val userDao: UserDao,
    private val notificationRepository: NotificationRepository? = null,
    private val feedbackRepository: FeedbackRepository? = null
) {
    private val _currentAdmin = MutableStateFlow<AdminEntity?>(null)
    val currentAdmin: StateFlow<AdminEntity?> = _currentAdmin.asStateFlow()

    suspend fun seedInitialDataIfNeeded() {
        // Seed Admin if not exists
        val adminCount = adminDao.getAdminCount()
        if (adminCount == 0) {
            val defaultAdmin = AdminEntity(
                username = "admin@jansahayata.gov.in",
                fullName = "Dr. Rajesh Kumar Sharma",
                role = "NODAL_OFFICER",
                passwordHash = SecurityUtil.hashPassword("Admin@123"),
                department = "Department of Social Welfare & DBT",
                district = "All",
                isActive = true
            )
            adminDao.insertAdmin(defaultAdmin)

            // Also seed a demo citizen user
            val citizenUser = UserEntity(
                fullName = "Suman Devi",
                mobileNumber = "9876543210",
                email = "suman.devi@example.com",
                passwordHash = SecurityUtil.hashPassword("Citizen@123"),
                state = "Uttar Pradesh",
                district = "Lucknow",
                address = "Village Rampur, Post Mohanlalganj",
                aadhaarLast4 = "7821",
                isVerified = true
            )
            val userId = userDao.insertUser(citizenUser)

            // Seed sample application 1 (SUBMITTED)
            seedSampleApplication(
                userId = userId,
                appNumber = "JS-2026-481902",
                status = ApplicationStatus.SUBMITTED.code,
                applicantName = "Suman Devi",
                parentName = "Rameshwar Prasad",
                dob = "12/04/1992",
                gender = "Female",
                category = "OBC",
                mobile = "9876543210",
                address = "House 45, Rampur Village",
                district = "Lucknow",
                block = "Mohanlalganj",
                pinCode = "226301",
                childName = "Ananya Devi",
                childDob = "18/08/2021",
                relationship = "Daughter",
                birthReg = "BR-UP-2021-98421",
                bankName = "State Bank of India",
                branch = "Mohanlalganj Branch",
                accNumber = "31948572019",
                ifsc = "SBIN0001234",
                remarks = "Application submitted online by citizen.",
                timeOffsetHours = 48
            )

            // Seed sample application 2 (DOCUMENT_VERIFICATION)
            seedSampleApplication(
                userId = userId,
                appNumber = "JS-2026-619284",
                status = ApplicationStatus.DOCUMENT_VERIFICATION.code,
                applicantName = "Pooja Verma",
                parentName = "Suresh Verma",
                dob = "05/11/1990",
                gender = "Female",
                category = "General (EWS)",
                mobile = "9812345678",
                address = "Ward 12, Civil Lines",
                district = "Kanpur",
                block = "Kalyanpur",
                pinCode = "208017",
                childName = "Kavya Verma",
                childDob = "10/02/2020",
                relationship = "Daughter",
                birthReg = "BR-UP-2020-41098",
                bankName = "Punjab National Bank",
                branch = "Civil Lines Branch",
                accNumber = "0194002100094821",
                ifsc = "PUNB0019400",
                remarks = "Documents forwarded to Block Nodal Officer for physical verification.",
                timeOffsetHours = 96
            )

            // Seed sample application 3 (RETURNED_FOR_CORRECTION)
            seedSampleApplication(
                userId = userId,
                appNumber = "JS-2026-724510",
                status = ApplicationStatus.RETURNED_FOR_CORRECTION.code,
                applicantName = "Anita Kumari",
                parentName = "Santosh Kumar",
                dob = "24/09/1994",
                gender = "Female",
                category = "SC",
                mobile = "9765432109",
                address = "Gram Panchayat Sarojini Nagar",
                district = "Lucknow",
                block = "Sarojini Nagar",
                pinCode = "226008",
                childName = "Rani Kumari",
                childDob = "05/06/2022",
                relationship = "Daughter",
                birthReg = "BR-UP-2022-77123",
                bankName = "Bank of Baroda",
                branch = "Sarojini Nagar",
                accNumber = "49180200003819",
                ifsc = "BARB0SAROJI",
                remarks = "Please re-upload clearer birth certificate showing date of birth clearly.",
                correctionNotes = "Birth certificate page is blurred. Please upload a high-contrast scan.",
                timeOffsetHours = 120
            )

            // Seed sample application 4 (APPROVED)
            seedSampleApplication(
                userId = userId,
                appNumber = "JS-2026-892147",
                status = ApplicationStatus.APPROVED.code,
                applicantName = "Meera Bai",
                parentName = "Ram Charan",
                dob = "15/01/1988",
                gender = "Female",
                category = "ST",
                mobile = "9654321098",
                address = "Near Panchayat Bhavan",
                district = "Varanasi",
                block = "Pindra",
                pinCode = "221206",
                childName = "Priyanka Bai",
                childDob = "01/01/2019",
                relationship = "Daughter",
                birthReg = "BR-UP-2019-11029",
                bankName = "Union Bank of India",
                branch = "Pindra Branch",
                accNumber = "582102010009412",
                ifsc = "UBIN0558214",
                remarks = "Application verified by District Welfare Committee. Sanction order issued.",
                timeOffsetHours = 180
            )

            // Seed initial notifications for citizen
            notificationRepository?.dispatchNotification(
                userId = userId,
                applicationId = null,
                applicationNumber = "JS-2026-481902",
                title = "Application Registered Successfully",
                message = "Your Jan Sahayata application JS-2026-481902 has been successfully submitted. Statutory fee ₹280 paid via UPI QR. Current status: SUBMITTED.",
                type = "PAYMENT",
                oldStatus = "",
                newStatus = "SUBMITTED"
            )
            notificationRepository?.dispatchNotification(
                userId = userId,
                applicationId = null,
                applicationNumber = "JS-2026-481902",
                title = "Officer Assigned for Scrutiny",
                message = "Dr. Rajesh Kumar Sharma (Nodal Officer) has been assigned to verify your uploaded certificates.",
                type = "STATUS_CHANGE",
                oldStatus = "SUBMITTED",
                newStatus = "UNDER_REVIEW"
            )

            // Seed initial feedback
            feedbackRepository?.seedInitialFeedbacksIfNeeded(userId)
        }
    }

    private suspend fun seedSampleApplication(
        userId: Long,
        appNumber: String,
        status: String,
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
        childName: String,
        childDob: String,
        relationship: String,
        birthReg: String,
        bankName: String,
        branch: String,
        accNumber: String,
        ifsc: String,
        remarks: String,
        correctionNotes: String = "",
        timeOffsetHours: Long
    ) {
        val baseTime = System.currentTimeMillis() - (timeOffsetHours * 3600 * 1000)
        val app = ApplicationEntity(
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
            remarks = remarks,
            correctionNotes = correctionNotes,
            submittedAt = baseTime,
            feeAmount = 280.0,
            feePaymentStatus = "PAID",
            feeTransactionId = "UPI/2026/0922/" + (10000000..99999999).random(),
            feePaidAt = baseTime,
            updatedAt = baseTime + (3600 * 1000)
        )
        val appId = applicationDao.insertApplication(app)

        val beneficiary = BeneficiaryEntity(
            applicationId = appId,
            childName = childName,
            dob = childDob,
            gender = "Female",
            birthRegNumber = birthReg,
            relationship = relationship,
            schoolDetails = "Government Primary School",
            gradeOrClass = "Class 1"
        )
        applicationDao.insertBeneficiary(beneficiary)

        val bank = BankDetailsEntity(
            applicationId = appId,
            accountHolderName = applicantName,
            bankName = bankName,
            branchName = branch,
            accountNumber = accNumber,
            ifscCode = ifsc,
            verificationStatus = "VERIFIED"
        )
        applicationDao.insertBankDetails(bank)

        val docs = listOf(
            DocumentEntity(applicationId = appId, docType = "IDENTITY", fileName = "voter_id_${applicantName.take(4)}.pdf", fileSize = "420 KB", fileFormat = "PDF", uploadedAt = baseTime, verificationStatus = if (status == ApplicationStatus.APPROVED.code) "VERIFIED" else "PENDING"),
            DocumentEntity(applicationId = appId, docType = "BIRTH_CERT", fileName = "birth_certificate_$childName.pdf", fileSize = "680 KB", fileFormat = "PDF", uploadedAt = baseTime, verificationStatus = if (status == ApplicationStatus.RETURNED_FOR_CORRECTION.code) "REJECTED" else "VERIFIED", officerRemarks = if (status == ApplicationStatus.RETURNED_FOR_CORRECTION.code) "Unclear scan" else ""),
            DocumentEntity(applicationId = appId, docType = "ADDRESS", fileName = "ration_card_copy.pdf", fileSize = "510 KB", fileFormat = "PDF", uploadedAt = baseTime, verificationStatus = "VERIFIED"),
            DocumentEntity(applicationId = appId, docType = "BANK_PASSBOOK", fileName = "bank_passbook_front.jpg", fileSize = "890 KB", fileFormat = "JPG", uploadedAt = baseTime, verificationStatus = "VERIFIED"),
            DocumentEntity(applicationId = appId, docType = "PHOTO", fileName = "beneficiary_photo.png", fileSize = "320 KB", fileFormat = "PNG", uploadedAt = baseTime, verificationStatus = "VERIFIED")
        )
        applicationDao.insertDocuments(docs)

        // Status history
        applicationDao.insertStatusHistory(
            StatusHistoryEntity(applicationId = appId, status = ApplicationStatus.SUBMITTED.code, changedBy = "Citizen ($applicantName)", remarks = "Application submitted via online portal.", timestamp = baseTime)
        )
        if (status != ApplicationStatus.SUBMITTED.code) {
            applicationDao.insertStatusHistory(
                StatusHistoryEntity(applicationId = appId, status = status, changedBy = "Verification Officer (Admin)", remarks = remarks, timestamp = baseTime + (3600 * 1000))
            )
        }

        adminDao.insertAuditLog(
            AuditLogEntity(
                applicationNumber = appNumber,
                action = "SYSTEM_INITIALIZE",
                performedBy = "System Administrator",
                details = "Initialized scheme application $appNumber with status $status."
            )
        )
    }

    suspend fun adminLogin(username: String, password: String): Result<AdminEntity> {
        return try {
            val admin = adminDao.getAdminByUsername(username.trim())
                ?: return Result.failure(Exception("Admin account not found."))

            if (!SecurityUtil.verifyPassword(password, admin.passwordHash)) {
                return Result.failure(Exception("Invalid administrative password."))
            }

            _currentAdmin.value = admin
            Result.success(admin)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun adminLogout() {
        _currentAdmin.value = null
    }

    fun getAllApplications(): Flow<List<ApplicationEntity>> {
        return applicationDao.getAllApplications()
    }

    suspend fun updateApplicationStatus(
        appId: Long,
        appNumber: String,
        newStatus: ApplicationStatus,
        remarks: String,
        correctionNotes: String = ""
    ): Result<Boolean> {
        return try {
            val admin = _currentAdmin.value
            val changedByName = admin?.fullName ?: "Verification Officer"
            val now = System.currentTimeMillis()

            applicationDao.updateStatus(
                id = appId,
                status = newStatus.code,
                remarks = remarks,
                correctionNotes = correctionNotes,
                updatedAt = now
            )

            applicationDao.insertStatusHistory(
                StatusHistoryEntity(
                    applicationId = appId,
                    status = newStatus.code,
                    changedBy = "$changedByName (${admin?.role ?: "Officer"})",
                    remarks = remarks.ifEmpty { "Status updated to ${newStatus.labelEn}" },
                    timestamp = now
                )
            )

            adminDao.insertAuditLog(
                AuditLogEntity(
                    applicationNumber = appNumber,
                    action = "STATUS_UPDATE",
                    performedBy = "$changedByName (${admin?.username ?: "admin"})",
                    details = "Status changed to ${newStatus.code}. Remarks: $remarks"
                )
            )

            // Real-time Notification dispatch to Citizen
            val existingApp = applicationDao.getApplicationById(appId)
            if (existingApp != null && notificationRepository != null) {
                val notifType = if (newStatus == ApplicationStatus.RETURNED_FOR_CORRECTION) "CORRECTION_REQUIRED" else "STATUS_CHANGE"
                val notifTitle = when (newStatus) {
                    ApplicationStatus.APPROVED -> "Application Approved ✓"
                    ApplicationStatus.REJECTED -> "Application Rejected"
                    ApplicationStatus.RETURNED_FOR_CORRECTION -> "Correction Required for $appNumber"
                    ApplicationStatus.DOCUMENT_VERIFICATION -> "Documents in Verification"
                    ApplicationStatus.UNDER_REVIEW -> "Application Under Scrutiny"
                    else -> "Status Updated: ${newStatus.labelEn}"
                }
                val notifMsg = buildString {
                    append("File $appNumber: Status changed to ${newStatus.labelEn}.")
                    if (remarks.isNotBlank()) append(" Remarks: $remarks.")
                    if (correctionNotes.isNotBlank()) append(" Action needed: $correctionNotes")
                }

                notificationRepository.dispatchNotification(
                    userId = existingApp.userId,
                    applicationId = appId,
                    applicationNumber = appNumber,
                    title = notifTitle,
                    message = notifMsg,
                    type = notifType,
                    oldStatus = existingApp.status,
                    newStatus = newStatus.code
                )
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyDocument(document: DocumentEntity, isVerified: Boolean, remarks: String) {
        val updated = document.copy(
            verificationStatus = if (isVerified) "VERIFIED" else "REJECTED",
            officerRemarks = remarks
        )
        applicationDao.updateDocument(updated)
        val admin = _currentAdmin.value
        adminDao.insertAuditLog(
            AuditLogEntity(
                applicationNumber = "APP-${document.applicationId}",
                action = "DOCUMENT_VERIFIED",
                performedBy = admin?.fullName ?: "Officer",
                details = "Doc: ${document.docType} marked ${updated.verificationStatus}. Remarks: $remarks"
            )
        )
    }

    fun getAuditLogs(): Flow<List<AuditLogEntity>> {
        return adminDao.getAllAuditLogs()
    }
}
