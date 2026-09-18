import math
import random
from datetime import datetime
from typing import Dict, List, Optional, Tuple


class TicTacToeEngine:
    def __init__(self):
        self.board: List[List[str]] = [["" for _ in range(3)] for _ in range(3)]
        self.current_player: str = "X"
        self.game_mode: str = "PvP"  # "PvP" or "PvE"
        self.ai_difficulty: str = "Hard"  # "Easy", "Medium", "Hard"
        self.scores: Dict[str, int] = {"X": 0, "O": 0, "Draws": 0}
        self.move_history: List[Dict[str, any]] = []
        self.is_game_over: bool = False
        self.winner: Optional[str] = None
        self.winning_line: Optional[List[Tuple[int, int]]] = None
        self._cache: Dict[Tuple[Tuple[Tuple[str, ...], ...], bool], int] = {}

    def reset_board(self) -> None:
        """Resets the board for a new round."""
        self.board = [["" for _ in range(3)] for _ in range(3)]
        self.current_player = "X"
        self.move_history.clear()
        self.is_game_over = False
        self.winner = None
        self.winning_line = None
        self._cache.clear()

    def make_move(self, row: int, col: int) -> bool:
        """Places the current player's mark at (row, col) if valid."""
        if self.is_game_over or self.board[row][col] != "":
            return False

        self.board[row][col] = self.current_player
        self.move_history.append({
            "turn": len(self.move_history) + 1,
            "player": self.current_player,
            "row": row,
            "col": col,
            "time": datetime.now().strftime("%H:%M:%S")
        })

        # Check for game end
        winner, line = self.check_winner()
        if winner:
            self.is_game_over = True
            self.winner = winner
            self.winning_line = line
            if winner in self.scores:
                self.scores[winner] += 1
        elif self.is_board_full():
            self.is_game_over = True
            self.winner = "Draw"
            self.scores["Draws"] += 1
        else:
            self.current_player = "O" if self.current_player == "X" else "X"

        return True

    def undo_last_move(self) -> bool:
        """Undoes the last move (or two moves if playing against AI)."""
        if not self.move_history:
            return False

        steps_to_undo = 2 if (
            self.game_mode == "PvE" and len(self.move_history) >= 2 and not self.is_game_over
        ) else 1
        for _ in range(steps_to_undo):
            if self.move_history:
                last_move = self.move_history.pop()
                self.board[last_move["row"]][last_move["col"]] = ""
                self.current_player = last_move["player"]

        self.is_game_over = False
        self.winner = None
        self.winning_line = None
        self._cache.clear()
        return True

    def check_winner(self) -> Tuple[Optional[str], Optional[List[Tuple[int, int]]]]:
        """Evaluates rows, columns, and diagonals for a 3-in-a-row winner."""
        # Rows
        for r in range(3):
            if self.board[r][0] == self.board[r][1] == self.board[r][2] != "":
                return self.board[r][0], [(r, 0), (r, 1), (r, 2)]

        # Columns
        for c in range(3):
            if self.board[0][c] == self.board[1][c] == self.board[2][c] != "":
                return self.board[0][c], [(0, c), (1, c), (2, c)]

        # Diagonals
        if self.board[0][0] == self.board[1][1] == self.board[2][2] != "":
            return self.board[0][0], [(0, 0), (1, 1), (2, 2)]
        if self.board[0][2] == self.board[1][1] == self.board[2][0] != "":
            return self.board[0][2], [(0, 2), (1, 1), (2, 0)]

        return None, None

    def is_board_full(self) -> bool:
        return all(self.board[r][c] != "" for r in range(3) for c in range(3))

    def get_empty_cells(self) -> List[Tuple[int, int]]:
        return [(r, c) for r in range(3) for c in range(3) if self.board[r][c] == ""]

    # -------------------------------------------------------------
    # AI OPPONENT (INSTANT MEMOIZED MINIMAX WITH ALPHA-BETA PRUNING)
    # -------------------------------------------------------------
    def get_best_ai_move(self) -> Optional[Tuple[int, int]]:
        """Calculates AI move with zero delay and optimal play."""
        empty_cells = self.get_empty_cells()
        if not empty_cells:
            return None

        if self.ai_difficulty == "Easy":
            return random.choice(empty_cells)
        elif self.ai_difficulty == "Medium" and random.random() < 0.35:
            return random.choice(empty_cells)

        # Fast opening moves (0ms lookup)
        if len(empty_cells) == 9:
            return (1, 1)
        if len(empty_cells) == 8:
            if self.board[1][1] == "":
                return (1, 1)
            return (0, 0)

        # Hard: Optimal Minimax with Transposition Memoization
        best_score = -math.inf
        best_move = None

        for (r, c) in empty_cells:
            self.board[r][c] = "O"
            score = self._minimax(depth=0, is_maximizing=False, alpha=-math.inf, beta=math.inf)
            self.board[r][c] = ""
            if score > best_score:
                best_score = score
                best_move = (r, c)

        return best_move or random.choice(empty_cells)

    def _minimax(self, depth: int, is_maximizing: bool, alpha: float, beta: float) -> int:
        winner, _ = self.check_winner()
        if winner == "O":
            return 10 - depth
        elif winner == "X":
            return depth - 10
        elif self.is_board_full():
            return 0

        # State memoization key
        state_key = (tuple(tuple(row) for row in self.board), is_maximizing)
        if state_key in self._cache:
            return self._cache[state_key]

        if is_maximizing:
            max_eval = -math.inf
            for (r, c) in self.get_empty_cells():
                self.board[r][c] = "O"
                ev = self._minimax(depth + 1, False, alpha, beta)
                self.board[r][c] = ""
                max_eval = max(max_eval, ev)
                alpha = max(alpha, ev)
                if beta <= alpha:
                    break
            self._cache[state_key] = max_eval
            return max_eval
        else:
            min_eval = math.inf
            for (r, c) in self.get_empty_cells():
                self.board[r][c] = "X"
                ev = self._minimax(depth + 1, True, alpha, beta)
                self.board[r][c] = ""
                min_eval = min(min_eval, ev)
                beta = min(beta, ev)
                if beta <= alpha:
                    break
            self._cache[state_key] = min_eval
            return min_eval
