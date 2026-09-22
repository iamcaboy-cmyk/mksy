package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.ApplicationStatus
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.FeedbackSubmissionDialog
import com.example.ui.components.JanTopAppBar
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.NotificationPreferencesDialog
import com.example.ui.components.RealtimeAlertBanner
import com.example.ui.components.StatusBadge
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalRed
import com.example.ui.theme.PortalSaffron
import com.example.ui.theme.PortalSaffronLight
import com.example.util.AppLanguage
import com.example.util.SecurityUtil
import com.example.util.Strings

@Composable
fun CitizenDashboardScreen(
    viewModel: MainViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val applications by viewModel.citizenApplications.collectAsState()
    val notifications by viewModel.citizenNotifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()
    val preferences by viewModel.notificationPreferences.collectAsState()
    val realtimeAlert by viewModel.realtimeAlert.collectAsState()

    val isNotificationCenterOpen by viewModel.isNotificationCenterOpen.collectAsState()
    val isNotificationPreferencesOpen by viewModel.isNotificationPreferencesOpen.collectAsState()
    val isFeedbackDialogOpen by viewModel.isFeedbackDialogOpen.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val drafts = applications.filter { it.status == ApplicationStatus.DRAFT.code }
    val submitted = applications.filter { it.status != ApplicationStatus.DRAFT.code }

    val tabs = listOf(
        Strings.get("my_applications", language) + " (${applications.size})",
        Strings.get("draft_applications", language) + " (${drafts.size})",
        Strings.get("submitted_applications", language) + " (${submitted.size})"
    )

    Scaffold(
        topBar = {
            JanTopAppBar(
                title = Strings.get("dashboard", language),
                currentLanguage = language,
                onLanguageToggle = { viewModel.toggleLanguage() },
                showBackButton = true,
                onBackClick = { onNavigate(Screen.HOME) },
                actions = {
                    // Notification Bell with Badge
                    IconButton(
                        onClick = { viewModel.isNotificationCenterOpen.value = true },
                        modifier = Modifier.testTag("dashboard_notification_bell")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = PortalSaffron,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }

                    // Notification Settings
                    IconButton(
                        onClick = { viewModel.isNotificationPreferencesOpen.value = true },
                        modifier = Modifier.testTag("dashboard_notification_prefs_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Notification Settings",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { showProfileDialog = true },
                        modifier = Modifier.testTag("dashboard_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.authRepository.logout()
                            onNavigate(Screen.HOME)
                        },
                        modifier = Modifier.testTag("dashboard_logout_button")
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
            // Real-Time Notification Alert Banner (pops down on admin status changes)
            RealtimeAlertBanner(
                notification = realtimeAlert,
                onDismiss = { viewModel.dismissRealtimeAlert() },
                onOpenNotificationCenter = {
                    viewModel.dismissRealtimeAlert()
                    viewModel.isNotificationCenterOpen.value = true
                }
            )

            // Citizen Welcome Banner
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
                            Text(
                                text = if (language == AppLanguage.HINDI) "नमस्ते," else "Welcome,",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = currentUser?.fullName ?: "Citizen",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${currentUser?.district ?: "Lucknow"}, ${currentUser?.state ?: "UP"}",
                                color = Color(0xFFFFD54F),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Masked Aadhaar Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = SecurityUtil.formatMaskedAadhaar(currentUser?.aadhaarLast4 ?: "XXXX"),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Action: New Application
                    Button(
                        onClick = {
                            viewModel.startNewApplication()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PortalSaffron),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("dashboard_new_application_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("new_application", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary Quick Actions: Notifications & Feedback
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Notifications Quick Button
                        OutlinedButton(
                            onClick = { viewModel.isNotificationCenterOpen.value = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.4f))),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("dashboard_quick_notifications_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Alerts ($unreadCount)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Feedback Quick Button
                        OutlinedButton(
                            onClick = { viewModel.openFeedbackDialog() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.4f))),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("dashboard_quick_feedback_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == AppLanguage.HINDI) "प्रतिक्रिया दें" else "Feedback",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PortalNavy,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PortalSaffron
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) PortalNavy else Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            // List of Applications according to selected tab
            val displayList = when (selectedTab) {
                1 -> drafts
                2 -> submitted
                else -> applications
            }

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (language == AppLanguage.HINDI) "कोई आवेदन नहीं मिला" else "No applications found in this section",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == AppLanguage.HINDI)
                                "योजना का लाभ लेने हेतु नया आवेदन भरें"
                            else
                                "Tap 'New Application' above to apply for scheme benefits",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayList, key = { it.id }) { app ->
                        CitizenApplicationCard(
                            app = app,
                            language = language,
                            onViewStatus = {
                                viewModel.viewApplicationStatus(app.id)
                            },
                            onEdit = {
                                viewModel.editApplication(app.id)
                            },
                            onDeleteDraft = {
                                viewModel.deleteDraft(app.id)
                            },
                            onGiveFeedback = {
                                viewModel.openFeedbackDialog(app.applicationNumber)
                            }
                        )
                    }
                }
            }
        }

        // Notification Center Dialog
        if (isNotificationCenterOpen) {
            NotificationCenterDialog(
                notifications = notifications,
                language = language,
                onMarkAsRead = { notifId -> viewModel.markNotificationAsRead(notifId) },
                onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
                onClearAll = { viewModel.clearAllNotifications() },
                onOpenPreferences = {
                    viewModel.isNotificationCenterOpen.value = false
                    viewModel.isNotificationPreferencesOpen.value = true
                },
                onDismiss = { viewModel.isNotificationCenterOpen.value = false }
            )
        }

        // Notification Preferences Dialog
        if (isNotificationPreferencesOpen) {
            NotificationPreferencesDialog(
                initialPreferences = preferences,
                language = language,
                onSave = { updated -> viewModel.saveNotificationPreferences(updated) },
                onDismiss = { viewModel.isNotificationPreferencesOpen.value = false }
            )
        }

        // Feedback Submission Dialog
        if (isFeedbackDialogOpen) {
            FeedbackSubmissionDialog(
                applicationNumber = viewModel.feedbackAppNumberContext.value,
                userEmail = currentUser?.email ?: "iamcaboy@gmail.com",
                language = language,
                onSubmit = { ease, clarity, service, category, comments, email ->
                    viewModel.submitFeedback(ease, clarity, service, category, comments, email)
                },
                onDismiss = { viewModel.closeFeedbackDialog() }
            )
        }

        // Profile Dialog
        if (showProfileDialog) {
            AlertDialog(
                onDismissRequest = { showProfileDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PortalNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("profile", language),
                            fontWeight = FontWeight.Bold,
                            color = PortalNavy
                        )
                    }
                },
                text = {
                    Column {
                        ProfileRow(label = "Full Name", value = currentUser?.fullName ?: "")
                        ProfileRow(label = "Mobile", value = "+91 ${currentUser?.mobileNumber ?: ""}")
                        ProfileRow(label = "Email", value = currentUser?.email ?: "")
                        ProfileRow(label = "Aadhaar", value = SecurityUtil.formatMaskedAadhaar(currentUser?.aadhaarLast4 ?: ""))
                        ProfileRow(label = "State", value = currentUser?.state ?: "")
                        ProfileRow(label = "District", value = currentUser?.district ?: "")
                        ProfileRow(label = "Address", value = currentUser?.address ?: "")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showProfileDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = PortalNavy)
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun CitizenApplicationCard(
    app: ApplicationEntity,
    language: AppLanguage,
    onViewStatus: () -> Unit,
    onEdit: () -> Unit,
    onDeleteDraft: () -> Unit,
    onGiveFeedback: () -> Unit
) {
    val statusEnum = ApplicationStatus.fromCode(app.status)
    val isDraft = statusEnum == ApplicationStatus.DRAFT
    val isCorrection = statusEnum == ApplicationStatus.RETURNED_FOR_CORRECTION

    Card(
        shape = RoundedCornerShape(12.dp),
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
                Text(
                    text = app.applicationNumber,
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy,
                    fontSize = 15.sp
                )
                StatusBadge(status = statusEnum, language = language)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Applicant: ${app.applicantName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )

            Text(
                text = "District: ${app.district} • Block: ${app.block}",
                fontSize = 12.sp,
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
                        text = "TXN: ${app.feeTransactionId.take(16)}...",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            if (app.submittedAt != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Submitted: ${SecurityUtil.formatDateTime(app.submittedAt)}",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            if (isCorrection && app.correctionNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PortalSaffronLight, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Officer Note: ${app.correctionNotes}",
                        color = Color(0xFF7C2D12),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Feedback affordance for non-drafts
                if (!isDraft) {
                    TextButton(onClick = onGiveFeedback) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            tint = PortalNavy,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Feedback",
                            fontSize = 11.sp,
                            color = PortalNavy,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isDraft) {
                        IconButton(onClick = onDeleteDraft) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete draft",
                                tint = PortalRed
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = onEdit,
                            colors = ButtonDefaults.buttonColors(containerColor = PortalSaffron),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.HINDI) "आवेदन जारी रखें" else "Continue Draft",
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        if (isCorrection) {
                            OutlinedButton(
                                onClick = onEdit,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PortalSaffron),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == AppLanguage.HINDI) "संशोधन करें" else "Edit & Fix",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(
                            onClick = onViewStatus,
                            colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.get("application_status", language),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 13.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
    }
}

