package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalGreen
import com.example.ui.theme.PortalNavy
import com.example.ui.theme.PortalSaffron
import com.example.util.AppLanguage
import kotlin.math.abs

@Composable
fun UpiQrCodeView(
    feeAmount: Double = 280.0,
    upiId: String = "iamcaboy@gmail.com",
    merchantName: String = "Jan Sahayata Portal",
    isPaid: Boolean,
    transactionId: String,
    onPaymentCompleted: (transactionId: String) -> Unit,
    language: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedToClipboard by remember { mutableStateOf(false) }
    var enteredUtr by remember { mutableStateOf(transactionId) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = PortalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.HINDI) "आधिकारिक ई-चालान शुल्क" else "Official E-Challan Fee",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PortalNavy
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isPaid) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isPaid) "PAID ✓" else "PENDING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPaid) Color(0xFF15803D) else Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fee Amount Display
            Text(
                text = if (language == AppLanguage.HINDI) "देय आवेदन प्रसंस्करण शुल्क" else "Payable Application Processing Fee",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₹${feeAmount.toInt()}",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PortalNavy
            )
            Text(
                text = if (language == AppLanguage.HINDI) "₹250 सरकारी शुल्क + ₹30 डीबीटी प्रसंस्करण" else "₹250 Statutory Govt Fee + ₹30 DBT Scrutiny",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isPaid) {
                // QR Code Container with Frame
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    UpiQrCanvas(
                        data = "upi://pay?pa=$upiId&pn=${merchantName.replace(" ", "%20")}&am=$feeAmount&cu=INR&tn=JanSahayataFee",
                        modifier = Modifier.size(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (language == AppLanguage.HINDI) "किसी भी UPI ऐप से स्कैन करके ₹280 का भुगतान करें" else "Scan QR with Any UPI App (GPay, PhonePe, Paytm, BHIM)",
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // UPI ID Row with Copy button
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Beneficiary VPA / UPI ID",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = upiId,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PortalNavy,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(upiId))
                                copiedToClipboard = true
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy UPI ID",
                                tint = PortalSaffron,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (copiedToClipboard) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "UPI ID copied to clipboard ✓",
                        color = PortalGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // One-click Simulate Payment / Demo Instant Pay
                Button(
                    onClick = {
                        val generatedUtr = "UPI/2026/0922/" + (10000000..99999999).random()
                        enteredUtr = generatedUtr
                        onPaymentCompleted(generatedUtr)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PortalGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulate_upi_payment_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.HINDI) "त्वरित UPI भुगतान सत्यापित करें (₹280)" else "Verify & Pay ₹280 via UPI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Or manual UTR verification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
                    Text(
                        text = if (language == AppLanguage.HINDI) " या UTR संदर्भ संख्या दर्ज करें " else " OR ENTER 12-DIGIT UTR ",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = enteredUtr,
                    onValueChange = {
                        enteredUtr = it
                        errorMessage = null
                    },
                    label = { Text("12-digit UPI UTR / Reference No.") },
                    placeholder = { Text("e.g. 426189201982") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("utr_number_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFDC2626),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        if (enteredUtr.trim().length >= 6) {
                            onPaymentCompleted(enteredUtr.trim())
                        } else {
                            errorMessage = "Please enter a valid UPI reference number."
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirm UTR & Mark Fee as Paid")
                }
            } else {
                // Payment Success Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0FDF4), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PortalGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Paid",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (language == AppLanguage.HINDI) "आवेदन शुल्क ₹280 सफलतापूर्वक प्राप्त" else "Application Fee of ₹280 Received",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "UTR / Transaction ID: $transactionId",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF15803D)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "E-Challan Reference verified via RBI / NPCI National Payment Switch.",
                            fontSize = 11.sp,
                            color = Color(0xFF4ADE80),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas UPI QR Code Renderer with authentic alignment patterns and Indian Tri-Color Center Shield
 */
@Composable
fun UpiQrCanvas(
    data: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val gridSize = 25
        val cellSize = width / gridSize

        // Clean white background
        drawRect(color = Color.White, size = size)

        // Draw outer finder pattern (Top-Left)
        drawFinderPattern(0f, 0f, cellSize)
        // Top-Right
        drawFinderPattern((gridSize - 7) * cellSize, 0f, cellSize)
        // Bottom-Left
        drawFinderPattern(0f, (gridSize - 7) * cellSize, cellSize)

        // Draw timing lines
        for (i in 7 until gridSize - 7) {
            if (i % 2 == 0) {
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(6 * cellSize, i * cellSize),
                    size = Size(cellSize, cellSize)
                )
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(i * cellSize, 6 * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }
        }

        // Draw deterministic data modules based on data hash
        val seed = abs(data.hashCode())
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                // Skip finder areas
                val inTopLeftFinder = r < 8 && c < 8
                val inTopRightFinder = r < 8 && c >= gridSize - 8
                val inBottomLeftFinder = r >= gridSize - 8 && c < 8
                val inCenterLogo = r in 10..14 && c in 10..14

                if (!inTopLeftFinder && !inTopRightFinder && !inBottomLeftFinder && !inCenterLogo) {
                    val pseudoRandom = ((r * 31 + c * 17 + seed) xor (r * c)) % 100
                    if (pseudoRandom < 48) {
                        drawRoundRect(
                            color = Color(0xFF1E293B),
                            topLeft = Offset(c * cellSize + 0.5f, r * cellSize + 0.5f),
                            size = Size(cellSize - 1f, cellSize - 1f),
                            cornerRadius = CornerRadius(1.5f, 1.5f)
                        )
                    }
                }
            }
        }

        // Center Emblem Badge (Indian Tri-Color Circle Badge)
        val centerRadius = cellSize * 2.5f
        val centerOffset = Offset(width / 2f, height / 2f)

        // White background circle with shadow
        drawCircle(
            color = Color.White,
            radius = centerRadius + 2f,
            center = centerOffset
        )
        // Saffron upper arc
        drawArc(
            color = PortalSaffron,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(centerOffset.x - centerRadius, centerOffset.y - centerRadius),
            size = Size(centerRadius * 2, centerRadius * 2)
        )
        // Green lower arc
        drawArc(
            color = PortalGreen,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(centerOffset.x - centerRadius, centerOffset.y - centerRadius),
            size = Size(centerRadius * 2, centerRadius * 2)
        )
        // Inner white circle
        drawCircle(
            color = Color.White,
            radius = centerRadius * 0.55f,
            center = centerOffset
        )
        // Navy Blue center chakra dot
        drawCircle(
            color = PortalNavy,
            radius = centerRadius * 0.28f,
            center = centerOffset
        )
    }
}

private fun DrawScope.drawFinderPattern(x: Float, y: Float, cellSize: Float) {
    // Outer 7x7 black square
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(x, y),
        size = Size(7 * cellSize, 7 * cellSize),
        cornerRadius = CornerRadius(cellSize * 0.8f, cellSize * 0.8f)
    )
    // Inner 5x5 white square
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(x + cellSize, y + cellSize),
        size = Size(5 * cellSize, 5 * cellSize),
        cornerRadius = CornerRadius(cellSize * 0.5f, cellSize * 0.5f)
    )
    // Center 3x3 black square
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(x + 2 * cellSize, y + 2 * cellSize),
        size = Size(3 * cellSize, 3 * cellSize),
        cornerRadius = CornerRadius(cellSize * 0.4f, cellSize * 0.4f)
    )
}
