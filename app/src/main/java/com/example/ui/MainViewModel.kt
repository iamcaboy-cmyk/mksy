package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.JanSahayataDatabase
import com.example.data.local.entity.AdminEntity
import com.example.data.local.entity.ApplicationEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.FeedbackEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.NotificationPreferencesEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.ApplicationStatus
import com.example.data.model.ApplicationWithDetails
import com.example.data.repository.AdminRepository
import com.example.data.repository.ApplicationRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.FeedbackRepository
import com.example.data.repository.NotificationRepository
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    REGISTER,
    LOGIN,
    ADMIN_LOGIN,
    DASHBOARD,
    NEW_APPLICATION,
    APPLICATION_STATUS,
    ADMIN_DASHBOARD,
    ADMIN_APPLICATION_DETAIL,
    RECEIPT
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = JanSahayataDatabase.getInstance(application)
    val authRepository = AuthRepository(db.userDao())
    val notificationRepository = NotificationRepository(db.notificationDao(), db.userDao())
    val feedbackRepository = FeedbackRepository(db.feedbackDao())
    val applicationRepository = ApplicationRepository(db.applicationDao(), db.adminDao(), notificationRepository)
    val adminRepository = AdminRepository(db.adminDao(), db.applicationDao(), db.userDao(), notificationRepository, feedbackRepository)

    // Language State
    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Navigation State
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Toast / Message
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Current logged-in citizen
    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser

    // Current logged-in admin
    val currentAdmin: StateFlow<AdminEntity?> = adminRepository.currentAdmin

    // Citizen Applications
    private val _citizenApplications = MutableStateFlow<List<ApplicationEntity>>(emptyList())
    val citizenApplications: StateFlow<List<ApplicationEntity>> = _citizenApplications.asStateFlow()

    // Real-time Citizen Notifications
    private val _citizenNotifications = MutableStateFlow<List<com.example.data.local.entity.NotificationEntity>>(emptyList())
    val citizenNotifications: StateFlow<List<com.example.data.local.entity.NotificationEntity>> = _citizenNotifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadNotificationsCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _notificationPreferences = MutableStateFlow<com.example.data.local.entity.NotificationPreferencesEntity?>(null)
    val notificationPreferences: StateFlow<com.example.data.local.entity.NotificationPreferencesEntity?> = _notificationPreferences.asStateFlow()

    // Real-time alert banner state
    private val _realtimeAlert = MutableStateFlow<com.example.data.local.entity.NotificationEntity?>(null)
    val realtimeAlert: StateFlow<com.example.data.local.entity.NotificationEntity?> = _realtimeAlert.asStateFlow()

    // Feedback Module State
    val allFeedbacks: StateFlow<List<com.example.data.local.entity.FeedbackEntity>> = feedbackRepository.getAllFeedbacks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val averageFeedbackRating: StateFlow<Float?> = feedbackRepository.getAverageRating()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Dialog State Controls
    var isNotificationCenterOpen = MutableStateFlow(false)
    var isNotificationPreferencesOpen = MutableStateFlow(false)
    var isFeedbackDialogOpen = MutableStateFlow(false)
    var feedbackAppNumberContext = MutableStateFlow("")
    val feedbackAppNumberTarget: StateFlow<String> = feedbackAppNumberContext.asStateFlow()

    fun openNotificationCenter() {
        isNotificationCenterOpen.value = true
    }

    fun closeNotificationCenter() {
        isNotificationCenterOpen.value = false
    }

    // Selected Application for Status Tracking or Admin Scrutiny
    private val _selectedApplication = MutableStateFlow<ApplicationWithDetails?>(null)
    val selectedApplication: StateFlow<ApplicationWithDetails?> = _selectedApplication.asStateFlow()

    // Admin applications list
    val allAdminApplications: StateFlow<List<ApplicationEntity>> = adminRepository.getAllApplications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin audit logs
    val adminAuditLogs: StateFlow<List<AuditLogEntity>> = adminRepository.getAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 7-Step Wizard Form State
    var editingApplicationId: Long? = null
    var wizardCurrentStep = MutableStateFlow(1)

    // Fee payment state for application (₹280)
    var wizardFeeAmount = MutableStateFlow(280.0)
    var wizardFeePaid = MutableStateFlow(true)
    var wizardFeeTransactionId = MutableStateFlow("UPI/2026/0922/8192039281")

    // Step 1: Applicant
    var applicantName = MutableStateFlow("")
    var parentName = MutableStateFlow("")
    var applicantDob = MutableStateFlow("15/07/1994")
    var applicantGender = MutableStateFlow("Female")
    var applicantCategory = MutableStateFlow("OBC")
    var applicantMobile = MutableStateFlow("")
    var applicantAddress = MutableStateFlow("")
    var applicantDistrict = MutableStateFlow("Lucknow")
    var applicantBlock = MutableStateFlow("Sarojini Nagar")
    var applicantPinCode = MutableStateFlow("226008")

    // Step 2: Beneficiary
    var beneficiaryName = MutableStateFlow("")
    var beneficiaryDob = MutableStateFlow("12/03/2021")
    var beneficiaryGender = MutableStateFlow("Female")
    var birthRegNumber = MutableStateFlow("")
    var relationship = MutableStateFlow("Daughter")
    var schoolDetails = MutableStateFlow("Government Primary School")

    // Step 3: Bank
    var accountHolderName = MutableStateFlow("")
    var bankName = MutableStateFlow("State Bank of India")
    var branchName = MutableStateFlow("Civil Lines Branch")
    var accountNumber = MutableStateFlow("")
    var confirmAccountNumber = MutableStateFlow("")
    var ifscCode = MutableStateFlow("SBIN0001234")
    var isBankVerified = MutableStateFlow(true)

    // Step 4: Eligibility Checklist
    var eligIncomeBelowLimit = MutableStateFlow(true)
    var eligStateResident = MutableStateFlow(true)
    var eligAgeValid = MutableStateFlow(true)
    var eligMaxTwoChildren = MutableStateFlow(true)
    var eligNonIncomeTaxPayer = MutableStateFlow(true)

    // Step 5: Documents
    var uploadedDocuments = MutableStateFlow<List<DocumentEntity>>(emptyList())

    // Step 6: Review & Consent
    var declarationAccepted = MutableStateFlow(false)
    var consentAuthorized = MutableStateFlow(false)

    // Step 7: Submitted Receipt
    var submittedAppReceipt = MutableStateFlow<ApplicationWithDetails?>(null)

    init {
        viewModelScope.launch {
            adminRepository.seedInitialDataIfNeeded()
        }

        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    launch {
                        applicationRepository.getUserApplications(user.id).collect { apps ->
                            _citizenApplications.value = apps
                        }
                    }
                    launch {
                        notificationRepository.getNotificationsForUser(user.id).collect { notifs ->
                            _citizenNotifications.value = notifs
                        }
                    }
                    launch {
                        notificationRepository.getUnreadCount(user.id).collect { count ->
                            _unreadCount.value = count
                        }
                    }
                    launch {
                        notificationRepository.getPreferences(user.id).collect { prefs ->
                            _notificationPreferences.value = prefs
                        }
                    }
                } else {
                    _citizenApplications.value = emptyList()
                    _citizenNotifications.value = emptyList()
                    _unreadCount.value = 0
                    _notificationPreferences.value = null
                }
            }
        }

        // Collect real-time notification alerts
        viewModelScope.launch {
            notificationRepository.realtimeNotificationEvent.collect { event ->
                val current = currentUser.value
                if (current != null && current.id == event.userId) {
                    _realtimeAlert.value = event
                }
            }
        }
    }

    fun dismissRealtimeAlert() {
        _realtimeAlert.value = null
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            notificationRepository.markAllAsRead(user.id)
            showMessage("All notifications marked as read.")
        }
    }

    fun clearAllNotifications() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            notificationRepository.clearAll(user.id)
            showMessage("Notifications cleared.")
        }
    }

    fun saveNotificationPreferences(prefs: com.example.data.local.entity.NotificationPreferencesEntity) {
        viewModelScope.launch {
            notificationRepository.updatePreferences(prefs)
            _notificationPreferences.value = prefs
            isNotificationPreferencesOpen.value = false
            showMessage("Notification preferences updated successfully.")
        }
    }

    fun openFeedbackDialog(appNumber: String = "") {
        feedbackAppNumberContext.value = appNumber
        isFeedbackDialogOpen.value = true
    }

    fun closeFeedbackDialog() {
        isFeedbackDialogOpen.value = false
    }

    fun submitFeedback(
        easeOfUse: Int,
        clarityOfInfo: Int,
        serviceQuality: Int,
        category: String,
        comments: String,
        email: String
    ) {
        val user = currentUser.value
        val userId = user?.id ?: 1L
        val userName = user?.fullName ?: "Citizen Applicant"
        val mobile = user?.mobileNumber ?: ""
        val appNumber = feedbackAppNumberContext.value

        viewModelScope.launch {
            val result = feedbackRepository.submitFeedback(
                userId = userId,
                userName = userName,
                userEmail = email.ifBlank { "iamcaboy@gmail.com" },
                userMobile = mobile,
                applicationNumber = appNumber,
                easeOfUseRating = easeOfUse,
                clarityOfInfoRating = clarityOfInfo,
                serviceQualityRating = serviceQuality,
                category = category,
                comments = comments
            )
            result.onSuccess {
                showMessage("Thank you! Feedback submitted to Welfare Oversight Cell.")
            }.onFailure { err ->
                showMessage("Failed to submit feedback: ${err.message}")
            }
        }
    }

    fun updateAdminFeedbackReview(feedbackId: Long, remarks: String, isReviewed: Boolean = true) {
        viewModelScope.launch {
            feedbackRepository.updateAdminReview(feedbackId, remarks, isReviewed)
            showMessage("Officer response recorded for citizen feedback.")
        }
    }

    fun markWizardFeePaid(transactionId: String) {
        wizardFeePaid.value = true
        wizardFeeTransactionId.value = transactionId
        showMessage("Application fee ₹280 marked as PAID (UTR: $transactionId)")
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun showMessage(msg: String) {
        viewModelScope.launch {
            _userMessage.emit(msg)
        }
    }

    // Reset wizard fields
    fun startNewApplication(existing: ApplicationWithDetails? = null) {
        val user = currentUser.value
        editingApplicationId = existing?.application?.id
        wizardCurrentStep.value = 1

        if (existing != null) {
            applicantName.value = existing.application.applicantName
            parentName.value = existing.application.parentName
            applicantDob.value = existing.application.dob
            applicantGender.value = existing.application.gender
            applicantCategory.value = existing.application.category
            applicantMobile.value = existing.application.mobile
            applicantAddress.value = existing.application.address
            applicantDistrict.value = existing.application.district
            applicantBlock.value = existing.application.block
            applicantPinCode.value = existing.application.pinCode

            existing.beneficiary?.let {
                beneficiaryName.value = it.childName
                beneficiaryDob.value = it.dob
                beneficiaryGender.value = it.gender
                birthRegNumber.value = it.birthRegNumber
                relationship.value = it.relationship
                schoolDetails.value = it.schoolDetails
            }

            existing.bankDetails?.let {
                accountHolderName.value = it.accountHolderName
                bankName.value = it.bankName
                branchName.value = it.branchName
                accountNumber.value = it.accountNumber
                confirmAccountNumber.value = it.accountNumber
                ifscCode.value = it.ifscCode
                isBankVerified.value = true
            }

            uploadedDocuments.value = existing.documents
        } else {
            applicantName.value = user?.fullName ?: ""
            parentName.value = ""
            applicantDob.value = "15/07/1994"
            applicantGender.value = "Female"
            applicantCategory.value = "OBC"
            applicantMobile.value = user?.mobileNumber ?: ""
            applicantAddress.value = user?.address ?: ""
            applicantDistrict.value = user?.district ?: "Lucknow"
            applicantBlock.value = "Central Block"
            applicantPinCode.value = "226001"

            beneficiaryName.value = ""
            beneficiaryDob.value = "10/05/2021"
            beneficiaryGender.value = "Female"
            birthRegNumber.value = ""
            relationship.value = "Daughter"
            schoolDetails.value = "Government Primary School"

            accountHolderName.value = user?.fullName ?: ""
            bankName.value = "State Bank of India"
            branchName.value = "Main Branch"
            accountNumber.value = ""
            confirmAccountNumber.value = ""
            ifscCode.value = "SBIN0001234"
            isBankVerified.value = true

            eligIncomeBelowLimit.value = true
            eligStateResident.value = true
            eligAgeValid.value = true
            eligMaxTwoChildren.value = true
            eligNonIncomeTaxPayer.value = true

            // Pre-seed mock uploaded documents
            uploadedDocuments.value = listOf(
                DocumentEntity(0, 0, "IDENTITY", "Aadhaar_Masked_Card.pdf", "420 KB", "PDF", System.currentTimeMillis(), "VERIFIED"),
                DocumentEntity(0, 0, "BIRTH_CERT", "Beneficiary_Birth_Certificate.pdf", "650 KB", "PDF", System.currentTimeMillis(), "PENDING"),
                DocumentEntity(0, 0, "ADDRESS", "Domicile_Certificate.pdf", "380 KB", "PDF", System.currentTimeMillis(), "VERIFIED"),
                DocumentEntity(0, 0, "BANK_PASSBOOK", "Bank_Passbook_Front.jpg", "890 KB", "JPG", System.currentTimeMillis(), "VERIFIED"),
                DocumentEntity(0, 0, "PHOTO", "Beneficiary_Child_Photo.png", "240 KB", "PNG", System.currentTimeMillis(), "VERIFIED")
            )
        }

        declarationAccepted.value = false
        consentAuthorized.value = false
        navigateTo(Screen.NEW_APPLICATION)
    }

    fun editApplication(appId: Long) {
        viewModelScope.launch {
            val details = applicationRepository.getApplicationWithDetails(appId)
            startNewApplication(details)
        }
    }

    fun submitOrDraftApplication(isDraft: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = applicationRepository.saveApplication(
                existingAppId = editingApplicationId,
                userId = user.id,
                applicantName = applicantName.value,
                parentName = parentName.value,
                dob = applicantDob.value,
                gender = applicantGender.value,
                category = applicantCategory.value,
                mobile = applicantMobile.value,
                address = applicantAddress.value,
                district = applicantDistrict.value,
                block = applicantBlock.value,
                pinCode = applicantPinCode.value,
                beneficiaryName = beneficiaryName.value,
                beneficiaryDob = beneficiaryDob.value,
                beneficiaryGender = beneficiaryGender.value,
                birthRegNumber = birthRegNumber.value,
                relationship = relationship.value,
                schoolDetails = schoolDetails.value,
                accountHolderName = accountHolderName.value,
                bankName = bankName.value,
                branchName = branchName.value,
                accountNumber = accountNumber.value,
                ifscCode = ifscCode.value,
                isDraft = isDraft,
                documents = uploadedDocuments.value,
                feePaymentStatus = if (wizardFeePaid.value) "PAID" else "PENDING",
                feeTransactionId = wizardFeeTransactionId.value
            )

            result.onSuccess { savedApp ->
                val details = applicationRepository.getApplicationWithDetails(savedApp.id)
                if (isDraft) {
                    showMessage("Application saved successfully as Draft (${savedApp.applicationNumber})")
                    navigateTo(Screen.DASHBOARD)
                } else {
                    submittedAppReceipt.value = details
                    _selectedApplication.value = details
                    showMessage("Application ${savedApp.applicationNumber} submitted successfully!")
                    navigateTo(Screen.RECEIPT)
                }
            }.onFailure { err ->
                showMessage("Failed to save: ${err.message}")
            }
        }
    }

    fun viewApplicationStatus(appId: Long) {
        viewModelScope.launch {
            val details = applicationRepository.getApplicationWithDetails(appId)
            _selectedApplication.value = details
            navigateTo(Screen.APPLICATION_STATUS)
        }
    }

    fun searchAndTrackApplication(appNumber: String) {
        viewModelScope.launch {
            val details = applicationRepository.getApplicationByNumber(appNumber.trim())
            if (details != null) {
                _selectedApplication.value = details
                navigateTo(Screen.APPLICATION_STATUS)
            } else {
                showMessage("No application found with number '$appNumber'")
            }
        }
    }

    fun viewAdminApplicationDetail(appId: Long) {
        viewModelScope.launch {
            val details = applicationRepository.getApplicationWithDetails(appId)
            _selectedApplication.value = details
            navigateTo(Screen.ADMIN_APPLICATION_DETAIL)
        }
    }

    fun updateStatusByOfficer(
        newStatus: ApplicationStatus,
        remarks: String,
        correctionNotes: String = ""
    ) {
        val selected = _selectedApplication.value ?: return
        viewModelScope.launch {
            val res = adminRepository.updateApplicationStatus(
                appId = selected.application.id,
                appNumber = selected.application.applicationNumber,
                newStatus = newStatus,
                remarks = remarks,
                correctionNotes = correctionNotes
            )
            if (res.isSuccess) {
                // Refresh
                _selectedApplication.value = applicationRepository.getApplicationWithDetails(selected.application.id)
                showMessage("Application status updated to ${newStatus.labelEn}")
            } else {
                showMessage("Error updating status")
            }
        }
    }

    fun verifyDocumentByOfficer(document: DocumentEntity, isVerified: Boolean, remarks: String) {
        viewModelScope.launch {
            adminRepository.verifyDocument(document, isVerified, remarks)
            _selectedApplication.value?.let { current ->
                _selectedApplication.value = applicationRepository.getApplicationWithDetails(current.application.id)
            }
            showMessage("Document ${document.docType} marked ${if (isVerified) "Verified" else "Discrepancy"}")
        }
    }

    fun deleteDraft(appId: Long) {
        viewModelScope.launch {
            applicationRepository.deleteDraft(appId)
            showMessage("Draft application removed.")
        }
    }
}
