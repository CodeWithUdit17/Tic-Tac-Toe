package com.tictactoe.game.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tictactoe.game.engine.GameMode
import com.tictactoe.game.ui.theme.CyanGlow
import com.tictactoe.game.ui.theme.DarkSurface
import com.tictactoe.game.ui.theme.GlassBorder
import com.tictactoe.game.ui.theme.GoldAccent
import com.tictactoe.game.ui.theme.NeonCoral
import com.tictactoe.game.ui.theme.NeonCyan
import com.tictactoe.game.ui.theme.TextMuted
import com.tictactoe.game.ui.theme.TextSilver
import com.tictactoe.game.ui.theme.TextWhite
import com.tictactoe.game.ui.theme.WinLaserGreen
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val speedY: Float,
    val size: Float,
    val color: Color,
    val phase: Float
)

@Composable
fun VictoryDialog(
    winner: String, // "X", "O", "Draw"
    gameMode: GameMode,
    movesCount: Int,
    onPlayAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDraw = winner == "Draw"
    val isXWin = winner == "X"

    val title = when {
        isDraw -> "STALEMATE!"
        isXWin -> "PLAYER 1 VICTORIOUS!"
        gameMode == GameMode.PVE -> "AI TAKES THE ROUND!"
        else -> "PLAYER 2 VICTORIOUS!"
    }

    val subtitle = when {
        isDraw -> "Equally matched intellects. No moves left!"
        isXWin -> "Masterful precision and flawless execution!"
        gameMode == GameMode.PVE -> "The neural algorithm strikes again."
        else -> "Player 2 claimed dominance!"
    }

    val themeColor = when {
        isDraw -> GoldAccent
        isXWin -> NeonCyan
        else -> NeonCoral
    }

    // Floating particles
    val particles = remember {
        List(40) {
            Particle(
                x = Random.nextFloat(),
                speedY = Random.nextFloat() * 0.8f + 0.5f,
                size = Random.nextFloat() * 6f + 4f,
                color = listOf(NeonCyan, NeonCoral, GoldAccent, WinLaserGreen).random(),
                phase = Random.nextFloat() * 6.28f
            )
        }
    }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(DarkSurface.copy(alpha = 0.95f))
                .border(2.dp, themeColor.copy(alpha = 0.7f), RoundedCornerShape(28.dp))
                .shadow(32.dp, RoundedCornerShape(28.dp), spotColor = themeColor)
        ) {
            // Particle celebration Canvas
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height
                val p = animProgress.value

                particles.forEach { pt ->
                    val y = (pt.speedY * p * h) % h
                    val x = (pt.x * w) + kotlin.math.sin(p * 6.28f + pt.phase).toFloat() * 20f
                    drawRect(
                        color = pt.color.copy(alpha = 0.7f),
                        topLeft = Offset(x, y),
                        size = Size(pt.size, pt.size)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy or Symbol icon
                Text(
                    text = if (isDraw) "🤝" else "🏆",
                    fontSize = 54.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = themeColor,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSilver,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF172033))
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Total Moves: ",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "$movesCount turns",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(themeColor, themeColor.copy(alpha = 0.7f))
                            )
                        )
                        .clickable(onClick = onPlayAgain),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PLAY AGAIN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Close",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    modifier = Modifier
                        .clickable(onClick = onDismiss)
                        .padding(8.dp)
                )
            }
        }
    }
}
