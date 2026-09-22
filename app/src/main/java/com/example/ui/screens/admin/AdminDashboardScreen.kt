package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entity.ApplicationEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.FeedbackEntity
import com.example.data.model.ApplicationStatus
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.JanTopAppBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalNavyDark
import com.example.ui.theme.PortalSaffron
import com.example.ui.theme.PortalSaffronLight
import com.example.util.AppLanguage
import com.example.util.SecurityUtil
import com.example.util.Strings

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val currentAdmin by viewModel.currentAdmin.collectAsState()
    val allApplications by viewModel.allAdminApplications.collectAsState()
    val auditLogs by viewModel.adminAuditLogs.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Applications, 1: Audit Logs
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<String?>("ALL") }
    var selectedDistrictFilter by remember { mutableStateOf<String?>("ALL") }

    val districts = listOf("ALL", "Lucknow", "Kanpur", "Varanasi", "Prayagraj", "Agra")
    val statuses = listOf(
        "ALL",
        ApplicationStatus.SUBMITTED.code,
        ApplicationStatus.UNDER_REVIEW.code,
        ApplicationStatus.DOCUMENT_VERIFICATION.code,
        ApplicationStatus.APPROVED.code,
        ApplicationStatus.REJECTED.code,
        ApplicationStatus.RETURNED_FOR_CORRECTION.code
    )

    // Filter applications
    val filteredApplications = allApplications.filter { app ->
        val matchesSearch = searchQuery.isBlank() ||
                app.applicationNumber.contains(searchQuery, ignoreCase = true) ||
                app.applicantName.contains(searchQuery, ignoreCase = true) ||
                app.mobile.contains(searchQuery)

        val matchesStatus = selectedStatusFilter == "ALL" || app.status == selectedStatusFilter
        val matchesDistrict = selectedDistrictFilter == "ALL" || app.district.equals(selectedDistrictFilter, ignoreCase = true)

        matchesSearch && matchesStatus && matchesDistrict
    }

    Scaffold(
        topBar = {
            JanTopAppBar(
                title = "Officer Scrutiny Portal",
                currentLanguage = language,
                onLanguageToggle = { viewModel.toggleLanguage() },
                showBackButton = true,
                onBackClick = { onNavigate(Screen.HOME) },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.adminRepository.adminLogout()
                            onNavigate(Screen.HOME)
                        },
                        modifier = Modifier.testTag("admin_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            // Officer Header Banner
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = PortalNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentAdmin?.department ?: "Social Welfare Department",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Officer: ${currentAdmin?.fullName ?: "Nodal Officer"}",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Jurisdiction: ${currentAdmin?.district ?: "Lucknow"} District • Role: ${currentAdmin?.role ?: "OFFICER"}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }

                        // Total Applications Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${allApplications.size}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Total Files",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            // Top Navigation Tabs: Applications vs Feedback vs Audit Logs
            val allFeedbacks by viewModel.allFeedbacks.collectAsState()
            val avgRating by viewModel.averageFeedbackRating.collectAsState()

            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.White,
                contentColor = PortalNavy,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = PortalSaffron
                    )
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Text(
                            text = "Applications (${filteredApplications.size})",
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == 0) PortalNavy else Color(0xFF64748B)
                        )
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Text(
                            text = "Citizen Feedback (${allFeedbacks.size})",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == 1) PortalNavy else Color(0xFF64748B)
                        )
                    }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = {
                        Text(
                            text = "Audit Logs (${auditLogs.size})",
                            fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == 2) PortalNavy else Color(0xFF64748B)
                        )
                    }
                )
            }

            if (activeTab == 0) {
                // APPLICATIONS VIEW
                Column(modifier = Modifier.fillMaxSize()) {
                    // Search & Filter controls
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by App #, Citizen Name or Mobile", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PortalNavy) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("admin_search_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            statuses.forEach { status ->
                                val label = if (status == "ALL") "All Status" else ApplicationStatus.fromCode(status).labelEn
                                FilterChip(
                                    selected = selectedStatusFilter == status,
                                    onClick = { selectedStatusFilter = status },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PortalNavy,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // District Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            districts.forEach { dist ->
                                FilterChip(
                                    selected = selectedDistrictFilter == dist,
                                    onClick = { selectedDistrictFilter = dist },
                                    label = { Text(if (dist == "ALL") "All Districts" else dist, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PortalSaffron,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Applications List
                    if (filteredApplications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No applications matching the search / filter criteria.",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredApplications, key = { it.id }) { app ->
                                AdminApplicationRowCard(
                                    app = app,
                                    language = language,
                                    onScrutinize = {
                                        viewModel.viewAdminApplicationDetail(app.id)
                                    }
                                )
                            }
                        }
                    }
                }
            } else if (activeTab == 1) {
                // CITIZEN FEEDBACK VIEW
                var feedbackCategoryFilter by remember { mutableStateOf("ALL") }
                val feedbackCategories = listOf("ALL", "Ease of Use", "Clarity of Information", "Service Quality", "General Comments", "Payment & Fees")

                val filteredFeedbacks = if (feedbackCategoryFilter == "ALL") {
                    allFeedbacks
                } else {
                    allFeedbacks.filter { it.category.equals(feedbackCategoryFilter, ignoreCase = true) }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Feedback Analytics Summary Header
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Citizen Satisfaction Index",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PortalNavy
                                    )
                                    Text(
                                        text = "Aggregated citizen feedback & service metrics",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "⭐ ${String.format("%.1f", avgRating)} / 5.0",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309),
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Category Filter Chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                feedbackCategories.forEach { category ->
                                    FilterChip(
                                        selected = feedbackCategoryFilter == category,
                                        onClick = { feedbackCategoryFilter = category },
                                        label = { Text(category, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PortalNavy,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (filteredFeedbacks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No citizen feedback found for this category.",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredFeedbacks, key = { it.id }) { fb ->
                                AdminFeedbackRowCard(
                                    feedback = fb,
                                    onRecordResponse = { remarks ->
                                        viewModel.updateAdminFeedbackReview(fb.id, remarks)
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // AUDIT LOGS VIEW
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(auditLogs, key = { it.id }) { log ->
                        AuditLogRowCard(log)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminApplicationRowCard(
    app: ApplicationEntity,
    language: AppLanguage,
    onScrutinize: () -> Unit
) {
    val statusEnum = ApplicationStatus.fromCode(app.status)

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = app.applicationNumber,
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy,
                    fontSize = 14.sp
                )
                StatusBadge(status = statusEnum, language = language)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Applicant: ${app.applicantName} • Mobile: ${app.mobile}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )

            Text(
                text = "Location: ${app.district}, Block: ${app.block}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            // Fee info pill
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (app.feePaymentStatus == "PAID") Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Fee: ₹${app.feeAmount.toInt()} (${app.feePaymentStatus})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (app.feePaymentStatus == "PAID") Color(0xFF15803D) else Color(0xFFB45309)
                    )
                }
                if (app.feeTransactionId.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "UTR: ${app.feeTransactionId.take(14)}...",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            if (app.submittedAt != null) {
                Text(
                    text = "Submitted: ${SecurityUtil.formatDateTime(app.submittedAt)}",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            if (app.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Latest Remarks: ${app.remarks}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onScrutinize,
                    colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "Scrutinize / Take Action", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AuditLogRowCard(log: AuditLogEntity) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = log.action,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PortalNavy
                )
                Text(
                    text = SecurityUtil.formatDateTime(log.timestamp),
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "File: ${log.applicationNumber} • Performed by: ${log.performedBy}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = log.details,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun AdminFeedbackRowCard(
    feedback: FeedbackEntity,
    onRecordResponse: (String) -> Unit
) {
    var isReplying by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = feedback.userName,
                        fontWeight = FontWeight.Bold,
                        color = PortalNavy,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${feedback.userEmail} • +91 ${feedback.userMobile}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PortalNavy.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = feedback.category,
                        color = PortalNavy,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (feedback.applicationNumber.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ref Application: ${feedback.applicationNumber}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PortalSaffron
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Star Ratings Breakdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RatingPill("Ease of Use", feedback.easeOfUseRating)
                RatingPill("Clarity", feedback.clarityOfInfoRating)
                RatingPill("Service Quality", feedback.serviceQualityRating)
            }

            if (feedback.comments.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${feedback.comments}\"",
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Submitted: ${SecurityUtil.formatDateTime(feedback.createdAt)}",
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )

            // Officer Review / Response Section
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            if (feedback.isReviewedByAdmin) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFECFDF5), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "✓ Officer Acknowledged",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                        if (!feedback.adminRemarks.isNullOrBlank()) {
                            Text(
                                text = "Remarks: ${feedback.adminRemarks}",
                                fontSize = 11.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }
            } else {
                if (isReplying) {
                    Column {
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            placeholder = { Text("Enter official redressal remarks...", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { isReplying = false }) {
                                Text("Cancel", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    onRecordResponse(replyText.ifBlank { "Acknowledged by Department." })
                                    isReplying = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Save Response", fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status: Pending Officer Review",
                            fontSize = 11.sp,
                            color = Color(0xFFB45309),
                            fontWeight = FontWeight.Medium
                        )
                        OutlinedButton(
                            onClick = { isReplying = true },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Official Reply", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingPill(label: String, rating: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$rating", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PortalNavy)
            Icon(Icons.Default.Star, contentDescription = null, tint = PortalGold, modifier = Modifier.size(12.dp))
        }
    }
}
