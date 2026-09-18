package com.tictactoe.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tictactoe.game.engine.AiDifficulty
import com.tictactoe.game.engine.GameMode
import com.tictactoe.game.engine.TicTacToeEngine
import com.tictactoe.game.sound.SoundManager
import com.tictactoe.game.ui.components.BoardGrid
import com.tictactoe.game.ui.components.GameActionToolbar
import com.tictactoe.game.ui.components.GlowBackground
import com.tictactoe.game.ui.components.HeaderBar
import com.tictactoe.game.ui.components.HistoryDialog
import com.tictactoe.game.ui.components.ModeAndDifficultySelector
import com.tictactoe.game.ui.components.ScoreBoard
import com.tictactoe.game.ui.components.VictoryDialog
import com.tictactoe.game.ui.theme.DarkBg
import com.tictactoe.game.ui.theme.TicTacToeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBg
                ) {
                    TicTacToeGameScreen()
                }
            }
        }
    }
}

@Composable
fun TicTacToeGameScreen() {
    val context = LocalContext.current
    val engine = remember { TicTacToeEngine(context) }
    val soundManager = remember { SoundManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Game UI State triggers
    var gameStateRevision by remember { mutableIntStateOf(0) }
    var isSoundEnabled by remember { mutableStateOf(soundManager.isSoundEnabled) }
    var isHapticsEnabled by remember { mutableStateOf(soundManager.isHapticsEnabled) }

    var showVictoryDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    var aiJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            aiJob?.cancel()
            soundManager.release()
        }
    }

    fun refreshState() {
        gameStateRevision++
    }

    // Function to perform AI turn if applicable
    fun triggerAiIfNeeded() {
        if (engine.gameMode == GameMode.PVE && engine.currentPlayer == "O" && !engine.isGameOver) {
            aiJob?.cancel()
            aiJob = coroutineScope.launch {
                delay(280) // Responsive AI deliberation delay
                if (engine.isGameOver || engine.gameMode != GameMode.PVE || engine.currentPlayer != "O") return@launch
                val aiMove = engine.getBestAiMove()
                if (aiMove != null && !engine.isGameOver) {
                    val moved = engine.makeMove(aiMove.first, aiMove.second)
                    if (moved) {
                        soundManager.playAiMove()
                        refreshState()
                        if (engine.isGameOver) {
                            delay(350)
                            if (engine.winner == "Draw") {
                                soundManager.playDraw()
                            } else {
                                soundManager.playWin()
                            }
                            showVictoryDialog = true
                        }
                    }
                }
            }
        }
    }

    GlowBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Header Bar
            HeaderBar(
                isSoundEnabled = isSoundEnabled,
                isHapticsEnabled = isHapticsEnabled,
                onToggleSound = {
                    soundManager.isSoundEnabled = !soundManager.isSoundEnabled
                    isSoundEnabled = soundManager.isSoundEnabled
                },
                onToggleHaptics = {
                    soundManager.isHapticsEnabled = !soundManager.isHapticsEnabled
                    isHapticsEnabled = soundManager.isHapticsEnabled
                },
                onResetScores = {
                    aiJob?.cancel()
                    engine.resetScores()
                    engine.resetBoard()
                    soundManager.playUndo()
                    refreshState()
                }
            )

            // 2. Scoreboard & Live Status
            ScoreBoard(
                currentPlayer = engine.currentPlayer,
                scoreX = engine.scoresX,
                scoreO = engine.scoresO,
                scoreDraws = engine.scoresDraws,
                streakWinner = engine.streakWinner,
                streakCount = engine.streakCount,
                gameMode = engine.gameMode,
                isGameOver = engine.isGameOver,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // 3. 3x3 Board Grid
            BoardGrid(
                board = engine.board,
                winningLine = engine.winningLine,
                isGameOver = engine.isGameOver,
                onCellClick = { r, c ->
                    if (engine.isGameOver) return@BoardGrid
                    if (engine.gameMode == GameMode.PVE && engine.currentPlayer == "O") return@BoardGrid

                    val success = engine.makeMove(r, c)
                    if (success) {
                        soundManager.playTap()
                        refreshState()

                        if (engine.isGameOver) {
                            aiJob?.cancel()
                            coroutineScope.launch {
                                delay(350)
                                if (engine.winner == "Draw") {
                                    soundManager.playDraw()
                                } else {
                                    soundManager.playWin()
                                }
                                showVictoryDialog = true
                            }
                        } else {
                            triggerAiIfNeeded()
                        }
                    }
                }
            )

            // 4. Mode & AI Difficulty Selector
            ModeAndDifficultySelector(
                gameMode = engine.gameMode,
                aiDifficulty = engine.aiDifficulty,
                onModeChange = { newMode ->
                    aiJob?.cancel()
                    engine.gameMode = newMode
                    engine.resetBoard()
                    refreshState()
                },
                onDifficultyChange = { newDiff ->
                    engine.aiDifficulty = newDiff
                    refreshState()
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // 5. Bottom Action Toolbar
            GameActionToolbar(
                canUndo = engine.moveHistory.isNotEmpty() && !engine.isGameOver,
                onUndo = {
                    aiJob?.cancel()
                    if (engine.undoLastMove()) {
                        soundManager.playUndo()
                        refreshState()
                    }
                },
                onNewRound = {
                    aiJob?.cancel()
                    engine.resetBoard()
                    soundManager.playUndo()
                    refreshState()
                },
                onResetScores = {
                    aiJob?.cancel()
                    engine.resetScores()
                    engine.resetBoard()
                    refreshState()
                },
                onViewHistory = {
                    showHistoryDialog = true
                },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }

    // Victory Dialog
    if (showVictoryDialog && engine.winner != null) {
        VictoryDialog(
            winner = engine.winner!!,
            gameMode = engine.gameMode,
            movesCount = engine.moveHistory.size,
            onPlayAgain = {
                showVictoryDialog = false
                engine.resetBoard()
                refreshState()
            },
            onDismiss = {
                showVictoryDialog = false
            }
        )
    }

    // Match History Dialog
    if (showHistoryDialog) {
        HistoryDialog(
            history = engine.matchHistory,
            onDismiss = {
                showHistoryDialog = false
            }
        )
    }
}
