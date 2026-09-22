package com.example.ui.screens.application

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DocumentEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.JanTopAppBar
import com.example.ui.components.StepperHeader
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalGreenLight
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalRed
import com.example.ui.theme.PortalSaffron
import com.example.ui.theme.PortalSaffronLight
import com.example.util.AppLanguage
import com.example.util.SecurityUtil
import com.example.util.Strings

@Composable
fun NewApplicationWizardScreen(
    viewModel: MainViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val scrollState = rememberScrollState()
    val currentStep by viewModel.wizardCurrentStep.collectAsState()

    var stepError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            JanTopAppBar(
                title = Strings.get("new_application", language),
                currentLanguage = language,
                onLanguageToggle = { viewModel.toggleLanguage() },
                showBackButton = true,
                onBackClick = {
                    if (currentStep > 1) {
                        viewModel.wizardCurrentStep.value -= 1
                    } else {
                        onNavigate(Screen.DASHBOARD)
                    }
                }
            )
        },
        bottomBar = {
            // Stepper Bottom Navigation Bar
            Card(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back or Save Draft
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { viewModel.wizardCurrentStep.value -= 1 },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == AppLanguage.HINDI) "पिछला" else "Back")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.submitOrDraftApplication(isDraft = true) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("save_draft_button")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = "Draft")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == AppLanguage.HINDI) "ड्राफ्ट सहेजें" else "Save Draft")
                        }
                    }

                    // Save Draft Shortcut on middle steps
                    if (currentStep in 2..6) {
                        TextButton(onClick = { viewModel.submitOrDraftApplication(isDraft = true) }) {
                            Text(
                                text = if (language == AppLanguage.HINDI) "ड्राफ्ट" else "Draft",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Next or Submit
                    if (currentStep < 7) {
                        Button(
                            onClick = {
                                stepError = null
                                val valid = validateCurrentStep(currentStep, viewModel)
                                if (valid) {
                                    viewModel.wizardCurrentStep.value += 1
                                } else {
                                    stepError = getStepValidationError(currentStep, viewModel)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("step_next_button")
                        ) {
                            Text(if (language == AppLanguage.HINDI) "अगला" else "Next")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next"
                            )
                        }
                    } else {
                        // Final Submit
                        val feePaid by viewModel.wizardFeePaid.collectAsState()
                        Button(
                            onClick = {
                                if (feePaid) {
                                    viewModel.submitOrDraftApplication(isDraft = false)
                                } else {
                                    stepError = "Please complete the ₹280 application processing fee payment via UPI QR before submitting."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (feePaid) PortalSaffron else Color(0xFF94A3B8)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("final_submit_application_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Submit")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.HINDI) "आवेदन अंतिम रूप से जमा करें (₹280 भुगतान सहित)" else "Submit Application (₹280 Paid)",
                                fontWeight = FontWeight.Bold
                            )
                        }
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
        ) {
            StepperHeader(
                currentStep = currentStep,
                totalSteps = 7,
                language = language
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                if (stepError != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFEE2E2), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stepError ?: "",
                            color = Color(0xFF991B1B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                when (currentStep) {
                    1 -> StepApplicantDetails(viewModel, language)
                    2 -> StepBeneficiaryDetails(viewModel, language)
                    3 -> StepBankDetails(viewModel, language)
                    4 -> StepEligibilityCheck(viewModel, language)
                    5 -> StepDocumentsUpload(viewModel, language)
                    6 -> StepReviewAndDeclaration(viewModel, language) { stepToJump ->
                        viewModel.wizardCurrentStep.value = stepToJump
                    }
                    7 -> StepSubmitConfirmation(viewModel, language)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// STEP 1: Applicant Details
@Composable
fun StepApplicantDetails(viewModel: MainViewModel, language: AppLanguage) {
    val name by viewModel.applicantName.collectAsState()
    val parent by viewModel.parentName.collectAsState()
    val dob by viewModel.applicantDob.collectAsState()
    val gender by viewModel.applicantGender.collectAsState()
    val category by viewModel.applicantCategory.collectAsState()
    val mobile by viewModel.applicantMobile.collectAsState()
    val address by viewModel.applicantAddress.collectAsState()
    val district by viewModel.applicantDistrict.collectAsState()
    val block by viewModel.applicantBlock.collectAsState()
    val pin by viewModel.applicantPinCode.collectAsState()

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (language == AppLanguage.HINDI) "चरण 1: आवेदक का व्यक्तिगत विवरण" else "Step 1: Applicant Details",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.applicantName.value = it },
                label = { Text("Applicant Full Name *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("applicant_name_field")
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = parent,
                onValueChange = { viewModel.parentName.value = it },
                label = { Text("Father's / Mother's / Guardian's Name *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("applicant_parent_field")
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dob,
                    onValueChange = { viewModel.applicantDob.value = it },
                    label = { Text("Date of Birth (DD/MM/YYYY)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = gender,
                    onValueChange = { viewModel.applicantGender.value = it },
                    label = { Text("Gender") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { viewModel.applicantCategory.value = it },
                    label = { Text("Category (OBC/SC/ST/Gen)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { if (it.length <= 10) viewModel.applicantMobile.value = it.filter { c -> c.isDigit() } },
                    label = { Text("Mobile Number *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { viewModel.applicantAddress.value = it },
                label = { Text("Complete Residential Address *") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = district,
                    onValueChange = { viewModel.applicantDistrict.value = it },
                    label = { Text("District *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = block,
                    onValueChange = { viewModel.applicantBlock.value = it },
                    label = { Text("Block / Tehsil *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 6) viewModel.applicantPinCode.value = it.filter { c -> c.isDigit() } },
                label = { Text("PIN Code (6 digits) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// STEP 2: Beneficiary Details
@Composable
fun StepBeneficiaryDetails(viewModel: MainViewModel, language: AppLanguage) {
    val childName by viewModel.beneficiaryName.collectAsState()
    val childDob by viewModel.beneficiaryDob.collectAsState()
    val gender by viewModel.beneficiaryGender.collectAsState()
    val birthReg by viewModel.birthRegNumber.collectAsState()
    val rel by viewModel.relationship.collectAsState()
    val school by viewModel.schoolDetails.collectAsState()

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (language == AppLanguage.HINDI) "चरण 2: लाभार्थी / बालिका का विवरण" else "Step 2: Beneficiary / Girl Child Details",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = childName,
                onValueChange = { viewModel.beneficiaryName.value = it },
                label = { Text("Beneficiary / Child Full Name *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("beneficiary_name_field")
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = childDob,
                    onValueChange = { viewModel.beneficiaryDob.value = it },
                    label = { Text("Date of Birth *") },
                    placeholder = { Text("DD/MM/YYYY") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = gender,
                    onValueChange = { viewModel.beneficiaryGender.value = it },
                    label = { Text("Gender *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = birthReg,
                onValueChange = { viewModel.birthRegNumber.value = it },
                label = { Text("Birth Registration Certificate Number *") },
                placeholder = { Text("e.g. BR-UP-2021-98421") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("beneficiary_birth_reg_field")
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = rel,
                onValueChange = { viewModel.relationship.value = it },
                label = { Text("Relationship with Applicant (Daughter/Ward) *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = school,
                onValueChange = { viewModel.schoolDetails.value = it },
                label = { Text("School / Educational Institution Details") },
                placeholder = { Text("e.g. Govt Primary School Rampur, Class 3") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// STEP 3: Bank Details
@Composable
fun StepBankDetails(viewModel: MainViewModel, language: AppLanguage) {
    val accHolder by viewModel.accountHolderName.collectAsState()
    val bank by viewModel.bankName.collectAsState()
    val branch by viewModel.branchName.collectAsState()
    val accNum by viewModel.accountNumber.collectAsState()
    val confirmAccNum by viewModel.confirmAccountNumber.collectAsState()
    val ifsc by viewModel.ifscCode.collectAsState()
    val verified by viewModel.isBankVerified.collectAsState()

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (language == AppLanguage.HINDI) "चरण 3: प्रत्यक्ष बैंक अंतरण (DBT) विवरण" else "Step 3: Direct Benefit Transfer (DBT) Bank Details",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ensure account is active & seeded with Aadhaar for seamless DBT credit.",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = accHolder,
                onValueChange = { viewModel.accountHolderName.value = it },
                label = { Text("Account Holder Name (Beneficiary or Mother) *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bank_holder_field")
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bank,
                    onValueChange = { viewModel.bankName.value = it },
                    label = { Text("Bank Name *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = branch,
                    onValueChange = { viewModel.branchName.value = it },
                    label = { Text("Branch *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = accNum,
                onValueChange = { viewModel.accountNumber.value = it.filter { c -> c.isDigit() } },
                label = { Text("Savings Bank Account Number *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bank_acc_num_field")
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmAccNum,
                onValueChange = { viewModel.confirmAccountNumber.value = it.filter { c -> c.isDigit() } },
                label = { Text("Confirm Bank Account Number *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = ifsc,
                onValueChange = { viewModel.ifscCode.value = it.uppercase() },
                label = { Text("IFSC Code (11 characters) *") },
                placeholder = { Text("e.g. SBIN0001234") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bank_ifsc_field")
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Bank Verification Badge
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
                        contentDescription = "Verified",
                        tint = PortalGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Account Verification Status: VERIFIED ✓",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PortalGreen
                        )
                        Text(
                            text = "Simulated NPCI Penny-Drop: Active savings account eligible for DBT transfer.",
                            fontSize = 11.sp,
                            color = Color(0xFF14532D)
                        )
                    }
                }
            }
        }
    }
}

// STEP 4: Eligibility Check
@Composable
fun StepEligibilityCheck(viewModel: MainViewModel, language: AppLanguage) {
    val q1 by viewModel.eligIncomeBelowLimit.collectAsState()
    val q2 by viewModel.eligStateResident.collectAsState()
    val q3 by viewModel.eligAgeValid.collectAsState()
    val q4 by viewModel.eligMaxTwoChildren.collectAsState()
    val q5 by viewModel.eligNonIncomeTaxPayer.collectAsState()

    val isEligible = q1 && q2 && q3 && q4 && q5

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (language == AppLanguage.HINDI) "चरण 4: पात्रता स्व-मूल्यांकन" else "Step 4: Scheme Eligibility Check",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            EligibilityQuestionRow(
                question = "1. Is the total annual family income from all sources less than ₹2,50,000?",
                answer = q1,
                onAnswerChange = { viewModel.eligIncomeBelowLimit.value = it }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9))

            EligibilityQuestionRow(
                question = "2. Is the beneficiary or parent a permanent resident of this State?",
                answer = q2,
                onAnswerChange = { viewModel.eligStateResident.value = it }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9))

            EligibilityQuestionRow(
                question = "3. Is the beneficiary child age within the scheme criteria (0 to 18 years)?",
                answer = q3,
                onAnswerChange = { viewModel.eligAgeValid.value = it }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9))

            EligibilityQuestionRow(
                question = "4. Does the family have at most two living children seeking this scheme?",
                answer = q4,
                onAnswerChange = { viewModel.eligMaxTwoChildren.value = it }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9))

            EligibilityQuestionRow(
                question = "5. Confirm that neither parent is an Income Tax payer?",
                answer = q5,
                onAnswerChange = { viewModel.eligNonIncomeTaxPayer.value = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculated Eligibility Result Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isEligible) PortalGreenLight else Color(0xFFFEE2E2),
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        if (isEligible) Color(0xFF86EFAC) else Color(0xFFFCA5A5),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isEligible) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isEligible) PortalGreen else PortalRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEligible)
                                "Preliminary Eligibility Check: PASSED / ELIGIBLE"
                            else
                                "Preliminary Eligibility Check: INELIGIBLE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isEligible) PortalGreen else PortalRed
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // MANDATORY DISCLAIMER PER PROMPT
                    Text(
                        text = "Preliminary eligibility check – final decision belongs to the concerned authority.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isEligible) Color(0xFF14532D) else Color(0xFF991B1B)
                    )
                }
            }
        }
    }
}

@Composable
fun EligibilityQuestionRow(
    question: String,
    answer: Boolean,
    onAnswerChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(
            text = question,
            fontSize = 13.sp,
            color = Color(0xFF1E293B),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAnswerChange(true) }
            ) {
                RadioButton(
                    selected = answer,
                    onClick = { onAnswerChange(true) },
                    colors = RadioButtonDefaults.colors(selectedColor = PortalNavy)
                )
                Text(text = "Yes", fontSize = 13.sp)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAnswerChange(false) }
            ) {
                RadioButton(
                    selected = !answer,
                    onClick = { onAnswerChange(false) },
                    colors = RadioButtonDefaults.colors(selectedColor = PortalNavy)
                )
                Text(text = "No", fontSize = 13.sp)
            }
        }
    }
}

// STEP 5: Documents Upload
@Composable
fun StepDocumentsUpload(viewModel: MainViewModel, language: AppLanguage) {
    val documents by viewModel.uploadedDocuments.collectAsState()

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (language == AppLanguage.HINDI) "चरण 5: आवश्यक दस्तावेज अपलोड" else "Step 5: Document Uploads",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Supported formats: PDF, JPG, PNG (Max size: 2MB per file).",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(14.dp))

            val requiredDocs = listOf(
                Pair("IDENTITY", "1. Identity Document (Aadhaar/Voter ID)"),
                Pair("BIRTH_CERT", "2. Child Birth Certificate"),
                Pair("ADDRESS", "3. Address Proof (Domicile/Electricity)"),
                Pair("BANK_PASSBOOK", "4. Bank Passbook Front Page / Cheque"),
                Pair("PHOTO", "5. Photograph of Child / Beneficiary"),
                Pair("INCOME_CERT", "6. Other Supporting / Income Certificate")
            )

            requiredDocs.forEach { (type, label) ->
                val existingDoc = documents.firstOrNull { it.docType == type }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PortalNavy
                            )
                            if (existingDoc != null) {
                                Text(
                                    text = "Attached: ${existingDoc.fileName} (${existingDoc.fileSize})",
                                    fontSize = 11.sp,
                                    color = PortalGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "Status: Not uploaded",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        if (existingDoc != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Uploaded",
                                    tint = PortalGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        viewModel.uploadedDocuments.value =
                                            documents.filterNot { it.docType == type }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = PortalRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    // Add sample valid document
                                    val newDoc = DocumentEntity(
                                        id = 0,
                                        applicationId = 0,
                                        docType = type,
                                        fileName = "${type.lowercase()}_doc_${System.currentTimeMillis() % 1000}.pdf",
                                        fileSize = "512 KB",
                                        fileFormat = "PDF",
                                        uploadedAt = System.currentTimeMillis(),
                                        verificationStatus = "PENDING"
                                    )
                                    viewModel.uploadedDocuments.value = documents + newDoc
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Upload", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// STEP 6: Review & Declaration
@Composable
fun StepReviewAndDeclaration(
    viewModel: MainViewModel,
    language: AppLanguage,
    onJumpToStep: (Int) -> Unit
) {
    val applicantName by viewModel.applicantName.collectAsState()
    val parent by viewModel.parentName.collectAsState()
    val district by viewModel.applicantDistrict.collectAsState()
    val block by viewModel.applicantBlock.collectAsState()
    val childName by viewModel.beneficiaryName.collectAsState()
    val childDob by viewModel.beneficiaryDob.collectAsState()
    val bank by viewModel.bankName.collectAsState()
    val accNum by viewModel.accountNumber.collectAsState()
    val ifsc by viewModel.ifscCode.collectAsState()
    val docs by viewModel.uploadedDocuments.collectAsState()
    val decAccepted by viewModel.declarationAccepted.collectAsState()
    val consentAuth by viewModel.consentAuthorized.collectAsState()

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (language == AppLanguage.HINDI) "चरण 6: पूर्ण आवेदन समीक्षा एवं घोषणा" else "Step 6: Review & Applicant Declaration",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Please verify all entered particulars before submitting.",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Section 1 Preview: Applicant
            ReviewSectionCard(
                title = "Applicant Details",
                stepIndex = 1,
                onEdit = { onJumpToStep(1) }
            ) {
                Text(text = "Name: $applicantName", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Parent/Guardian: $parent", fontSize = 12.sp)
                Text(text = "District / Block: $district / $block", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Section 2 Preview: Beneficiary
            ReviewSectionCard(
                title = "Beneficiary / Girl Child Details",
                stepIndex = 2,
                onEdit = { onJumpToStep(2) }
            ) {
                Text(text = "Child Name: $childName", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Date of Birth: $childDob", fontSize = 12.sp)
                Text(text = "Birth Reg: ${viewModel.birthRegNumber.value}", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Section 3 Preview: Bank
            ReviewSectionCard(
                title = "Bank Account Details",
                stepIndex = 3,
                onEdit = { onJumpToStep(3) }
            ) {
                Text(text = "Bank: $bank", fontSize = 12.sp)
                Text(text = "Account: $accNum", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "IFSC: $ifsc", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Section 4 Preview: Documents
            ReviewSectionCard(
                title = "Uploaded Documents (${docs.size} files)",
                stepIndex = 5,
                onEdit = { onJumpToStep(5) }
            ) {
                docs.forEach { doc ->
                    Text(text = "• ${doc.docType}: ${doc.fileName} (${doc.fileSize})", fontSize = 11.sp, color = PortalNavy)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Declaration Checkboxes
            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = decAccepted,
                    onCheckedChange = { viewModel.declarationAccepted.value = it },
                    colors = CheckboxDefaults.colors(checkedColor = PortalNavy),
                    modifier = Modifier.testTag("declaration_checkbox")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "I solemnly affirm and declare that the statements made above are true and complete to the best of my knowledge.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = consentAuth,
                    onCheckedChange = { viewModel.consentAuthorized.value = it },
                    colors = CheckboxDefaults.colors(checkedColor = PortalNavy),
                    modifier = Modifier.testTag("consent_checkbox")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "I hereby consent to Jan Sahayata verifying my uploaded records with concerned state departments and DBT PFMS gateways.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
fun ReviewSectionCard(
    title: String,
    stepIndex: Int,
    onEdit: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit section",
                        tint = PortalSaffron,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            content()
        }
    }
}

// STEP 7: Application Processing Fee & Submit Confirmation Screen
@Composable
fun StepSubmitConfirmation(viewModel: MainViewModel, language: AppLanguage) {
    val feePaid by viewModel.wizardFeePaid.collectAsState()
    val feeAmount by viewModel.wizardFeeAmount.collectAsState()
    val txId by viewModel.wizardFeeTransactionId.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.HINDI) "चरण 7: आवेदन शुल्क भुगतान (₹280) एवं अंतिम प्रस्तुति" else "Step 7: Application Fee (₹280) & Final Submission",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PortalNavy
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (language == AppLanguage.HINDI) "सरकारी नियमों के अनुसार, आवेदन जांच और प्रत्यक्ष लाभ हस्तांतरण (DBT) सत्यापन हेतु ₹280 का ई-चालान शुल्क अनिवार्य है।" else "As per DBT welfare guidelines, a statutory fee of ₹280 is required for portal scrutiny, biometric linkage & document verification.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Official UPI QR Code Payment Card
        com.example.ui.components.UpiQrCodeView(
            feeAmount = feeAmount,
            upiId = "iamcaboy@gmail.com",
            merchantName = "Jan Sahayata DBT Portal",
            isPaid = feePaid,
            transactionId = txId,
            onPaymentCompleted = { newTxId ->
                viewModel.markWizardFeePaid(newTxId)
            },
            language = language
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PortalSaffronLight, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Note: Once submitted, your application will receive a unique Application Tracking Number, and an officer will begin verification. You will receive real-time SMS & In-App notifications at every step.",
                        fontSize = 11.sp,
                        color = Color(0xFF7C2D12),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun validateCurrentStep(step: Int, viewModel: MainViewModel): Boolean {
    return when (step) {
        1 -> viewModel.applicantName.value.isNotBlank() &&
                viewModel.parentName.value.isNotBlank() &&
                viewModel.applicantDistrict.value.isNotBlank() &&
                viewModel.applicantBlock.value.isNotBlank()
        2 -> viewModel.beneficiaryName.value.isNotBlank() &&
                viewModel.birthRegNumber.value.isNotBlank()
        3 -> viewModel.accountHolderName.value.isNotBlank() &&
                viewModel.accountNumber.value.isNotBlank() &&
                viewModel.accountNumber.value == viewModel.confirmAccountNumber.value &&
                SecurityUtil.isValidIfsc(viewModel.ifscCode.value)
        4 -> true // Checklist always returns calculation
        5 -> viewModel.uploadedDocuments.value.isNotEmpty()
        6 -> viewModel.declarationAccepted.value && viewModel.consentAuthorized.value
        7 -> viewModel.wizardFeePaid.value
        else -> true
    }
}

fun getStepValidationError(step: Int, viewModel: MainViewModel): String {
    return when (step) {
        1 -> "Please complete all mandatory fields in Step 1 (Applicant Name, Parent Name, District, Block)."
        2 -> "Please enter Beneficiary Name and Birth Registration Number."
        3 -> if (viewModel.accountNumber.value != viewModel.confirmAccountNumber.value)
            "Bank Account Numbers do not match."
        else if (!SecurityUtil.isValidIfsc(viewModel.ifscCode.value))
            "Invalid IFSC code format (e.g. SBIN0001234)."
        else
            "Please complete all bank details."
        4 -> "Please review the eligibility questions."
        5 -> "Please upload at least one required verification document."
        6 -> "Please check both the Declaration and Consent checkboxes to proceed."
        7 -> "Please verify & pay the ₹280 statutory application fee via UPI QR to finalize submission."
        else -> "Please review the information."
    }
}
