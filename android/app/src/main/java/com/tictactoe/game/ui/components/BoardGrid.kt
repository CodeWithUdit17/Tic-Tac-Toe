package com.tictactoe.game.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tictactoe.game.engine.WinningLine
import com.tictactoe.game.ui.theme.CoralGlow
import com.tictactoe.game.ui.theme.CyanGlow
import com.tictactoe.game.ui.theme.DarkSurface
import com.tictactoe.game.ui.theme.GlassBorder
import com.tictactoe.game.ui.theme.GlassSurface
import com.tictactoe.game.ui.theme.GoldAccent
import com.tictactoe.game.ui.theme.NeonCoral
import com.tictactoe.game.ui.theme.NeonCyan
import com.tictactoe.game.ui.theme.WinLaserGreen

@Composable
fun BoardGrid(
    board: Array<Array<String>>,
    winningLine: WinningLine?,
    isGameOver: Boolean,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .aspectRatio(1f)
            .shadow(24.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFF0F172A))
            .clip(RoundedCornerShape(26.dp))
            .background(DarkSurface.copy(alpha = 0.85f))
            .border(1.5.dp, GlassBorder, RoundedCornerShape(26.dp))
            .padding(12.dp)
    ) {
        // 3x3 Cells
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (r in 0..2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (c in 0..2) {
                        val isWinningCell = winningLine != null && (
                                (r == winningLine.r1 && c == winningLine.c1) ||
                                        (r == winningLine.r2 && c == winningLine.c2) ||
                                        (r == winningLine.r3 && c == winningLine.c3)
                                )

                        BoardCell(
                            mark = board[r][c],
                            isWinningCell = isWinningCell,
                            onClick = { onCellClick(r, c) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }

        // Winning Laser Strike Beam Overlay
        if (winningLine != null) {
            WinningLaserLine(winningLine = winningLine)
        }
    }
}

@Composable
private fun BoardCell(
    mark: String,
    isWinningCell: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile bounce scale on press
    val scale = remember { Animatable(1f) }
    LaunchedEffect(isPressed) {
        scale.animateTo(
            targetValue = if (isPressed) 0.92f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Winning cell glow pulse
    val infiniteTransition = rememberInfiniteTransition(label = "winPulse")
    val winGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "winGlow"
    )

    val cellBorder = when {
        isWinningCell -> WinLaserGreen.copy(alpha = winGlowAlpha)
        mark == "X" -> CyanGlow.copy(alpha = 0.35f)
        mark == "O" -> CoralGlow.copy(alpha = 0.35f)
        else -> GlassBorder
    }

    val cellBg = when {
        isWinningCell -> WinLaserGreen.copy(alpha = 0.18f)
        mark == "X" -> CyanGlow.copy(alpha = 0.08f)
        mark == "O" -> CoralGlow.copy(alpha = 0.08f)
        else -> GlassSurface.copy(alpha = 0.85f)
    }

    Box(
        modifier = modifier
            .scale(scale.value)
            .clip(RoundedCornerShape(16.dp))
            .background(cellBg)
            .border(if (isWinningCell) 2.dp else 1.dp, cellBorder, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (mark.isNotEmpty()) {
            MarkDrawing(mark = mark, isWinning = isWinningCell)
        }
    }
}

@Composable
private fun MarkDrawing(mark: String, isWinning: Boolean) {
    val markScale = remember { Animatable(0f) }
    LaunchedEffect(mark) {
        markScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize(0.62f)
            .scale(markScale.value)
    ) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.15f
        val glowStrokeWidth = strokeWidth * 1.8f

        if (mark == "X") {
            val primaryColor = if (isWinning) WinLaserGreen else NeonCyan
            val glowColor = if (isWinning) WinLaserGreen.copy(alpha = 0.45f) else CyanGlow.copy(alpha = 0.4f)

            // Outer Soft Halo
            drawLine(
                color = glowColor,
                start = Offset(0f, 0f),
                end = Offset(w, h),
                strokeWidth = glowStrokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = glowColor,
                start = Offset(w, 0f),
                end = Offset(0f, h),
                strokeWidth = glowStrokeWidth,
                cap = StrokeCap.Round
            )

            // Sharp Inner Neon Core
            drawLine(
                color = primaryColor,
                start = Offset(0f, 0f),
                end = Offset(w, h),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = primaryColor,
                start = Offset(w, 0f),
                end = Offset(0f, h),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        } else if (mark == "O") {
            val primaryColor = if (isWinning) WinLaserGreen else NeonCoral
            val glowColor = if (isWinning) WinLaserGreen.copy(alpha = 0.45f) else CoralGlow.copy(alpha = 0.4f)
            val radius = (w - strokeWidth) / 2f
            val center = Offset(w / 2f, h / 2f)

            // Outer Soft Halo
            drawCircle(
                color = glowColor,
                radius = radius,
                center = center,
                style = Stroke(width = glowStrokeWidth, cap = StrokeCap.Round)
            )

            // Sharp Inner Neon Core
            drawCircle(
                color = primaryColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun WinningLaserLine(winningLine: WinningLine) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(winningLine) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellW = size.width / 3f
        val cellH = size.height / 3f

        val startX = (winningLine.c1 + 0.5f) * cellW
        val startY = (winningLine.r1 + 0.5f) * cellH

        val endX = (winningLine.c3 + 0.5f) * cellW
        val endY = (winningLine.r3 + 0.5f) * cellH

        val currentEndX = startX + (endX - startX) * progress.value
        val currentEndY = startY + (endY - startY) * progress.value

        val laserBrush = Brush.linearGradient(
            colors = listOf(WinLaserGreen, NeonCyan, GoldAccent),
            start = Offset(startX, startY),
            end = Offset(endX, endY)
        )

        // Outer laser bloom
        drawLine(
            brush = laserBrush,
            start = Offset(startX, startY),
            end = Offset(currentEndX, currentEndY),
            strokeWidth = 24f,
            cap = StrokeCap.Round,
            alpha = 0.35f
        )

        // Mid laser glow
        drawLine(
            brush = laserBrush,
            start = Offset(startX, startY),
            end = Offset(currentEndX, currentEndY),
            strokeWidth = 14f,
            cap = StrokeCap.Round,
            alpha = 0.7f
        )

        // Intense laser center beam
        drawLine(
            color = Color.White,
            start = Offset(startX, startY),
            end = Offset(currentEndX, currentEndY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
    }
}
