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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tictactoe.game.engine.MatchRecord
import com.tictactoe.game.ui.theme.DarkSurface
import com.tictactoe.game.ui.theme.GlassBorder
import com.tictactoe.game.ui.theme.GlassSurface
import com.tictactoe.game.ui.theme.GoldAccent
import com.tictactoe.game.ui.theme.NeonCoral
import com.tictactoe.game.ui.theme.NeonCyan
import com.tictactoe.game.ui.theme.TextMuted
import com.tictactoe.game.ui.theme.TextSilver
import com.tictactoe.game.ui.theme.TextWhite

@Composable
fun HistoryDialog(
    history: List<MatchRecord>,
    onDismiss: () -> Unit
) {
    val totalGames = history.size
    val xWins = history.count { it.winner == "X" }
    val oWins = history.count { it.winner == "O" }
    val draws = history.count { it.winner == "Draw" }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .border(1.5.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MATCH ARCHIVE & STATS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextWhite,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Overview Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatMetric(title = "GAMES", value = "$totalGames", color = TextWhite)
                    StatMetric(title = "X WINS", value = "$xWins", color = NeonCyan)
                    StatMetric(title = "O WINS", value = "$oWins", color = NeonCoral)
                    StatMetric(title = "DRAWS", value = "$draws", color = GoldAccent)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "RECENT MATCH LOGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No recorded matches yet.\nPlay a round to start logging!",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(history) { match ->
                            MatchRow(match = match)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GlassSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DONE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSilver
                    )
                }
            }
        }
    }
}

@Composable
private fun StatMetric(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
    }
}

@Composable
private fun MatchRow(match: MatchRecord) {
    val winColor = when (match.winner) {
        "X" -> NeonCyan
        "O" -> NeonCoral
        else -> GoldAccent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141C2E))
            .border(1.dp, GlassBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(winColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (match.winner == "Draw") "=" else match.winner,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = winColor
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = if (match.winner == "Draw") "Draw" else "${match.winner} Won",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "${match.mode} • ${match.totalMoves} moves",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        Text(
            text = match.date,
            fontSize = 10.sp,
            color = TextMuted
        )
    }
}
