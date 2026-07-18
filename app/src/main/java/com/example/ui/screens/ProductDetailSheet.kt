package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Product
import com.example.ui.components.FrostedGlassCard
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextLightSlate

@Composable
fun ProductDetailSheet(
    product: Product,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPrizeScratched by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 44.dp) // Leave status bar space
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = Color(0xFF0F172A).copy(alpha = 0.94f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // Header Drag Handle & Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 4.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                CircleShape
                            )
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(bottom = 12.dp)
                ) {
                    // Category & Title
                    Text(
                        text = product.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldAccent,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )

                    Text(
                        text = "by ${product.brand}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSlate,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Price & Eatable Status Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Price Card
                        FrostedGlassCard(
                            modifier = Modifier.weight(1f),
                            cornerRadius = 16.dp,
                            backgroundColor = Color.White.copy(alpha = 0.05f),
                            borderColor = Color.White.copy(alpha = 0.12f)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "MARKET PRICE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSlate,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${product.price}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldAccent
                                )
                            }
                        }

                        // Edible/Eatable Card
                        FrostedGlassCard(
                            modifier = Modifier.weight(1f),
                            cornerRadius = 16.dp,
                            backgroundColor = Color.White.copy(alpha = 0.05f),
                            borderColor = if (product.isEatable) EmeraldAccent.copy(alpha = 0.25f) else Color(0xFFF87171).copy(alpha = 0.25f)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "CONSUMABILITY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (product.isEatable) EmeraldAccent else Color(0xFFF87171),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (product.isEatable) Icons.Outlined.CheckCircle else Icons.Outlined.Warning,
                                        contentDescription = null,
                                        tint = if (product.isEatable) EmeraldAccent else Color(0xFFF87171),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (product.isEatable) "Eatable" else "Non-Eatable",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (product.isEatable) EmeraldAccent else Color(0xFFF87171)
                                    )
                                }
                            }
                        }
                    }

                    // FSSAI Certification Card (If Eatable)
                    if (product.isEatable && product.isFssaiCertified) {
                        FrostedGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            backgroundColor = Color.White.copy(alpha = 0.05f),
                            borderColor = EmeraldAccent.copy(alpha = 0.25f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(EmeraldAccent, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Safe",
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Certified Eatable",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldAccent
                                        )
                                        Text(
                                            text = "Safe for general consumption",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSlate
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "fssai",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = Color(0xFF1E3A8A), // blue-900
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                    Text(
                                        text = "Lic No. ${product.fssaiLicenseNo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        color = TextLightSlate
                                    )
                                }
                            }
                        }
                    } else if (product.isEatable && !product.isFssaiCertified) {
                        FrostedGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            backgroundColor = Color.White.copy(alpha = 0.05f),
                            borderColor = Color(0xFFF87171).copy(alpha = 0.25f)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color(0xFFF87171), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Uncertified Food Product",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF87171)
                                    )
                                    Text(
                                        text = "Please consume at your own risk. No valid registration found.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSlate
                                    )
                                }
                            }
                        }
                    }

                    // Scratch Card / Loyalty Reward Section
                    Text(
                        text = "MARKET LOYALTY REWARD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldAccent,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .padding(bottom = 20.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isPrizeScratched) {
                                    Brush.horizontalGradient(
                                        listOf(
                                            EmeraldAccent.copy(alpha = 0.12f),
                                            IndigoAccent.copy(alpha = 0.12f)
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                    )
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isPrizeScratched) EmeraldAccent.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(enabled = !isPrizeScratched) { isPrizeScratched = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isPrizeScratched) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap to Reveal Your Lucky Prize Coupon!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Scratch & Claim Market Special Reward",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSlate
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(
                                            EmeraldAccent.copy(alpha = 0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Won",
                                        tint = EmeraldAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Congratulations! You won:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldAccent
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = product.prize,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Health Score Bar Counter matching HTML design!
                    if (product.isEatable) {
                        Text(
                            text = "HEALTH EVALUATION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        FrostedGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            backgroundColor = IndigoAccent.copy(alpha = 0.10f),
                            borderColor = IndigoAccent.copy(alpha = 0.25f),
                            cornerRadius = 16.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Health Score",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFA5B4FC) // Indigo light accent
                                    )
                                    Text(
                                        text = "82/100",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Progress track
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .background(Color.White.copy(alpha = 0.12f), CircleShape)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.82f)
                                            .fillMaxHeight()
                                            .background(IndigoAccent, CircleShape)
                                    )
                                }
                            }
                        }
                    }

                    // Ingredients List
                    if (product.ingredients.isNotEmpty()) {
                        Text(
                            text = "INGREDIENTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        FrostedGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            backgroundColor = Color.White.copy(alpha = 0.04f),
                            borderColor = Color.White.copy(alpha = 0.08f)
                        ) {
                            Text(
                                text = product.ingredients.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextLightSlate,
                                modifier = Modifier.padding(14.dp),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Allergens / Hazards Warnings
                    if (product.allergens.isNotEmpty()) {
                        Text(
                            text = if (product.isEatable) "DIETARY NOTES & ALLERGENS" else "HAZARD & SAFETY WARNINGS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (product.isEatable) Color(0xFFF97316) else Color(0xFFF87171),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            product.allergens.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (product.isEatable) Color(0x22F97316) else Color(0x22F87171),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (product.isEatable) Color(0x55F97316) else Color(0x55F87171),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (product.isEatable) Icons.Default.Info else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (product.isEatable) Color(0xFFFB923C) else Color(0xFFF87171),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (product.isEatable) Color(0xFFFB923C) else Color(0xFFF87171)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Nutrition Facts / Chemical Composition Block
                    Text(
                        text = if (product.isEatable) "NUTRITIONAL FACTS / PER SERVING" else "CHEMICAL STABILITY & SPECS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Row 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Calories Card
                            FrostedGlassCard(
                                modifier = Modifier.weight(1f),
                                cornerRadius = 14.dp,
                                backgroundColor = Color.White.copy(alpha = 0.04f),
                                borderColor = Color.White.copy(alpha = 0.08f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = product.nutrition["Calories"] ?: product.nutrition["Energy"] ?: "184",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "CALORIES",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSlate
                                    )
                                }
                            }

                            // Protein Card
                            FrostedGlassCard(
                                modifier = Modifier.weight(1f),
                                cornerRadius = 14.dp,
                                backgroundColor = Color.White.copy(alpha = 0.04f),
                                borderColor = Color.White.copy(alpha = 0.08f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = product.nutrition["Protein"] ?: "5.2g",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "PROTEIN",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSlate
                                    )
                                }
                            }
                        }

                        // Row 2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Carbs Card
                            FrostedGlassCard(
                                modifier = Modifier.weight(1f),
                                cornerRadius = 14.dp,
                                backgroundColor = Color.White.copy(alpha = 0.04f),
                                borderColor = Color.White.copy(alpha = 0.08f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = product.nutrition["Carbohydrates"] ?: product.nutrition["Carbs"] ?: "24g",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "CARBS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSlate
                                    )
                                }
                            }

                            // Sugar Card (Sugar uses accent Orange or Red if high)
                            FrostedGlassCard(
                                modifier = Modifier.weight(1f),
                                cornerRadius = 14.dp,
                                backgroundColor = Color.White.copy(alpha = 0.04f),
                                borderColor = Color.White.copy(alpha = 0.08f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    val sugarVal = product.nutrition["Sugar"] ?: product.nutrition["Sugars"] ?: "6.8g"
                                    Text(
                                        text = sugarVal,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFB923C) // orange-400
                                    )
                                    Text(
                                        text = "SUGAR",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSlate
                                    )
                                }
                            }
                        }
                    }
                }

                // Beautiful absolute "Add to Basket" / "Add to Cart" button at the bottom of sheet
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldAccent,
                        contentColor = Color(0xFF0F172A)
                    ),
                    elevation = ButtonDefaults.buttonColors().let { ButtonDefaults.buttonElevation(defaultElevation = 0.dp) }
                ) {
                    Text(
                        text = "ADD TO BASKET",
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// Simple FlowRow helper since standard Layout FlowRow might require multiplatform compiler configurations
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val layoutWidth = constraints.maxWidth
        
        var currentY = 0
        var currentX = 0
        var rowMaxHeight = 0
        
        val itemPositions = mutableListOf<Triple<androidx.compose.ui.layout.Placeable, Int, Int>>()
        
        placeables.forEach { placeable ->
            if (currentX + placeable.width > layoutWidth) {
                // Next line
                currentX = 0
                currentY += rowMaxHeight + verticalArrangement.let { 8.dp.roundToPx() } // default margin
                rowMaxHeight = 0
            }
            itemPositions.add(Triple(placeable, currentX, currentY))
            currentX += placeable.width + horizontalArrangement.let { 8.dp.roundToPx() }
            rowMaxHeight = maxOf(rowMaxHeight, placeable.height)
        }
        
        val finalHeight = currentY + rowMaxHeight
        layout(layoutWidth, finalHeight) {
            itemPositions.forEach { (placeable, x, y) ->
                placeable.placeRelative(x, y)
            }
        }
    }
}
