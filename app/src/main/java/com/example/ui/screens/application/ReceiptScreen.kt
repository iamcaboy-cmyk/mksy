package com.example.ui.screens.application

import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.JanTopAppBar
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalGreenLight
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalNavyDark
import com.example.ui.theme.PortalSaffron
import com.example.ui.theme.PortalSaffronLight
import com.example.util.AppLanguage
import com.example.util.SecurityUtil
import com.example.util.Strings

@Composable
fun ReceiptScreen(
    viewModel: MainViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val details by viewModel.submittedAppReceipt.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val app = details?.application
    val ben = details?.beneficiary
    val bank = details?.bankDetails

    val appNumber = app?.applicationNumber ?: "JS-2026-000000"
    val submissionDate = if (app?.submittedAt != null) SecurityUtil.formatDateTime(app.submittedAt) else "Just now"

    Scaffold(
        topBar = {
            JanTopAppBar(
                title = if (language == AppLanguage.HINDI) "आवेदन पावती रसीद" else "Acknowledgement Receipt",
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
                .background(Color(0xFFF1F5F9))
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success Header
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PortalGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = PortalGreen,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (language == AppLanguage.HINDI) "आवेदन सफलतापूर्वक जमा हुआ!" else "Application Submitted Successfully!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PortalGreen
                )
            )

            Text(
                text = "Please save or print this official acknowledgement for future reference.",
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Official Printable Acknowledgement Slip Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .testTag("acknowledgement_receipt_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Portal Receipt Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "Emblem",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "JAN SAHAYATA WELFARE PORTAL",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PortalNavy,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "Direct Benefit Transfer & Girl Child Scheme",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "GOVERNMENT ASSISTANCE INITIATIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PortalSaffron
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Application Number Highlight Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "APPLICATION TRACKING NUMBER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8)
                                )
                                Text(
                                    text = appNumber,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PortalNavy,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(appNumber))
                                viewModel.showMessage("Application Number copied to clipboard")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = PortalNavy
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Receipt Fields Table
                    ReceiptFieldRow("Submission Date & Time", submissionDate)
                    ReceiptFieldRow("Applicant Name", app?.applicantName ?: "")
                    ReceiptFieldRow("Parent / Guardian", app?.parentName ?: "")
                    ReceiptFieldRow("Beneficiary Child Name", ben?.childName ?: "")
                    ReceiptFieldRow("Beneficiary DOB", ben?.dob ?: "")
                    ReceiptFieldRow("Birth Reg Number", ben?.birthRegNumber ?: "")
                    ReceiptFieldRow("District & Block", "${app?.district ?: ""}, ${app?.block ?: ""}")
                    ReceiptFieldRow("Bank IFSC", bank?.ifscCode ?: "")
                    ReceiptFieldRow(
                        "Bank Account Number",
                        if (bank?.accountNumber != null) "XXXX-XXXX-${bank.accountNumber.takeLast(4)}" else ""
                    )
                    ReceiptFieldRow("Verification Status", "SUBMITTED (Pending Nodal Scrutiny)")

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Verification Watermark & Seal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PortalSaffronLight, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "★ OFFICIAL VERIFICATION WATERMARK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C2D12)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Expected scrutiny duration: 7 to 15 working days. Real-time SMS updates will be dispatched to ${app?.mobile ?: ""}.",
                                fontSize = 10.sp,
                                color = Color(0xFF7C2D12),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons: Share, Track Status, Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                """
                                Jan Sahayata Scheme Acknowledgement Receipt
                                --------------------------------------------
                                Application Number: $appNumber
                                Applicant: ${app?.applicantName}
                                Beneficiary: ${ben?.childName}
                                Submission Date: $submissionDate
                                Status: SUBMITTED
                                Helpline: 1800-233-5267
                                --------------------------------------------
                                Track status online at Jan Sahayata Portal
                                """.trimIndent()
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Acknowledgement Slip"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("share_receipt_button")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Share / Print", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (app != null) {
                            viewModel.viewApplicationStatus(app.id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PortalSaffron),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("track_status_from_receipt_button")
                ) {
                    Icon(imageVector = Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Track Status", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { onNavigate(Screen.DASHBOARD) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Go to Citizen Dashboard", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ReceiptFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.8f)
        )
    }
}
