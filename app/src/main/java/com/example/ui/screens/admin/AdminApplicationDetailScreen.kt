package com.example.ui.screens.admin

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DocumentEntity
import com.example.data.model.ApplicationStatus
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.JanTopAppBar
import com.example.ui.components.StatusBadge
import com.example.ui.screens.status.TimelineItem
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApplicationDetailScreen(
    viewModel: MainViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val scrollState = rememberScrollState()
    val details by viewModel.selectedApplication.collectAsState()

    val app = details?.application
    val ben = details?.beneficiary
    val bank = details?.bankDetails
    val documents = details?.documents ?: emptyList()
    val history = details?.statusHistory ?: emptyList()

    var showStatusUpdateDialog by remember { mutableStateOf(false) }
    var selectedNewStatus by remember { mutableStateOf(ApplicationStatus.UNDER_REVIEW) }
    var officerRemarks by remember { mutableStateOf("") }
    var correctionNotes by remember { mutableStateOf("") }
    var statusDialogError by remember { mutableStateOf<String?>(null) }
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            JanTopAppBar(
                title = "Application Scrutiny",
                currentLanguage = language,
                onLanguageToggle = { viewModel.toggleLanguage() },
                showBackButton = true,
                onBackClick = { onNavigate(Screen.ADMIN_DASHBOARD) }
            )
        },
        bottomBar = {
            Card(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigate(Screen.ADMIN_DASHBOARD) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back to Queue")
                    }

                    Button(
                        onClick = {
                            if (app != null) {
                                selectedNewStatus = ApplicationStatus.fromCode(app.status)
                                officerRemarks = ""
                                correctionNotes = app.correctionNotes
                                statusDialogError = null
                                showStatusUpdateDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PortalSaffron),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("open_status_action_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change Status / Remarks", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
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
            if (app == null) {
                Text("No application selected.")
            } else {
                val currentStatus = ApplicationStatus.fromCode(app.status)

                // Header Card
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
                                    text = app.applicationNumber,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PortalNavy
                                )
                                Text(
                                    text = "Submitted: ${if (app.submittedAt != null) SecurityUtil.formatDateTime(app.submittedAt) else "Draft"}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            StatusBadge(status = currentStatus, language = language)
                        }

                        if (app.remarks.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Officer Remarks: ${app.remarks}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF334155)
                                )
                            }
                        }

                        if (app.correctionNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PortalSaffronLight, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Citizen Correction Request: ${app.correctionNotes}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF7C2D12)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SECTION 1: APPLICANT PARTICULARS
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "1. Applicant Particulars",
                            fontWeight = FontWeight.Bold,
                            color = PortalNavy,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailItem("Full Name", app.applicantName)
                        DetailItem("Parent / Spouse", app.parentName)
                        DetailItem("Date of Birth & Gender", "${app.dob} • ${app.gender}")
                        DetailItem("Category", app.category)
                        DetailItem("Mobile", "+91 ${app.mobile}")
                        DetailItem("Address", app.address)
                        DetailItem("District & Block", "${app.district} • ${app.block} (PIN: ${app.pinCode})")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 2: BENEFICIARY DETAILS
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "2. Beneficiary / Girl Child Particulars",
                            fontWeight = FontWeight.Bold,
                            color = PortalNavy,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailItem("Child Name", ben?.childName ?: "—")
                        DetailItem("DOB & Gender", "${ben?.dob ?: "—"} • ${ben?.gender ?: "—"}")
                        DetailItem("Birth Reg Number", ben?.birthRegNumber ?: "—")
                        DetailItem("Relationship", ben?.relationship ?: "—")
                        DetailItem("Schooling", ben?.schoolDetails ?: "—")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 3: BANK DETAILS
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "3. DBT Bank Account Verification",
                            fontWeight = FontWeight.Bold,
                            color = PortalNavy,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailItem("Account Holder", bank?.accountHolderName ?: "—")
                        DetailItem("Bank & Branch", "${bank?.bankName ?: "—"} (${bank?.branchName ?: "—"})")
                        DetailItem("Account Number", bank?.accountNumber ?: "—")
                        DetailItem("IFSC Code", bank?.ifscCode ?: "—")
                        DetailItem("NPCI Verification", "VERIFIED ✓ (Account Active for DBT)")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 4: APPLICATION PROCESSING FEE & E-CHALLAN
                Card(
                    shape = RoundedCornerShape(12.dp),
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
                                text = "4. Application Processing Fee",
                                fontWeight = FontWeight.Bold,
                                color = PortalNavy,
                                fontSize = 13.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (app.feePaymentStatus == "PAID") Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = app.feePaymentStatus,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (app.feePaymentStatus == "PAID") Color(0xFF15803D) else Color(0xFFB45309)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailItem("Prescribed Fee Amount", "₹${app.feeAmount.toInt()}.00")
                        DetailItem("E-Challan / UTR Ref", if (app.feeTransactionId.isNotBlank()) app.feeTransactionId else "None")
                        if (app.feePaidAt != null) {
                            DetailItem("Payment Timestamp", SecurityUtil.formatDateTime(app.feePaidAt))
                        }
                        DetailItem("Settlement Channel", "Bharat UPI / NPCI Gateway (Recipient: iamcaboy@gmail.com)")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 5: DOCUMENTS & OFFICER VERIFICATION
                Card(
                    shape = RoundedCornerShape(12.dp),
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
                                text = "4. Supporting Documents Scrutiny",
                                fontWeight = FontWeight.Bold,
                                color = PortalNavy,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${documents.size} Uploaded",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        documents.forEach { doc ->
                            OfficerDocumentRow(
                                doc = doc,
                                onVerify = { isVerified, remarks ->
                                    viewModel.verifyDocumentByOfficer(doc, isVerified, remarks)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 5: AUDIT TRAIL / TIMELINE
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "5. Status History & Audit Trail",
                            fontWeight = FontWeight.Bold,
                            color = PortalNavy,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        history.forEachIndexed { index, hist ->
                            TimelineItem(
                                status = hist.status,
                                changedBy = hist.changedBy,
                                remarks = hist.remarks,
                                timestamp = hist.timestamp,
                                isLast = index == history.size - 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Change Status Dialog
        if (showStatusUpdateDialog) {
            val availableStatuses = listOf(
                ApplicationStatus.UNDER_REVIEW,
                ApplicationStatus.DOCUMENT_VERIFICATION,
                ApplicationStatus.APPROVED,
                ApplicationStatus.REJECTED,
                ApplicationStatus.RETURNED_FOR_CORRECTION
            )

            AlertDialog(
                onDismissRequest = { showStatusUpdateDialog = false },
                title = {
                    Text(
                        text = "Update Application Scrutiny Status",
                        fontWeight = FontWeight.Bold,
                        color = PortalNavy,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "File: ${app?.applicationNumber ?: ""}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Dropdown
                        ExposedDropdownMenuBox(
                            expanded = statusMenuExpanded,
                            onExpandedChange = { statusMenuExpanded = !statusMenuExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedNewStatus.labelEn,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select New Status *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusMenuExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = statusMenuExpanded,
                                onDismissRequest = { statusMenuExpanded = false }
                            ) {
                                availableStatuses.forEach { statusOption ->
                                    DropdownMenuItem(
                                        text = { Text(statusOption.labelEn) },
                                        onClick = {
                                            selectedNewStatus = statusOption
                                            statusMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Officer Remarks
                        OutlinedTextField(
                            value = officerRemarks,
                            onValueChange = { officerRemarks = it },
                            label = { Text("Officer Internal Remarks *") },
                            placeholder = { Text("e.g. Domicile verified against state revenue records.") },
                            maxLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("officer_remarks_input")
                        )

                        // Correction Notes (Mandatory if RETURNED_FOR_CORRECTION)
                        if (selectedNewStatus == ApplicationStatus.RETURNED_FOR_CORRECTION) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = correctionNotes,
                                onValueChange = { correctionNotes = it },
                                label = { Text("Citizen Correction Notes (Visible to Citizen) *") },
                                placeholder = { Text("e.g. Birth certificate image is blurry. Please re-upload.") },
                                maxLines = 2,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("correction_notes_input")
                            )
                        }

                        // Notice on automated citizen notification
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "⚡ Real-Time Alert: Citizen will immediately receive an In-App banner, SMS simulation, and email notification with your remarks.",
                                fontSize = 10.sp,
                                color = PortalNavy,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (statusDialogError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = statusDialogError ?: "",
                                color = PortalRed,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (officerRemarks.isBlank()) {
                                statusDialogError = "Please enter officer scrutiny remarks."
                                return@Button
                            }
                            if (selectedNewStatus == ApplicationStatus.RETURNED_FOR_CORRECTION && correctionNotes.isBlank()) {
                                statusDialogError = "Please provide specific instructions in the Citizen Correction field."
                                return@Button
                            }

                            viewModel.updateStatusByOfficer(
                                newStatus = selectedNewStatus,
                                remarks = officerRemarks,
                                correctionNotes = correctionNotes
                            )
                            showStatusUpdateDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                        modifier = Modifier.testTag("confirm_status_update_btn")
                    ) {
                        Text("Apply Status Change")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStatusUpdateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
    }
}

@Composable
fun OfficerDocumentRow(
    doc: DocumentEntity,
    onVerify: (Boolean, String) -> Unit
) {
    val isVerified = doc.verificationStatus == "VERIFIED"
    val isDiscrepancy = doc.verificationStatus == "DISCREPANCY"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doc.docType,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PortalNavy
                    )
                    Text(
                        text = "${doc.fileName} (${doc.fileSize})",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Verification Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (doc.verificationStatus) {
                                "VERIFIED" -> PortalGreenLight
                                "DISCREPANCY" -> PortalRedLight
                                else -> PortalSaffronLight
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = doc.verificationStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (doc.verificationStatus) {
                            "VERIFIED" -> PortalGreen
                            "DISCREPANCY" -> PortalRed
                            else -> PortalSaffron
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Officer action buttons for this document
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onVerify(false, "Discrepancy identified in document") },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = PortalRed, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Discrepancy", fontSize = 10.sp, color = PortalRed)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onVerify(true, "Verified successfully against records") },
                    colors = ButtonDefaults.buttonColors(containerColor = PortalGreen),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verify ✓", fontSize = 10.sp)
                }
            }
        }
    }
}
