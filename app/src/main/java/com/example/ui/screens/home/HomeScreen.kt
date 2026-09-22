package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
import com.example.util.Strings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val scrollState = rememberScrollState()
    var trackAppNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            JanTopAppBar(
                currentLanguage = language,
                onLanguageToggle = { viewModel.toggleLanguage() },
                showBackButton = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(scrollState)
        ) {
            // HERO BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PortalNavy, PortalNavyDark)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(PortalSaffron.copy(alpha = 0.25f))
                            .border(1.dp, PortalSaffron.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = Strings.get("portal_badge", language),
                            color = Color(0xFFFFD54F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Emblem",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, PortalGold, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = Strings.get("hero_heading", language),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = Strings.get("hero_description", language),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (viewModel.currentUser.value != null) {
                                    viewModel.startNewApplication()
                                } else {
                                    onNavigate(Screen.LOGIN)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PortalSaffron),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("hero_apply_button")
                        ) {
                            Text(
                                text = Strings.get("hero_apply_btn", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                if (viewModel.currentUser.value != null) {
                                    onNavigate(Screen.DASHBOARD)
                                } else {
                                    onNavigate(Screen.REGISTER)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color.White, Color.LightGray))),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("hero_register_button")
                        ) {
                            Text(
                                text = if (viewModel.currentUser.value != null)
                                    Strings.get("dashboard", language)
                                else
                                    Strings.get("register", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Track Application Card
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = PortalNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.HINDI) "आवेदन की स्थिति तुरंत जांचें" else "Quick Status Lookup",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PortalNavy
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = trackAppNumber,
                                    onValueChange = { trackAppNumber = it },
                                    placeholder = { Text("e.g. JS-2026-481902", fontSize = 13.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .testTag("quick_track_input"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PortalNavy,
                                        unfocusedBorderColor = Color(0xFFCBD5E1)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (trackAppNumber.isNotBlank()) {
                                            viewModel.searchAndTrackApplication(trackAppNumber)
                                        } else {
                                            viewModel.showMessage("Please enter an Application Number")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(52.dp)
                                        .testTag("quick_track_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Track"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SCHEME SERVICES SECTION
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = Strings.get("services_heading", language),
                    icon = Icons.Default.ChildCare
                )
                Spacer(modifier = Modifier.height(10.dp))

                ServiceCard(
                    icon = Icons.Default.FamilyRestroom,
                    title = Strings.get("service_1_title", language),
                    desc = Strings.get("service_1_desc", language),
                    badge = if (language == AppLanguage.HINDI) "प्राथमिक अनुदान" else "Flagship Grant"
                )
                Spacer(modifier = Modifier.height(8.dp))
                ServiceCard(
                    icon = Icons.Default.School,
                    title = Strings.get("service_2_title", language),
                    desc = Strings.get("service_2_desc", language),
                    badge = if (language == AppLanguage.HINDI) "शिक्षा सहायता" else "Education DBT"
                )
                Spacer(modifier = Modifier.height(8.dp))
                ServiceCard(
                    icon = Icons.Default.AccountBalance,
                    title = Strings.get("service_3_title", language),
                    desc = Strings.get("service_3_desc", language),
                    badge = if (language == AppLanguage.HINDI) "प्रत्यक्ष अंतरण" else "Direct DBT"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ELIGIBILITY SECTION
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = Strings.get("eligibility_heading", language),
                    icon = Icons.Default.AssignmentTurnedIn
                )
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        EligibilityBullet(text = Strings.get("eligibility_point_1", language))
                        EligibilityBullet(text = Strings.get("eligibility_point_2", language))
                        EligibilityBullet(text = Strings.get("eligibility_point_3", language))
                        EligibilityBullet(text = Strings.get("eligibility_point_4", language))
                        EligibilityBullet(text = Strings.get("eligibility_point_5", language))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // HOW IT WORKS SECTION
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = Strings.get("how_it_works_heading", language),
                    icon = Icons.Default.Info
                )
                Spacer(modifier = Modifier.height(10.dp))

                HowItWorksCard(
                    stepNum = "1",
                    title = Strings.get("step_1_title", language),
                    desc = Strings.get("step_1_desc", language)
                )
                Spacer(modifier = Modifier.height(8.dp))
                HowItWorksCard(
                    stepNum = "2",
                    title = Strings.get("step_2_title", language),
                    desc = Strings.get("step_2_desc", language)
                )
                Spacer(modifier = Modifier.height(8.dp))
                HowItWorksCard(
                    stepNum = "3",
                    title = Strings.get("step_3_title", language),
                    desc = Strings.get("step_3_desc", language)
                )
                Spacer(modifier = Modifier.height(8.dp))
                HowItWorksCard(
                    stepNum = "4",
                    title = Strings.get("step_4_title", language),
                    desc = Strings.get("step_4_desc", language)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // REQUIRED DOCUMENTS
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = Strings.get("docs_heading", language),
                    icon = Icons.Default.Description
                )
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        DocumentItem(doc = Strings.get("doc_1", language))
                        DocumentItem(doc = Strings.get("doc_2", language))
                        DocumentItem(doc = Strings.get("doc_3", language))
                        DocumentItem(doc = Strings.get("doc_4", language))
                        DocumentItem(doc = Strings.get("doc_5", language))
                        DocumentItem(doc = Strings.get("doc_6", language))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FAQ SECTION
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = Strings.get("faq_heading", language),
                    icon = Icons.Default.HelpOutline
                )
                Spacer(modifier = Modifier.height(10.dp))

                FaqAccordion(
                    q = Strings.get("faq_q1", language),
                    a = Strings.get("faq_a1", language)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FaqAccordion(
                    q = Strings.get("faq_q2", language),
                    a = Strings.get("faq_a2", language)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FaqAccordion(
                    q = Strings.get("faq_q3", language),
                    a = Strings.get("faq_a3", language)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FaqAccordion(
                    q = Strings.get("faq_q4", language),
                    a = Strings.get("faq_a4", language)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // HELPDESK & CONTACT
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PortalSaffronLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Help",
                            tint = PortalSaffron
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("help_heading", language),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C2D12)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Strings.get("helpline", language),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF7C2D12),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Strings.get("email_support", language),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF7C2D12)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // OFFICIAL DISCLAIMER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Disclaimer",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.get("disclaimer_title", language),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Strings.get("disclaimer_text", language),
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FOOTER BAR WITH OFFICER LOGIN LINK
            Card(
                colors = CardDefaults.cardColors(containerColor = PortalNavy),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = Strings.get("app_title", language) + " - Jan Sahayata Citizen Portal",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Designed for transparency, integrity, and timely citizen DBT service.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onNavigate(Screen.ADMIN_LOGIN) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("officer_portal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Officer Portal",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("login_as_admin", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PortalNavy.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PortalNavy,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PortalNavy
            )
        )
    }
}

@Composable
fun ServiceCard(icon: ImageVector, title: String, desc: String, badge: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PortalSaffronLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PortalSaffron,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PortalNavy
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PortalGreenLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PortalGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
fun EligibilityBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(PortalGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF334155),
            lineHeight = 18.sp
        )
    }
}

@Composable
fun HowItWorksCard(stepNum: String, title: String, desc: String) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PortalNavy),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNum,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PortalNavy
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
fun DocumentItem(doc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = PortalGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = doc,
            fontSize = 13.sp,
            color = Color(0xFF334155)
        )
    }
}

@Composable
fun FaqAccordion(q: String, a: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = q,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PortalNavy
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (expanded) "▲" else "▼",
                    color = PortalNavy,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = a,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF475569),
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}
