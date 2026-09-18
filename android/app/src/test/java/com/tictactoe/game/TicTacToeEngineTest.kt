package com.tictactoe.game

import com.tictactoe.game.engine.AiDifficulty
import com.tictactoe.game.engine.GameMode
import com.tictactoe.game.engine.TicTacToeEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TicTacToeEngineTest {
    private lateinit var engine: TicTacToeEngine

    @Before
    fun setUp() {
        engine = TicTacToeEngine(context = null)
        engine.resetBoard()
    }

    @Test
    fun testInitialState() {
        assertEquals("X", engine.currentPlayer)
        assertFalse(engine.isGameOver)
        assertEquals(null, engine.winner)
        assertEquals(0, engine.moveHistory.size)
    }

    @Test
    fun testValidMoveSequence() {
        assertTrue(engine.makeMove(0, 0)) // X
        assertEquals("O", engine.currentPlayer)
        assertTrue(engine.makeMove(1, 1)) // O
        assertEquals("X", engine.currentPlayer)
        assertFalse(engine.makeMove(0, 0)) // Already occupied
    }

    @Test
    fun testRowWinDetection() {
        engine.gameMode = GameMode.PVP
        engine.makeMove(0, 0) // X
        engine.makeMove(1, 0) // O
        engine.makeMove(0, 1) // X
        engine.makeMove(1, 1) // O
        engine.makeMove(0, 2) // X wins top row

        assertTrue(engine.isGameOver)
        assertEquals("X", engine.winner)
        assertNotNull(engine.winningLine)
        assertEquals(1, engine.scoresX)
    }

    @Test
    fun testMinimaxAiProducesValidMove() {
        engine.gameMode = GameMode.PVE
        engine.aiDifficulty = AiDifficulty.HARD
        engine.makeMove(0, 0) // X takes top-left

        val aiMove = engine.getBestAiMove()
        assertNotNull(aiMove)
        assertTrue(aiMove!!.first in 0..2)
        assertTrue(aiMove.second in 0..2)
        assertTrue(engine.board[aiMove.first][aiMove.second].isEmpty())
    }

    @Test
    fun testUndoMove() {
        engine.gameMode = GameMode.PVP
        engine.makeMove(0, 0) // X
        engine.makeMove(1, 1) // O
        assertEquals(2, engine.moveHistory.size)

        assertTrue(engine.undoLastMove())
        assertEquals(1, engine.moveHistory.size)
        assertEquals("O", engine.currentPlayer)
        assertTrue(engine.board[1][1].isEmpty())
    }
}
