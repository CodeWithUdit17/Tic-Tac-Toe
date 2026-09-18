package com.tictactoe.game.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.game.engine.GameMode
import com.tictactoe.game.ui.theme.CoralGlow
import com.tictactoe.game.ui.theme.CyanGlow
import com.tictactoe.game.ui.theme.GlassBorder
import com.tictactoe.game.ui.theme.GlassSurface
import com.tictactoe.game.ui.theme.GoldAccent
import com.tictactoe.game.ui.theme.NeonCoral
import com.tictactoe.game.ui.theme.NeonCyan
import com.tictactoe.game.ui.theme.TextMuted
import com.tictactoe.game.ui.theme.TextSilver
import com.tictactoe.game.ui.theme.TextWhite

@Composable
fun ScoreBoard(
    currentPlayer: String,
    scoreX: Int,
    scoreO: Int,
    scoreDraws: Int,
    streakWinner: String,
    streakCount: Int,
    gameMode: GameMode,
    isGameOver: Boolean,
    modifier: Modifier = Modifier
) {
    val isXTurn = currentPlayer == "X" && !isGameOver
    val isOTurn = currentPlayer == "O" && !isGameOver

    val infiniteTransition = rememberInfiniteTransition(label = "pulseTurn")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player X Card
            PlayerScoreCard(
                playerName = "PLAYER 1",
                symbol = "X",
                score = scoreX,
                symbolColor = NeonCyan,
                glowColor = CyanGlow,
                isActive = isXTurn,
                pulseAlpha = if (isXTurn) pulseGlow else 0f,
                streak = if (streakWinner == "X" && streakCount > 1) streakCount else 0,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Central Draw Pill
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(GlassSurface.copy(alpha = 0.7f))
                    .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "DRAWS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$scoreDraws",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextSilver
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Player O Card
            val p2Name = if (gameMode == GameMode.PVE) "AI OPPONENT" else "PLAYER 2"
            PlayerScoreCard(
                playerName = p2Name,
                symbol = "O",
                score = scoreO,
                symbolColor = NeonCoral,
                glowColor = CoralGlow,
                isActive = isOTurn,
                pulseAlpha = if (isOTurn) pulseGlow else 0f,
                streak = if (streakWinner == "O" && streakCount > 1) streakCount else 0,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Turn Status Banner
        val turnText = if (isGameOver) {
            "ROUND CONCLUDED"
        } else if (currentPlayer == "X") {
            "⚡ PLAYER 1's TURN (X)"
        } else {
            if (gameMode == GameMode.PVE) "🤖 AI IS THINKING..." else "🔥 PLAYER 2's TURN (O)"
        }

        val bannerColor by animateColorAsState(
            targetValue = when {
                isGameOver -> GoldAccent
                currentPlayer == "X" -> NeonCyan
                else -> NeonCoral
            },
            animationSpec = tween(300),
            label = "bannerColor"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(bannerColor.copy(alpha = 0.12f))
                .border(1.dp, bannerColor.copy(alpha = 0.35f), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(bannerColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = turnText,
                color = bannerColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
private fun PlayerScoreCard(
    playerName: String,
    symbol: String,
    score: Int,
    symbolColor: Color,
    glowColor: Color,
    isActive: Boolean,
    pulseAlpha: Float,
    streak: Int,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isActive) {
        glowColor.copy(alpha = 0.4f + (pulseAlpha * 0.5f))
    } else {
        GlassBorder
    }

    val shadowElevation = if (isActive) 10.dp else 2.dp

    Box(
        modifier = modifier
            .shadow(shadowElevation, RoundedCornerShape(18.dp), spotColor = glowColor)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (isActive) listOf(
                        GlassSurface,
                        symbolColor.copy(alpha = 0.15f)
                    ) else listOf(
                        GlassSurface.copy(alpha = 0.6f),
                        GlassSurface.copy(alpha = 0.8f)
                    )
                )
            )
            .border(if (isActive) 2.dp else 1.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = symbol,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = symbolColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = playerName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) TextWhite else TextMuted,
                    letterSpacing = 0.8.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$score",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite
            )

            if (streak > 1) {
                Text(
                    text = "🔥 ${streak}x STREAK",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
