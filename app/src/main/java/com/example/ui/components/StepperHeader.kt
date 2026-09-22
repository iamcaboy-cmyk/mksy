package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalSaffron
import com.example.util.AppLanguage

@Composable
fun StepperHeader(
    currentStep: Int, // 1 to 7
    totalSteps: Int = 7,
    language: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    val stepTitlesEn = listOf(
        "Applicant",
        "Beneficiary",
        "Bank Details",
        "Eligibility",
        "Documents",
        "Review",
        "Submit"
    )

    val stepTitlesHi = listOf(
        "आवेदक",
        "लाभार्थी",
        "बैंक विवरण",
        "पात्रता",
        "दस्तावेज",
        "समीक्षा",
        "जमा"
    )

    val stepTitles = if (language == AppLanguage.HINDI) stepTitlesHi else stepTitlesEn
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        // Progress text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (language == AppLanguage.HINDI)
                    "चरण $currentStep / $totalSteps : ${stepTitles.getOrNull(currentStep - 1) ?: ""}"
                else
                    "Step $currentStep of $totalSteps : ${stepTitles.getOrNull(currentStep - 1) ?: ""}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
            )

            Text(
                text = "${(currentStep * 100) / totalSteps}%",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PortalSaffron
                )
            )
        }

        // Horizontal visual step trail
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..totalSteps) {
                val isCompleted = i < currentStep
                val isCurrent = i == currentStep

                val circleBg = when {
                    isCompleted -> PortalGreen
                    isCurrent -> PortalSaffron
                    else -> Color(0xFFE2E8F0)
                }

                val textColor = when {
                    isCompleted || isCurrent -> Color.White
                    else -> Color(0xFF64748B)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(68.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(circleBg),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "$i",
                                    color = textColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stepTitles[i - 1],
                            fontSize = 10.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) PortalNavy else Color(0xFF64748B)
                        )
                    }

                    if (i < totalSteps) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(2.dp)
                                .background(if (i < currentStep) PortalGreen else Color(0xFFE2E8F0))
                        )
                    }
                }
            }
        }
    }
}
