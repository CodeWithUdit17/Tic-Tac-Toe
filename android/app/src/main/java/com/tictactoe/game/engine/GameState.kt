package com.tictactoe.game.engine

enum class GameMode(val displayName: String) {
    PVE("vs AI"),
    PVP("2 Players")
}

enum class AiDifficulty(val displayName: String, val description: String) {
    EASY("Casual", "Random & relaxed"),
    MEDIUM("Tactical", "Smart with occasional slips"),
    HARD("Unbeatable", "Flawless Minimax AI")
}

data class MoveRecord(
    val turn: Int,
    val player: String,
    val row: Int,
    val col: Int,
    val time: String
)

data class MatchRecord(
    val id: String,
    val winner: String, // "X", "O", "Draw"
    val totalMoves: Int,
    val date: String,
    val mode: String,
    val difficulty: String
)

data class WinningLine(
    val r1: Int, val c1: Int,
    val r2: Int, val c2: Int,
    val r3: Int, val c3: Int
)
