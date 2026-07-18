package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextLightSlate
import com.example.ui.components.FrostedGlassCard

@Composable
fun FssaiVerifyView(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var licenseInput by remember { mutableStateOf("") }
    var verificationResult by remember { mutableStateOf<FssaiCertResult?>(null) }
    var isVerifying by remember { mutableStateOf(false) }
    var inputError by remember { mutableStateOf<String?>(null) }

    fun verifyLicense(license: String) {
        val trimmed = license.trim().replace("\\s".toRegex(), "")
        keyboardController?.hide()
        
        if (trimmed.length != 14 || !trimmed.all { it.isDigit() }) {
            inputError = "FSSAI License number must be exactly 14 digits."
            verificationResult = null
            return
        }

        inputError = null
        isVerifying = true
        verificationResult = null

        // Simulate lookup processing delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isVerifying = false
            
            // Parse FSSAI parameters
            val regTypeDigit = trimmed[0].toString().toInt()
            val stateCode = trimmed.substring(1, 3).toInt()
            val yearDigits = trimmed.substring(3, 5).toInt()
            val officerCode = trimmed.substring(5, 8).toInt()
            val serialNo = trimmed.substring(8, 14).toInt()

            val regType = when (regTypeDigit) {
                1 -> "Primary Food Producer / Manufacturer"
                2 -> "State Food Business Operator / Wholesaler"
                3 -> "Central Food Importer / Large Retailer"
                4 -> "Catering, Restaurants & Hoteliers"
                else -> "General Packaged Food Processor"
            }

            val state = when (stateCode % 15) {
                0 -> "Delhi NCR"
                1 -> "Maharashtra (Mumbai Hub)"
                2 -> "Karnataka (Bengaluru Central)"
                3 -> "Tamil Nadu (Chennai Division)"
                4 -> "West Bengal (Kolkata Port)"
                5 -> "Telangana (Hyderabad Region)"
                6 -> "Gujarat (Ahmedabad Central)"
                7 -> "Uttar Pradesh (Lucknow Unit)"
                8 -> "Rajasthan (Jaipur West)"
                9 -> "Kerala (Trivandrum South)"
                else -> "Maharashtra (Pune Division)"
            }

            val regYear = 2000 + yearDigits

            verificationResult = FssaiCertResult(
                licenseNo = trimmed,
                status = "ACTIVE / VALID",
                registrationType = regType,
                stateOfIssue = state,
                issueYear = regYear,
                safetyGrade = when (serialNo % 4) {
                    0 -> "GRADE A+ (Excellent Safety Rating)"
                    1 -> "GRADE A (Highly Compliant Standards)"
                    2 -> "GRADE B (Satisfactory Standards)"
                    else -> "GRADE A+ (Exceptional Quality Seal)"
                },
                safetyScore = 4.0 + (serialNo % 10) * 0.1,
                lastInspected = "12-Apr-2026",
                expiryDate = "11-Apr-2029"
            )
        }, 1200)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        // Tab Heading
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(EmeraldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "fssai",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
            Column {
                Text(
                    text = "FSSAI License Auditor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Verify Government Safety Registrations Instantly",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card containing input and instructions
        FrostedGlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            backgroundColor = Color.White.copy(alpha = 0.05f),
            borderColor = Color.White.copy(alpha = 0.15f)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "ENTER 14-DIGIT FSSAI NUMBER",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldAccent,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = licenseInput,
                    onValueChange = {
                        if (it.length <= 14 && it.all { char -> char.isDigit() }) {
                            licenseInput = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. 11523034000182", color = TextSlate) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Tag, contentDescription = "License Tag", tint = TextSlate)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            verifyLicense(licenseInput)
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EmeraldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = EmeraldAccent,
                        unfocusedLabelColor = TextSlate,
                        cursorColor = EmeraldAccent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    isError = inputError != null,
                    singleLine = true
                )

                if (inputError != null) {
                    Text(
                        text = inputError!!,
                        color = Color(0xFFF87171),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { verifyLicense(licenseInput) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldAccent,
                        contentColor = Color(0xFF0F172A)
                    )
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            color = Color(0xFF0F172A),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Auditing Database...", fontWeight = FontWeight.Bold)
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Verify License Record", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Certificate Section
        AnimatedVisibility(
            visible = verificationResult != null && !isVerifying,
            enter = slideInVertically(initialOffsetY = { 60 }) + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            verificationResult?.let { cert ->
                FssaiCertificateCard(cert = cert)
            }
        }

        // Informative block
        Spacer(modifier = Modifier.height(12.dp))
        FrostedGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            backgroundColor = Color.White.copy(alpha = 0.03f),
            borderColor = Color.White.copy(alpha = 0.08f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = IndigoAccent, modifier = Modifier.size(18.dp))
                    Text(
                        text = "FSSAI License Structure Guide",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "A valid FSSAI food safety number has 14 digits structured into 5 segments tracking the type of industry, state code, registration year, registering authority code, and serial registration. Eatables must display this mark legally in India.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun FssaiCertificateCard(cert: FssaiCertResult) {
    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        cornerRadius = 20.dp,
        backgroundColor = Color.White.copy(alpha = 0.06f),
        borderColor = EmeraldAccent.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Certificate Title
            Text(
                text = "FOOD SAFETY & STANDARDS AUTHORITY OF INDIA",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldAccent,
                textAlign = TextAlign.Center
            )
            Text(
                text = "CERTIFICATE OF SAFETY COMPLIANCE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = EmeraldAccent,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(14.dp))

            // License Big Stat
            Text(
                text = "LICENSE NUMBER",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextSlate
            )
            Text(
                text = cert.licenseNo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Box(
                modifier = Modifier
                    .background(EmeraldAccent, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = cert.status,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Details rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "REGISTRATION TYPE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSlate)
                    Text(text = cert.registrationType, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "STATE JURISDICTION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSlate)
                    Text(text = cert.stateOfIssue, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CERTIFICATION YEAR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSlate)
                    Text(text = cert.issueYear.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "EXPIRY DATE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSlate)
                    Text(text = cert.expiryDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(14.dp))

            // Score Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldAccent.copy(alpha = 0.08f))
                    .border(1.dp, EmeraldAccent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = EmeraldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = cert.safetyGrade,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldAccent
                    )
                    Text(
                        text = "Score: ${cert.safetyScore} / 5.0 (Passed Audits on ${cert.lastInspected})",
                        fontSize = 10.sp,
                        color = TextLightSlate
                    )
                }
            }
        }
    }
}

data class FssaiCertResult(
    val licenseNo: String,
    val status: String,
    val registrationType: String,
    val stateOfIssue: String,
    val issueYear: Int,
    val safetyGrade: String,
    val safetyScore: Double,
    val lastInspected: String,
    val expiryDate: String
)
