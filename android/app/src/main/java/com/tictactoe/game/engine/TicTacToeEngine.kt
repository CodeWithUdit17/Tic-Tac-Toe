package com.tictactoe.game.engine

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class TicTacToeEngine(private val context: Context? = null) {
    var board: Array<Array<String>> = Array(3) { Array(3) { "" } }
    var currentPlayer: String = "X"
    var gameMode: GameMode = GameMode.PVE
    var aiDifficulty: AiDifficulty = AiDifficulty.HARD

    var scoresX: Int = 0
    var scoresO: Int = 0
    var scoresDraws: Int = 0

    var streakWinner: String = ""
    var streakCount: Int = 0

    var isGameOver: Boolean = false
    var winner: String? = null
    var winningLine: WinningLine? = null

    val moveHistory = mutableListOf<MoveRecord>()
    val matchHistory = mutableListOf<MatchRecord>()

    init {
        loadHistoryFromStorage()
    }

    fun resetBoard() {
        board = Array(3) { Array(3) { "" } }
        currentPlayer = "X"
        isGameOver = false
        winner = null
        winningLine = null
        moveHistory.clear()
    }

    fun resetScores() {
        scoresX = 0
        scoresO = 0
        scoresDraws = 0
        streakWinner = ""
        streakCount = 0
    }

    fun makeMove(row: Int, col: Int): Boolean {
        if (isGameOver || row !in 0..2 || col !in 0..2 || board[row][col].isNotEmpty()) {
            return false
        }

        board[row][col] = currentPlayer
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        moveHistory.add(MoveRecord(moveHistory.size + 1, currentPlayer, row, col, timeStr))

        val (winPlayer, line) = checkWinner(board)
        if (winPlayer != null) {
            isGameOver = true
            winner = winPlayer
            winningLine = line
            if (winPlayer == "X") scoresX++ else scoresO++

            if (streakWinner == winPlayer) {
                streakCount++
            } else {
                streakWinner = winPlayer
                streakCount = 1
            }
            recordMatch(winPlayer)
        } else if (isBoardFull(board)) {
            isGameOver = true
            winner = "Draw"
            scoresDraws++
            streakWinner = ""
            streakCount = 0
            recordMatch("Draw")
        } else {
            currentPlayer = if (currentPlayer == "X") "O" else "X"
        }

        return true
    }

    fun undoLastMove(): Boolean {
        if (moveHistory.isEmpty()) return false

        val steps = if (gameMode == GameMode.PVE && moveHistory.size >= 2 && !isGameOver) 2 else 1
        for (i in 0 until steps) {
            if (moveHistory.isNotEmpty()) {
                val last = moveHistory.removeAt(moveHistory.size - 1)
                board[last.row][last.col] = ""
                currentPlayer = last.player
            }
        }

        isGameOver = false
        winner = null
        winningLine = null
        return true
    }

    fun getEmptyCells(b: Array<Array<String>> = board): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        for (r in 0..2) {
            for (c in 0..2) {
                if (b[r][c].isEmpty()) list.add(Pair(r, c))
            }
        }
        return list
    }

    fun checkWinner(b: Array<Array<String>>): Pair<String?, WinningLine?> {
        // Rows
        for (r in 0..2) {
            if (b[r][0].isNotEmpty() && b[r][0] == b[r][1] && b[r][1] == b[r][2]) {
                return Pair(b[r][0], WinningLine(r, 0, r, 1, r, 2))
            }
        }
        // Columns
        for (c in 0..2) {
            if (b[0][c].isNotEmpty() && b[0][c] == b[1][c] && b[1][c] == b[2][c]) {
                return Pair(b[0][c], WinningLine(0, c, 1, c, 2, c))
            }
        }
        // Diagonals
        if (b[0][0].isNotEmpty() && b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            return Pair(b[0][0], WinningLine(0, 0, 1, 1, 2, 2))
        }
        if (b[0][2].isNotEmpty() && b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            return Pair(b[0][2], WinningLine(0, 2, 1, 1, 2, 0))
        }

        return Pair(null, null)
    }

    fun isBoardFull(b: Array<Array<String>>): Boolean {
        for (r in 0..2) {
            for (c in 0..2) {
                if (b[r][c].isEmpty()) return false
            }
        }
        return true
    }

    // -------------------------------------------------------------
    // MINIMAX AI WITH ALPHA-BETA PRUNING
    // -------------------------------------------------------------
    fun getBestAiMove(): Pair<Int, Int>? {
        val emptyCells = getEmptyCells()
        if (emptyCells.isEmpty()) return null

        when (aiDifficulty) {
            AiDifficulty.EASY -> return emptyCells.random()
            AiDifficulty.MEDIUM -> {
                // 35% chance to make random move, else smart move
                if (Random.nextFloat() < 0.35f) {
                    return emptyCells.random()
                }
            }
            AiDifficulty.HARD -> { /* Optimal minimax */ }
        }

        var bestScore = Int.MIN_VALUE
        var bestMove: Pair<Int, Int>? = null

        for (cell in emptyCells) {
            board[cell.first][cell.second] = "O"
            val score = minimax(0, isMaximizing = false, alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE)
            board[cell.first][cell.second] = ""
            if (score > bestScore) {
                bestScore = score
                bestMove = cell
            }
        }

        return bestMove ?: emptyCells.random()
    }

    private fun minimax(depth: Int, isMaximizing: Boolean, alpha: Int, beta: Int): Int {
        var a = alpha
        var b = beta
        val (winPlayer, _) = checkWinner(board)
        if (winPlayer == "O") return 10 - depth
        if (winPlayer == "X") return depth - 10
        if (isBoardFull(board)) return 0

        val emptyCells = getEmptyCells()

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (cell in emptyCells) {
                board[cell.first][cell.second] = "O"
                val eval = minimax(depth + 1, false, a, b)
                board[cell.first][cell.second] = ""
                maxEval = maxOf(maxEval, eval)
                a = maxOf(a, eval)
                if (b <= a) break
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (cell in emptyCells) {
                board[cell.first][cell.second] = "X"
                val eval = minimax(depth + 1, true, a, b)
                board[cell.first][cell.second] = ""
                minEval = minOf(minEval, eval)
                b = minOf(b, eval)
                if (b <= a) break
            }
            return minEval
        }
    }

    // -------------------------------------------------------------
    // PERSISTENCE (MATCH HISTORY)
    // -------------------------------------------------------------
    private fun recordMatch(win: String) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val record = MatchRecord(
            id = System.currentTimeMillis().toString(),
            winner = win,
            totalMoves = moveHistory.size,
            date = dateStr,
            mode = gameMode.displayName,
            difficulty = if (gameMode == GameMode.PVE) aiDifficulty.displayName else "N/A"
        )
        matchHistory.add(0, record)
        if (matchHistory.size > 50) {
            matchHistory.removeAt(matchHistory.size - 1)
        }
        saveHistoryToStorage()
    }

    private fun saveHistoryToStorage() {
        if (context == null) return
        try {
            val jsonArray = JSONArray()
            for (m in matchHistory) {
                val obj = JSONObject()
                obj.put("id", m.id)
                obj.put("winner", m.winner)
                obj.put("totalMoves", m.totalMoves)
                obj.put("date", m.date)
                obj.put("mode", m.mode)
                obj.put("difficulty", m.difficulty)
                jsonArray.put(obj)
            }
            val file = File(context.filesDir, "match_history.json")
            file.writeText(jsonArray.toString(2))
        } catch (_: Exception) {}
    }

    private fun loadHistoryFromStorage() {
        if (context == null) return
        try {
            val file = File(context.filesDir, "match_history.json")
            if (!file.exists()) return
            val text = file.readText()
            val jsonArray = JSONArray(text)
            matchHistory.clear()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                matchHistory.add(
                    MatchRecord(
                        id = obj.optString("id"),
                        winner = obj.optString("winner"),
                        totalMoves = obj.optInt("totalMoves"),
                        date = obj.optString("date"),
                        mode = obj.optString("mode"),
                        difficulty = obj.optString("difficulty")
                    )
                )
            }
        } catch (_: Exception) {}
    }
}
