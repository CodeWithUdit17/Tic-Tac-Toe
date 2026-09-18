package com.tictactoe.game.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.tictactoe.game.ui.theme.CyanGlow
import com.tictactoe.game.ui.theme.CoralGlow
import com.tictactoe.game.ui.theme.DarkBg

@Composable
fun GlowBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambientGlow")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Cyan ambient nebula in top-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyanGlow.copy(alpha = 0.18f),
                        CyanGlow.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.85f, h * 0.15f),
                    radius = w * 0.7f * pulse1
                ),
                radius = w * 0.7f * pulse1,
                center = Offset(w * 0.85f, h * 0.15f)
            )

            // Coral ambient nebula in bottom-left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CoralGlow.copy(alpha = 0.15f),
                        CoralGlow.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.15f, h * 0.82f),
                    radius = w * 0.75f * pulse2
                ),
                radius = w * 0.75f * pulse2,
                center = Offset(w * 0.15f, h * 0.82f)
            )

            // Subtle cyber grid dots
            val step = 48f
            var x = step / 2
            while (x < w) {
                var y = step / 2
                while (y < h) {
                    drawCircle(
                        color = Color(0xFF1E293B).copy(alpha = 0.25f),
                        radius = 1.2f,
                        center = Offset(x, y)
                    )
                    y += step
                }
                x += step
            }
        }

        content()
    }
}
