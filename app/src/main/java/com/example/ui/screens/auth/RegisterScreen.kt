package com.example.ui.screens.auth

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.JanTopAppBar
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalGreenLight
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalSaffron
import com.example.ui.theme.PortalSaffronLight
import com.example.util.AppLanguage
import com.example.util.SecurityUtil
import com.example.util.Strings
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: MainViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("Uttar Pradesh") }
    var district by remember { mutableStateOf("Lucknow") }
    var address by remember { mutableStateOf("") }
    var aadhaarLast4 by remember { mutableStateOf("") }
    var consentChecked by remember { mutableStateOf(false) }

    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Mock OTP Dialog
    var showOtpDialog by remember { mutableStateOf(false) }
    var generatedMockOtp by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            JanTopAppBar(
                title = Strings.get("register", language),
                currentLanguage = language,
                onLanguageToggle = { viewModel.toggleLanguage() },
                showBackButton = true,
                onBackClick = { onNavigate(Screen.HOME) }
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
            // Header Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PortalNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.HINDI) "नया नागरिक पंजीकरण" else "Citizen Scheme Registration",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (language == AppLanguage.HINDI)
                                "सत्यापित मोबाइल व आधार अंतिम 4 अंकों के साथ खाता बनाएं"
                            else
                                "Create verified account with mobile & masked Aadhaar",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Registration Form Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEE2E2), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFF991B1B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(Strings.get("full_name", language)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PortalNavy) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_fullname_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mobile
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { if (it.length <= 10) mobile = it.filter { c -> c.isDigit() } },
                        label = { Text(Strings.get("mobile_number", language)) },
                        placeholder = { Text("10-digit Indian Mobile") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PortalNavy) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_mobile_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(Strings.get("email_address", language)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PortalNavy) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(Strings.get("password", language)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PortalNavy) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_password_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(Strings.get("confirm_password", language)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PortalNavy) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_confirm_password_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // State & District in a row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text(Strings.get("state", language)) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = district,
                            onValueChange = { district = it },
                            label = { Text(Strings.get("district", language)) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("register_district_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Address
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(Strings.get("address", language)) },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = PortalNavy) },
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_address_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Aadhaar Last 4 Digits Only (Privacy Preserving)
                    OutlinedTextField(
                        value = aadhaarLast4,
                        onValueChange = { if (it.length <= 4) aadhaarLast4 = it.filter { c -> c.isDigit() } },
                        label = { Text(Strings.get("aadhaar_last4", language)) },
                        placeholder = { Text(Strings.get("aadhaar_hint", language)) },
                        leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null, tint = PortalNavy) },
                        trailingIcon = {
                            if (aadhaarLast4.length == 4) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Valid",
                                    tint = PortalGreen
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        supportingText = {
                            Text(
                                text = "Preview: ${SecurityUtil.formatMaskedAadhaar(aadhaarLast4)} (Never reveals full 12 digits)",
                                fontSize = 11.sp,
                                color = PortalGreen
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_aadhaar_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Consent Checkbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = consentChecked,
                            onCheckedChange = { consentChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = PortalNavy),
                            modifier = Modifier.testTag("register_consent_checkbox")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = Strings.get("consent_label", language),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit / Proceed to OTP Button
                    Button(
                        onClick = {
                            errorMessage = null
                            if (fullName.isBlank()) {
                                errorMessage = "Please enter your Full Name."
                                return@Button
                            }
                            if (!SecurityUtil.isValidPhone(mobile)) {
                                errorMessage = "Please enter a valid 10-digit Indian Mobile Number."
                                return@Button
                            }
                            if (!SecurityUtil.isValidEmail(email)) {
                                errorMessage = "Please enter a valid Email address."
                                return@Button
                            }
                            if (password.length < 6) {
                                errorMessage = "Password must be at least 6 characters."
                                return@Button
                            }
                            if (password != confirmPassword) {
                                errorMessage = "Passwords do not match."
                                return@Button
                            }
                            if (!SecurityUtil.isValidAadhaarLast4(aadhaarLast4)) {
                                errorMessage = "Please enter exactly 4 digits of Aadhaar."
                                return@Button
                            }
                            if (!consentChecked) {
                                errorMessage = "Please check the consent box to proceed."
                                return@Button
                            }

                            // Trigger mock OTP flow
                            val mockOtp = SecurityUtil.generateMockOtp()
                            generatedMockOtp = mockOtp
                            enteredOtp = ""
                            otpError = null
                            showOtpDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PortalSaffron),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("register_submit_button")
                    ) {
                        Text(
                            text = if (language == AppLanguage.HINDI) "OTP सत्यापन हेतु आगे बढ़ें" else "Proceed to OTP Verification",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = { onNavigate(Screen.LOGIN) }) {
                            Text(
                                text = Strings.get("already_registered", language),
                                color = PortalNavy,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Mock OTP Verification Dialog
        if (showOtpDialog) {
            AlertDialog(
                onDismissRequest = { showOtpDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = PortalNavy
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("otp_title", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PortalNavy
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "${Strings.get("otp_desc", language)}: +91 $mobile",
                            fontSize = 13.sp,
                            color = Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Simulation Banner with generated OTP
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PortalSaffronLight, RoundedCornerShape(8.dp))
                                .border(1.dp, PortalSaffron.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "SMS SIMULATION (Demonstration)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PortalSaffron
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Jan Sahayata OTP: $generatedMockOtp",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C2D12)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = enteredOtp,
                            onValueChange = { if (it.length <= 6) enteredOtp = it.filter { c -> c.isDigit() } },
                            label = { Text(Strings.get("enter_otp", language)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_input_field")
                        )

                        if (otpError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = otpError ?: "",
                                color = Color.Red,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                enteredOtp = generatedMockOtp
                            }) {
                                Text("Auto-fill OTP", fontSize = 12.sp, color = PortalGreen)
                            }
                            TextButton(onClick = {
                                generatedMockOtp = SecurityUtil.generateMockOtp()
                                otpError = null
                            }) {
                                Text(Strings.get("resend_otp", language), fontSize = 12.sp, color = PortalNavy)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (enteredOtp != generatedMockOtp) {
                                otpError = "Invalid OTP code. Please enter the 6-digit code shown above."
                                return@Button
                            }

                            isLoading = true
                            scope.launch {
                                val res = viewModel.authRepository.registerUser(
                                    fullName = fullName,
                                    mobileNumber = mobile,
                                    email = email,
                                    password = password,
                                    state = state,
                                    district = district,
                                    address = address,
                                    aadhaarLast4 = aadhaarLast4
                                )

                                isLoading = false
                                res.onSuccess { user ->
                                    showOtpDialog = false
                                    viewModel.showMessage("Registration successful! Welcome, ${user.fullName}")
                                    onNavigate(Screen.DASHBOARD)
                                }.onFailure { err ->
                                    showOtpDialog = false
                                    errorMessage = err.message
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PortalGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("verify_otp_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Text(
                                text = Strings.get("verify_proceed", language),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showOtpDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
