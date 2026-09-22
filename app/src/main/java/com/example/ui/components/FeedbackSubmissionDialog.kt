package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalSaffron
import com.example.util.AppLanguage

@Composable
fun FeedbackSubmissionDialog(
    applicationNumber: String = "",
    userEmail: String = "iamcaboy@gmail.com",
    language: AppLanguage = AppLanguage.ENGLISH,
    onSubmit: (
        easeOfUse: Int,
        clarityOfInfo: Int,
        serviceQuality: Int,
        category: String,
        comments: String,
        email: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var easeOfUse by remember { mutableStateOf(5) }
    var clarityOfInfo by remember { mutableStateOf(4) }
    var serviceQuality by remember { mutableStateOf(5) }
    var selectedCategory by remember { mutableStateOf("Ease of Use") }
    var comments by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf(userEmail.ifBlank { "iamcaboy@gmail.com" }) }
    var submitted by remember { mutableStateOf(false) }

    val categories = listOf(
        "Ease of Use",
        "Clarity of Information",
        "Service Quality",
        "General Suggestions",
        "Fee & Payment Process"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (!submitted) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PortalNavy.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RateReview,
                                    contentDescription = null,
                                    tint = PortalNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.HINDI) "नागरिक प्रतिक्रिया और रेटिंग" else "Citizen Feedback & Rating",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PortalNavy
                                    )
                                )
                                if (applicationNumber.isNotEmpty()) {
                                    Text(
                                        text = "Ref: $applicationNumber",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (language == AppLanguage.HINDI) "कृपया जन सहायता पोर्टल पर अपने अनुभव का मूल्यांकन करें।" else "Your feedback helps the Department of Social Welfare improve public service delivery and simplify welfare access.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Rating 1: Ease of Use
                    RatingRow(
                        title = if (language == AppLanguage.HINDI) "1. उपयोग में सरलता (Ease of Use)" else "1. Ease of Use",
                        description = "Navigation, wizard steps & form filling",
                        rating = easeOfUse,
                        onRatingChanged = { easeOfUse = it },
                        tag = "rating_ease_of_use"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rating 2: Clarity of Information
                    RatingRow(
                        title = if (language == AppLanguage.HINDI) "2. सूचना की स्पष्टता (Clarity of Information)" else "2. Clarity of Information",
                        description = "Eligibility rules, instructions & required documents",
                        rating = clarityOfInfo,
                        onRatingChanged = { clarityOfInfo = it },
                        tag = "rating_clarity_of_info"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rating 3: Service Quality
                    RatingRow(
                        title = if (language == AppLanguage.HINDI) "3. सेवा की गुणवत्ता (Service Quality)" else "3. Service Quality",
                        description = "Real-time updates, fee handling & transparency",
                        rating = serviceQuality,
                        onRatingChanged = { serviceQuality = it },
                        tag = "rating_service_quality"
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Selection Chips
                    Text(
                        text = if (language == AppLanguage.HINDI) "प्रतिक्रिया श्रेणी चुनें" else "Select Feedback Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PortalNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        categories.chunked(2).forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                chunk.forEach { cat ->
                                    val isSelected = selectedCategory == cat
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PortalNavy else Color(0xFFF1F5F9))
                                            .border(
                                                1.dp,
                                                if (isSelected) PortalNavy else Color(0xFFCBD5E1),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedCategory = cat }
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else PortalNavy
                                        )
                                    }
                                }
                                if (chunk.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email Address Field
                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { contactEmail = it },
                        label = { Text("Your Email Address") },
                        placeholder = { Text("iamcaboy@gmail.com") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feedback_email_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Comments Text Area
                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        label = { Text(if (language == AppLanguage.HINDI) "सामान्य टिप्पणियाँ / सुझाव" else "General Comments & Suggestions") },
                        placeholder = { Text("Share your suggestions, grievance, or praise...") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feedback_comments_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            onSubmit(
                                easeOfUse,
                                clarityOfInfo,
                                serviceQuality,
                                selectedCategory,
                                comments.ifBlank { "Citizen submitted ratings without text comment." },
                                contactEmail.ifBlank { "iamcaboy@gmail.com" }
                            )
                            submitted = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_feedback_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.HINDI) "प्रतिक्रिया जमा करें" else "Submit Feedback",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Success Screen inside Dialog
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PortalGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = PortalGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (language == AppLanguage.HINDI) "प्रतिक्रिया हेतु धन्यवाद!" else "Thank You For Your Feedback!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PortalNavy
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your rating has been submitted to the Welfare Department administrative oversight dashboard. Our grievance and service quality cell will review it shortly.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = PortalGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingRow(
    title: String,
    description: String,
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    tag: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PortalNavy
        )
        Text(
            text = description,
            fontSize = 11.sp,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                IconButton(
                    onClick = { onRatingChanged(i) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star $i",
                        tint = if (i <= rating) PortalGold else Color(0xFFCBD5E1),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$rating / 5",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PortalNavy
            )
        }
    }
}
