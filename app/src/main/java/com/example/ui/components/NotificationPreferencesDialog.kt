package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.NotificationPreferencesEntity
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalSaffron
import com.example.util.AppLanguage

@Composable
fun NotificationPreferencesDialog(
    initialPreferences: NotificationPreferencesEntity?,
    language: AppLanguage = AppLanguage.ENGLISH,
    onSave: (NotificationPreferencesEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val prefs = initialPreferences ?: NotificationPreferencesEntity(userId = 0)

    var inAppAlerts by remember { mutableStateOf(prefs.inAppAlertsEnabled) }
    var smsAlerts by remember { mutableStateOf(prefs.smsAlertsEnabled) }
    var emailAlerts by remember { mutableStateOf(prefs.emailAlertsEnabled) }
    var statusAlerts by remember { mutableStateOf(prefs.statusChangeAlerts) }
    var correctionAlerts by remember { mutableStateOf(prefs.correctionAlerts) }
    var paymentAlerts by remember { mutableStateOf(prefs.paymentAlerts) }
    var soundEnabled by remember { mutableStateOf(prefs.soundVibrationEnabled) }
    var emailAddress by remember { mutableStateOf(prefs.notificationEmail.ifBlank { "iamcaboy@gmail.com" }) }
    var mobileNumber by remember { mutableStateOf(prefs.notificationMobile) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = PortalNavy,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.HINDI) "अधिसूचना प्राथमिकताएँ" else "Notification Preferences",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PortalNavy,
                            fontSize = 18.sp
                        )
                    )
                }

                Text(
                    text = if (language == AppLanguage.HINDI) "आवेदन स्थिति में बदलाव की तत्काल सूचना प्राप्त करने के माध्यम चुनें" else "Choose how you want to be alerted for application updates & scrutiny remarks.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Section 1: Channels
                Text(
                    text = "Delivery Channels",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
                Spacer(modifier = Modifier.height(6.dp))

                PreferenceSwitchRow(
                    title = "In-App Alerts",
                    description = "Show immediate banner alerts inside Jan Sahayata app",
                    icon = Icons.Default.Notifications,
                    checked = inAppAlerts,
                    onCheckedChange = { inAppAlerts = it },
                    tag = "pref_in_app"
                )

                PreferenceSwitchRow(
                    title = "SMS Notifications",
                    description = "Receive instant SMS when officer changes file status",
                    icon = Icons.Default.PhoneAndroid,
                    checked = smsAlerts,
                    onCheckedChange = { smsAlerts = it },
                    tag = "pref_sms"
                )

                if (smsAlerts) {
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("SMS Mobile Number") },
                        placeholder = { Text("10-digit mobile number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                PreferenceSwitchRow(
                    title = "Email Notifications",
                    description = "Send detailed status updates & receipts to your email",
                    icon = Icons.Default.Email,
                    checked = emailAlerts,
                    onCheckedChange = { emailAlerts = it },
                    tag = "pref_email"
                )

                if (emailAlerts) {
                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        label = { Text("Notification Email") },
                        placeholder = { Text("iamcaboy@gmail.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("notification_email_input")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Section 2: Alert Categories
                Text(
                    text = "Alert Triggers",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PortalNavy
                )
                Spacer(modifier = Modifier.height(6.dp))

                PreferenceSwitchRow(
                    title = "Application Status Changes",
                    description = "Under Review, Verification, Approved, Rejected",
                    icon = Icons.Default.Notifications,
                    checked = statusAlerts,
                    onCheckedChange = { statusAlerts = it }
                )

                PreferenceSwitchRow(
                    title = "Officer Correction Requests",
                    description = "Immediate alert when an officer asks for re-uploaded documents",
                    icon = Icons.Default.Notifications,
                    checked = correctionAlerts,
                    onCheckedChange = { correctionAlerts = it }
                )

                PreferenceSwitchRow(
                    title = "Fee & Payment Confirmation",
                    description = "₹280 application fee receipt & e-Challan confirmation",
                    icon = Icons.Default.Notifications,
                    checked = paymentAlerts,
                    onCheckedChange = { paymentAlerts = it }
                )

                PreferenceSwitchRow(
                    title = "Sound & Vibration Alerts",
                    description = "Play notification chime on real-time alerts",
                    icon = Icons.Default.VolumeUp,
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val updated = prefs.copy(
                                inAppAlertsEnabled = inAppAlerts,
                                smsAlertsEnabled = smsAlerts,
                                emailAlertsEnabled = emailAlerts,
                                statusChangeAlerts = statusAlerts,
                                correctionAlerts = correctionAlerts,
                                paymentAlerts = paymentAlerts,
                                soundVibrationEnabled = soundEnabled,
                                notificationEmail = emailAddress.ifBlank { "iamcaboy@gmail.com" },
                                notificationMobile = mobileNumber,
                                updatedAt = System.currentTimeMillis()
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PortalNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_notification_preferences_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == AppLanguage.HINDI) "सहेजें" else "Save Preferences")
                    }
                }
            }
        }
    }
}

@Composable
fun PreferenceSwitchRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) PortalNavy else Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PortalNavy
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 14.sp
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PortalGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            ),
            modifier = if (tag.isNotEmpty()) Modifier.testTag(tag) else Modifier
        )
    }
}
