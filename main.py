import tkinter as tk
from tkinter import messagebox
from typing import Dict, Tuple

from game_engine import TicTacToeEngine
from xml_manager import XMLManager


class TicTacToeApp:
    def __init__(self, root: tk.Tk):
        self.root = root
        self.root.title("Tic-Tac-Toe (XML Powered)")
        self.root.geometry("460x650")
        self.root.resizable(False, False)

        # Load XML Configuration & Initialize Engine
        self.config = XMLManager.load_config()
        self.engine = TicTacToeEngine()
        self.engine.game_mode = self.config["default_mode"]
        self.engine.ai_difficulty = self.config["ai_difficulty"]

        # Theme Colors
        self.theme = self.config["theme"]
        self.root.configure(bg=self.theme["bg"])

        self.buttons: Dict[Tuple[int, int], tk.Button] = {}
        self.build_ui()
        self.update_ui()

    def build_ui(self) -> None:
        """Constructs widgets, header, grid buttons, and action toolbar."""
        # Top Header & Mode Toggle
        header_frame = tk.Frame(self.root, bg=self.theme["bg"], pady=10)
        header_frame.pack(fill="x")

        title = tk.Label(
            header_frame,
            text="TIC-TAC-TOE",
            font=(self.theme["font"], 20, "bold"),
            bg=self.theme["bg"],
            fg="#0F172A"
        )
        title.pack()

        # Mode Selection
        mode_frame = tk.Frame(header_frame, bg=self.theme["bg"], pady=5)
        mode_frame.pack()

        self.mode_btn = tk.Button(
            mode_frame,
            text=f"Mode: {self.engine.game_mode} (Click to Switch)",
            font=(self.theme["font"], 10, "bold"),
            bg="#E2E8F0",
            relief="flat",
            command=self.toggle_mode
        )
        self.mode_btn.pack(side="left", padx=5)

        # Scoreboard
        self.score_frame = tk.Frame(self.root, bg=self.theme["bg"], pady=5)
        self.score_frame.pack(fill="x")

        self.score_label = tk.Label(
            self.score_frame,
            text="",
            font=(self.theme["font"], 12, "bold"),
            bg=self.theme["bg"],
            fg="#334155"
        )
        self.score_label.pack()

        # Status Label (Turn / Winner)
        self.status_label = tk.Label(
            self.root,
            text="Player X's Turn",
            font=(self.theme["font"], 14, "bold"),
            bg=self.theme["bg"],
            fg=self.config["player_x"]["color"],
            pady=10
        )
        self.status_label.pack()

        # 3x3 Board Grid
        grid_frame = tk.Frame(self.root, bg=self.theme["grid"], padx=4, pady=4)
        grid_frame.pack()

        for r in range(3):
            for c in range(3):
                btn = tk.Button(
                    grid_frame,
                    text="",
                    font=(self.theme["font"], 28, "bold"),
                    width=4,
                    height=2,
                    bg=self.theme["btn_bg"],
                    activebackground=self.theme["bg"],
                    relief="flat",
                    command=lambda row=r, col=c: self.on_cell_clicked(row, col)
                )
                btn.grid(row=r, column=c, padx=3, pady=3)
                self.buttons[(r, c)] = btn

        # Bottom Action Bar (XML Save, Load, Undo, Reset)
        action_frame = tk.Frame(self.root, bg=self.theme["bg"], pady=20)
        action_frame.pack(fill="x", padx=20)

        btn_style = {"font": (self.theme["font"], 10), "width": 10, "relief": "groove"}

        tk.Button(action_frame, text="ðŸ’¾ Save XML", bg="#DCFCE7", command=self.save_game, **btn_style).grid(row=0,
column=0, padx=5, pady=5)
        tk.Button(action_frame, text="ðŸ“‚ Load XML", bg="#DBEAFE", command=self.load_game, **btn_style).grid(row=0,
column=1, padx=5, pady=5)
        tk.Button(action_frame, text="â†©ï¸ Undo", bg="#FEF3C7", command=self.undo_move, **btn_style).grid(row=0,
column=2, padx=5, pady=5)
        tk.Button(action_frame, text="ðŸ”„ New Round", bg="#F1F5F9", command=self.new_round, **btn_style).grid(row=0,
column=3, padx=5, pady=5)

    def on_cell_clicked(self, row: int, col: int) -> None:
        """Handles user clicking on a board tile."""
        if self.engine.is_game_over:
            return

        success = self.engine.make_move(row, col)
        if not success:
            return

        self.update_ui()

        # Handle Win / Draw
        if self.engine.is_game_over:
            self.handle_game_over()
            return

        # Trigger AI Move if in PvE Mode
        if self.engine.game_mode == "PvE" and self.engine.current_player == "O":
            self.root.after(300, self.make_ai_move)

    def make_ai_move(self) -> None:
        """Executes computer opponent turn."""
        if self.engine.is_game_over:
            return
        move = self.engine.get_best_ai_move()
        if move:
            self.engine.make_move(move[0], move[1])
            self.update_ui()
            if self.engine.is_game_over:
                self.handle_game_over()

    def update_ui(self) -> None:
        """Synchronizes GUI buttons and labels with Engine State."""
        # Update Buttons
        for (r, c), btn in self.buttons.items():
            val = self.engine.board[r][c]
            btn.config(text=val)
            if val == "X":
                btn.config(fg=self.config["player_x"]["color"])
            elif val == "O":
                btn.config(fg=self.config["player_o"]["color"])
            else:
                btn.config(bg=self.theme["btn_bg"])

        # Highlight Winning Line if game won
        if self.engine.winning_line:
            for (r, c) in self.engine.winning_line:
                self.buttons[(r, c)].config(bg="#BBF7D0")  # Light green highlight

        # Update Scoreboard
        p1 = self.config["player_x"]["name"]
        p2 = self.config["player_o"]["name"] if self.engine.game_mode == "PvP" else "AI"
        self.score_label.config(
            text=f"{p1} (X): {self.engine.scores['X']}   |   {p2} (O): "
            f"{self.engine.scores['O']}   |   Draws: {self.engine.scores['Draws']}"
        )

        # Update Turn Banner
        if not self.engine.is_game_over:
            curr = self.engine.current_player
            color = self.config["player_x"]["color"] if curr == "X" else self.config["player_o"]["color"]
            name = p1 if curr == "X" else p2
            self.status_label.config(text=f"{name}'s Turn ({curr})", fg=color)

    def handle_game_over(self) -> None:
        """Logs match history to XML and alerts players."""
        if self.engine.winner == "Draw":
            self.status_label.config(text="Game Over: It's a Draw!", fg="#64748B")
            messagebox.showinfo("Round Over", "It's a Draw!")
        else:
            winner_name = self.config["player_x"]["name"] if self.engine.winner == "X" else (
                self.config["player_o"]["name"] if self.engine.game_mode == "PvP" else "AI"
            )
            self.status_label.config(text=f"Winner: {winner_name} ({self.engine.winner})!", fg="#16A34A")
            messagebox.showinfo("Winner!", f"ðŸŽ‰ Congratulations {winner_name} ({self.engine.winner}) won!")

        # Log match to XML archive
        XMLManager.log_match_history(
            winner=self.engine.winner or "None",
            total_moves=len(self.engine.move_history),
            move_history=self.engine.move_history
        )

    # -------------------------------------------------------------
    # XML PERSISTENCE HANDLERS
    # -------------------------------------------------------------
    def save_game(self) -> None:
        """Saves active game to savegame.xml."""
        saved_file = XMLManager.save_game_state(
            board=self.engine.board,
            current_player=self.engine.current_player,
            game_mode=self.engine.game_mode,
            scores=self.engine.scores,
            move_history=self.engine.move_history
        )
        messagebox.showinfo("Saved to XML", f"Game state successfully exported to '{saved_file}'!")

    def load_game(self) -> None:
        """Loads game state from savegame.xml."""
        data = XMLManager.load_game_state()
        if not data:
            messagebox.showwarning("File Missing", "No 'savegame.xml' file found.")
            return

        self.engine.board = data["board"]
        self.engine.current_player = data["current_player"]
        self.engine.game_mode = data["game_mode"]
        self.engine.scores = data["scores"]
        self.engine.move_history = data["move_history"]
        self.engine.is_game_over = False
        self.engine.winner = None
        self.engine.winning_line = None

        self.mode_btn.config(text=f"Mode: {self.engine.game_mode} (Click to Switch)")
        self.update_ui()
        messagebox.showinfo("Loaded from XML", "Game state successfully restored from XML!")

    def undo_move(self) -> None:
        """Rolls back the last turn."""
        if self.engine.undo_last_move():
            self.update_ui()
        else:
            messagebox.showinfo("Undo", "No moves left to undo.")

    def new_round(self) -> None:
        """Clears board for a fresh game."""
        self.engine.reset_board()
        self.update_ui()

    def toggle_mode(self) -> None:
        """Switches between PvP (2 players) and PvE (vs Minimax AI)."""
        self.engine.game_mode = "PvE" if self.engine.game_mode == "PvP" else "PvP"
        self.mode_btn.config(text=f"Mode: {self.engine.game_mode} (Click to Switch)")
        self.new_round()


if __name__ == "__main__":
    root = tk.Tk()
    app = TicTacToeApp(root)
    root.mainloop()
