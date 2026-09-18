package com.tictactoe.game.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.game.engine.AiDifficulty
import com.tictactoe.game.engine.GameMode
import com.tictactoe.game.ui.theme.CyanGlow
import com.tictactoe.game.ui.theme.DarkSurface
import com.tictactoe.game.ui.theme.GlassBorder
import com.tictactoe.game.ui.theme.GlassSurface
import com.tictactoe.game.ui.theme.GoldAccent
import com.tictactoe.game.ui.theme.NeonCyan
import com.tictactoe.game.ui.theme.TextMuted
import com.tictactoe.game.ui.theme.TextSilver
import com.tictactoe.game.ui.theme.TextWhite

@Composable
fun ModeAndDifficultySelector(
    gameMode: GameMode,
    aiDifficulty: AiDifficulty,
    onModeChange: (GameMode) -> Unit,
    onDifficultyChange: (AiDifficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode Selector: vs AI | 2 Players
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface.copy(alpha = 0.9f))
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            ModeTab(
                title = "🤖 vs AI",
                isSelected = gameMode == GameMode.PVE,
                onClick = { onModeChange(GameMode.PVE) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            ModeTab(
                title = "👥 Pass & Play",
                isSelected = gameMode == GameMode.PVP,
                onClick = { onModeChange(GameMode.PVP) }
            )
        }

        // If PvE, show AI Difficulty selector
        if (gameMode == GameMode.PVE) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassSurface.copy(alpha = 0.6f))
                    .border(1.dp, GlassBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI LEVEL:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 6.dp, end = 4.dp)
                )

                AiDifficulty.values().forEach { diff ->
                    val isSelected = aiDifficulty == diff
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color.Transparent
                            )
                            .border(
                                if (isSelected) 1.dp else 0.dp,
                                if (isSelected) NeonCyan.copy(alpha = 0.6f) else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onDifficultyChange(diff) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = diff.displayName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) NeonCyan else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Brush.horizontalGradient(
                    listOf(NeonCyan.copy(alpha = 0.25f), CyanGlow.copy(alpha = 0.15f))
                ) else SolidColor(Color.Transparent)
            )
            .border(
                if (isSelected) 1.5.dp else 0.dp,
                if (isSelected) NeonCyan.copy(alpha = 0.8f) else Color.Transparent,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
            color = if (isSelected) TextWhite else TextSilver
        )
    }
}

@Composable
fun GameActionToolbar(
    canUndo: Boolean,
    onUndo: () -> Unit,
    onNewRound: () -> Unit,
    onResetScores: () -> Unit,
    onViewHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Undo Button
        GlassActionButton(
            label = "↩ Undo",
            enabled = canUndo,
            onClick = onUndo,
            accentColor = NeonCyan,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // New Round Button
        GlassActionButton(
            label = "⚡ New Round",
            enabled = true,
            onClick = onNewRound,
            accentColor = GoldAccent,
            modifier = Modifier.weight(1.2f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // History Button
        GlassActionButton(
            label = "📜 Logs",
            enabled = true,
            onClick = onViewHistory,
            accentColor = TextSilver,
            modifier = Modifier.weight(0.9f)
        )
    }
}

@Composable
private fun GlassActionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val alpha = if (enabled) 1f else 0.35f

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(GlassSurface.copy(alpha = 0.8f * alpha))
            .border(1.dp, GlassBorder.copy(alpha = alpha), RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor.copy(alpha = alpha),
            maxLines = 1
        )
    }
}
