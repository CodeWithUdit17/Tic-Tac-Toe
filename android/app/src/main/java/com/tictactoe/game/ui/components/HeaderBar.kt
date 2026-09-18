package com.tictactoe.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoe.game.ui.theme.CyanGlow
import com.tictactoe.game.ui.theme.GlassBorder
import com.tictactoe.game.ui.theme.GlassSurface
import com.tictactoe.game.ui.theme.GoldAccent
import com.tictactoe.game.ui.theme.NeonCyan
import com.tictactoe.game.ui.theme.TextSilver
import com.tictactoe.game.ui.theme.TextWhite

@Composable
fun HeaderBar(
    isSoundEnabled: Boolean,
    isHapticsEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onResetScores: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title with Cyber Badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "TIC•TAC•TOE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(listOf(NeonCyan, CyanGlow))
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "ULTRA",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        // Action Toggles
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Audio toggle
            HeaderIconButton(
                icon = if (isSoundEnabled) "🔊" else "🔇",
                onClick = onToggleSound
            )

            // Haptic toggle
            HeaderIconButton(
                icon = if (isHapticsEnabled) "📳" else "📴",
                onClick = onToggleHaptics
            )

            // Reset scores icon
            HeaderIconButton(
                icon = "🔄",
                onClick = onResetScores
            )
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(GlassSurface.copy(alpha = 0.8f))
            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = icon, fontSize = 14.sp)
    }
}
