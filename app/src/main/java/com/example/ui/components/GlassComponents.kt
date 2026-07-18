package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.FrostedBg
import com.example.ui.theme.FrostedCardBg
import com.example.ui.theme.FrostedCardBorder
import com.example.ui.theme.IndigoAccent

@Composable
fun FrostedAmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FrostedBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Top-left emerald ambient blur glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        EmeraldAccent.copy(alpha = 0.20f),
                        EmeraldAccent.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(x = size.width * 0.1f, y = size.height * 0.1f),
                    radius = size.width * 0.9f
                ),
                radius = size.width * 0.9f,
                center = androidx.compose.ui.geometry.Offset(x = size.width * 0.1f, y = size.height * 0.1f)
            )

            // Bottom-right indigo ambient blur glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        IndigoAccent.copy(alpha = 0.22f),
                        IndigoAccent.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(x = size.width * 0.9f, y = size.height * 0.9f),
                    radius = size.width * 1.0f
                ),
                radius = size.width * 1.0f,
                center = androidx.compose.ui.geometry.Offset(x = size.width * 0.9f, y = size.height * 0.9f)
            )
        }

        content()
    }
}

@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderColor: Color = FrostedCardBorder,
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = FrostedCardBg,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
    ) {
        content()
    }
}
