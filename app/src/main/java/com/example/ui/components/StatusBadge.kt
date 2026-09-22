package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationStatus
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalGreenLight
import com.example.ui.theme.PortalRed
import com.example.ui.theme.PortalRedLight
import com.example.ui.theme.PortalSaffron
import com.example.ui.theme.PortalSaffronLight
import com.example.util.AppLanguage

@Composable
fun StatusBadge(
    status: ApplicationStatus,
    language: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (status) {
        ApplicationStatus.DRAFT -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), Color(0xFFCBD5E1))
        ApplicationStatus.SUBMITTED -> Triple(Color(0xFFEFF6FF), Color(0xFF1D4ED8), Color(0xFFBFDBFE))
        ApplicationStatus.UNDER_REVIEW -> Triple(Color(0xFFFFFBEB), Color(0xFFB45309), Color(0xFFFDE68A))
        ApplicationStatus.DOCUMENT_VERIFICATION -> Triple(Color(0xFFFAF5FF), Color(0xFF7E22CE), Color(0xFFE9D5FF))
        ApplicationStatus.APPROVED -> Triple(PortalGreenLight, PortalGreen, Color(0xFF86EFAC))
        ApplicationStatus.REJECTED -> Triple(PortalRedLight, PortalRed, Color(0xFFFCA5A5))
        ApplicationStatus.RETURNED_FOR_CORRECTION -> Triple(PortalSaffronLight, PortalSaffron, Color(0xFFFDBA74))
    }

    val label = if (language == AppLanguage.HINDI) status.labelHi else status.labelEn

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
