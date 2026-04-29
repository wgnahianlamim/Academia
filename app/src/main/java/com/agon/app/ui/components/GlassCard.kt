package com.agon.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.agon.app.ui.theme.Charcoal
import com.agon.app.ui.theme.GlassBorder
import com.agon.app.ui.theme.LightGlass
import com.agon.app.ui.theme.MidnightBlue

@Composable
fun GlassBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Charcoal, MidnightBlue),
                    radius = 2000f
                )
            )
    ) {
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(LightGlass)
            .border(BorderStroke(0.5.dp, GlassBorder), RoundedCornerShape(16.dp))
            // .blur(20.dp) // Blur can be expensive and sometimes buggy on older devices, but we'll try it if needed. Actually we just use a semi-transparent background for glass effect.
    ) {
        content()
    }
}
