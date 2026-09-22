package com.example.ui.screens.status

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StatusHistoryEntity
import com.example.data.model.ApplicationStatus
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.FeedbackSubmissionDialog
import com.example.ui.components.JanTopAppBar
import com.example.ui.components.RealtimeAlertBanner
import com.example.ui.components.StatusBadge
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalGreenLight
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalRed
import com.example.ui.theme.PortalRedLight
import com.example.ui.theme.PortalSaffron
import com.example.ui.theme.PortalSaffronLight
import com.example.util.AppLanguage
import com.example.util.SecurityUtil
import com.example.util.Strings

@Composable
fun ApplicationStatusScreen(
    viewModel: MainViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val scrollState = rememberScrollState()
    val details by viewModel.selectedApplication.collectAsState()
    var searchAppNumber by remember { mutableStateOf("") }
    val realtimeAlert by viewModel.realtimeAlert.collectAsState()
    val isFeedbackDialogOpen by viewModel.isFeedbackDialogOpen.collectAsState()
    val feedbackAppNumberTarget by viewModel.feedbackAppNumberTarget.collectAsState()

    val app = details?.application
    val ben = details?.beneficiary
    val history = details?.statusHistory ?: emptyList()

    val currentStatus = if (app != null) ApplicationStatus.fromCode(app.status) else ApplicationStatus.SUBMITTED
    val canEdit = currentStatus.isEditable()

    Scaffold(
        topBar = {
            JanTopAppBar(
                title = Strings.get("application_status", language),
                currentLanguage = language,
                onLanguageToggle = { viewModel.toggleLanguage() },
                showBackButton = true,
                onBackClick = { onNavigate(Screen.DASHBOARD) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Real-Time Alert Banner (if status changed by officer)
            RealtimeAlertBanner(
                notification = realtimeAlert,
                onDismiss = { viewModel.dismissRealtimeAlert() },
                onOpenNotificationCenter = {
                    onNavigate(Screen.DASHBOARD)
                    viewModel.openNotificationCenter()
                }
            )

            // Search Another Application Bar
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchAppNumber,
                        onValueChange = { searchAppNumber = it },
                        placeholder = { Text("Track another Application #", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (searchAppNumber.isNotBlank()) {
                                viewModel.searchAndTrackApplication(searchAppNumber)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (app == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Please select or search an application to view status timeline.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                }
            } else {
                // Application Overview Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Application Number",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = app.applicationNumber,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PortalNavy
                                )
                            }
                            StatusBadge(status = currentStatus, language = language)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Applicant Name", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(text = app.applicantName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Beneficiary Child", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(text = ben?.childName ?: "—", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "District & Block", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(text = "${app.district}, ${app.block}", fontSize = 12.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Submission Date", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = if (app.submittedAt != null) SecurityUtil.formatDate(app.submittedAt) else "Draft",
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // RETURNED FOR CORRECTION ALERT & CTA
                        if (currentStatus == ApplicationStatus.RETURNED_FOR_CORRECTION) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PortalSaffronLight, RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFFDBA74), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = PortalSaffron,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Application Returned for Correction",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF7C2D12)
                                        )
                                    }
                                    if (app.correctionNotes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Officer Instructions: ${app.correctionNotes}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF7C2D12)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.startNewApplication(details)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PortalSaffron),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.testTag("edit_correction_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (language == AppLanguage.HINDI) "संशोधन करें और पुनः जमा करें" else "Edit & Re-submit Application",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // APPROVED CELEBRATION BANNER
                        if (currentStatus == ApplicationStatus.APPROVED) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PortalGreenLight, RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PortalGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Sanction Order Issued",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = PortalGreen
                                        )
                                        Text(
                                            text = "Grant approved by District Welfare Committee. Benefit scheduled for DBT transfer to bank account.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF14532D)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TIMELINE SECTION
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = PortalNavy
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.HINDI) "आवेदन की समय-सीमा (टाइमलाइन)" else "Application Status Timeline",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PortalNavy
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val lifecycleStages = listOf(
                            Triple("SUBMITTED", "Application Submitted", "Citizen filed application online via portal."),
                            Triple("UNDER_REVIEW", "Under Nodal Scrutiny", "Assigned to Block Officer for eligibility check."),
                            Triple("DOCUMENT_VERIFICATION", "Document Verification", "Verifying birth certificate & resident proofs."),
                            Triple(
                                currentStatus.code,
                                currentStatus.labelEn,
                                app.remarks.ifEmpty { "Current application stage." }
                            )
                        )

                        history.forEachIndexed { index, hist ->
                            TimelineItem(
                                status = hist.status,
                                changedBy = hist.changedBy,
                                remarks = hist.remarks,
                                timestamp = hist.timestamp,
                                isLast = index == history.size - 1
                            )
                        }

                        if (history.isEmpty()) {
                            // Fallback default timeline display
                            TimelineItem(
                                status = app.status,
                                changedBy = "System",
                                remarks = app.remarks.ifEmpty { "Application in process." },
                                timestamp = app.updatedAt,
                                isLast = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // APPLICATION PROCESSING FEE RECEIPT CARD
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Payment, contentDescription = null, tint = PortalNavy)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Statutory Processing Fee",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = PortalNavy
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "₹${app.feeAmount.toInt()}.00 ${app.feePaymentStatus} ✓",
                                    color = Color(0xFF15803D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Amount Paid", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(text = "₹280.00 (Fixed Statutory Fee)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Gateway / Mode", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(text = "Bharat UPI / NetBanking", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        if (app.feeTransactionId.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Transaction Ref (UTR):", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = app.feeTransactionId,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // CITIZEN FEEDBACK & GRIEVANCE CARD
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RateReview, contentDescription = null, tint = Color(0xFF15803D))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Citizen Experience & Feedback",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF14532D)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Help us improve public service delivery. Rate the application scrutiny process, turnaround time, and officer support.",
                            fontSize = 11.sp,
                            color = Color(0xFF166534),
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.openFeedbackDialog(app.applicationNumber)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_feedback_btn_status_screen")
                        ) {
                            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share Feedback on this Application",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        if (isFeedbackDialogOpen) {
            FeedbackSubmissionDialog(
                applicationNumber = feedbackAppNumberTarget,
                language = language,
                onDismiss = { viewModel.closeFeedbackDialog() },
                onSubmit = { easeOfUse, clarity, serviceQuality, category, comments, email ->
                    viewModel.submitFeedback(
                        easeOfUse = easeOfUse,
                        clarityOfInfo = clarity,
                        serviceQuality = serviceQuality,
                        category = category,
                        comments = comments,
                        email = email
                    )
                }
            )
        }
    }
}

@Composable
fun TimelineItem(
    status: String,
    changedBy: String,
    remarks: String,
    timestamp: Long,
    isLast: Boolean
) {
    val statusEnum = ApplicationStatus.fromCode(status)
    val dotColor = when (statusEnum) {
        ApplicationStatus.APPROVED -> PortalGreen
        ApplicationStatus.REJECTED -> PortalRed
        ApplicationStatus.RETURNED_FOR_CORRECTION -> PortalSaffron
        ApplicationStatus.DOCUMENT_VERIFICATION -> Color(0xFF7E22CE)
        ApplicationStatus.UNDER_REVIEW -> Color(0xFFD97706)
        else -> PortalNavy
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(dotColor),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(56.dp)
                        .background(Color(0xFFCBD5E1))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusEnum.labelEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = dotColor
                )
                Text(
                    text = SecurityUtil.formatDateTime(timestamp),
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            Text(
                text = "Officer / Action: $changedBy",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569)
            )
            if (remarks.isNotBlank()) {
                Text(
                    text = remarks,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
