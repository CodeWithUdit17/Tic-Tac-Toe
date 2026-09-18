import os
import xml.etree.ElementTree as ET
from xml.dom import minidom
from datetime import datetime
from typing import Any, Dict, List, Optional, Tuple


class XMLManager:
    CONFIG_FILE = "config.xml"
    SAVE_FILE = "savegame.xml"
    HISTORY_FILE = "match_history.xml"

    @staticmethod
    def prettify(elem: ET.Element) -> str:
        """Return a pretty-printed XML string with indentation."""
        rough_string = ET.tostring(elem, "utf-8")
        reparsed = minidom.parseString(rough_string)
        return reparsed.toprettyxml(indent="    ")

    # -------------------------------------------------------------
    # CONFIGURATION XML
    # -------------------------------------------------------------
    @classmethod
    def load_config(cls) -> Dict[str, Any]:
        """Loads configuration from config.xml or creates default if missing."""
        if not os.path.exists(cls.CONFIG_FILE):
            cls.create_default_config()

        tree = ET.parse(cls.CONFIG_FILE)
        root = tree.getroot()

        config = {
            "board_size": int(root.findtext("GameSettings/BoardSize", "3")),
            "default_mode": root.findtext("GameSettings/DefaultMode", "PvP"),
            "ai_difficulty": root.findtext("GameSettings/AiDifficulty", "Hard"),
            "player_x": {
                "name": root.findtext("Players/Player[@id='X']/Name", "Player 1"),
                "symbol": root.findtext("Players/Player[@id='X']/Symbol", "X"),
                "color": root.findtext("Players/Player[@id='X']/Color", "#2563EB"),
            },
            "player_o": {
                "name": root.findtext("Players/Player[@id='O']/Name", "Player 2"),
                "symbol": root.findtext("Players/Player[@id='O']/Symbol", "O"),
                "color": root.findtext("Players/Player[@id='O']/Color", "#DC2626"),
            },
            "theme": {
                "bg": root.findtext("Theme/BackgroundColor", "#F8FAFC"),
                "grid": root.findtext("Theme/GridColor", "#E2E8F0"),
                "btn_bg": root.findtext("Theme/ButtonBg", "#FFFFFF"),
                "font": root.findtext("Theme/FontFamily", "Helvetica"),
            }
        }
        return config

    @classmethod
    def create_default_config(cls) -> None:
        """Generates a default config.xml file."""
        root = ET.Element("TicTacToeConfig")

        settings = ET.SubElement(root, "GameSettings")
        ET.SubElement(settings, "BoardSize").text = "3"
        ET.SubElement(settings, "DefaultMode").text = "PvP"
        ET.SubElement(settings, "AiDifficulty").text = "Hard"

        players = ET.SubElement(root, "Players")
        px = ET.SubElement(players, "Player", id="X")
        ET.SubElement(px, "Name").text = "Player 1"
        ET.SubElement(px, "Symbol").text = "X"
        ET.SubElement(px, "Color").text = "#2563EB"

        po = ET.SubElement(players, "Player", id="O")
        ET.SubElement(po, "Name").text = "Player 2"
        ET.SubElement(po, "Symbol").text = "O"
        ET.SubElement(po, "Color").text = "#DC2626"

        theme = ET.SubElement(root, "Theme")
        ET.SubElement(theme, "BackgroundColor").text = "#F8FAFC"
        ET.SubElement(theme, "GridColor").text = "#E2E8F0"
        ET.SubElement(theme, "ButtonBg").text = "#FFFFFF"
        ET.SubElement(theme, "FontFamily").text = "Helvetica"

        with open(cls.CONFIG_FILE, "w", encoding="utf-8") as f:
            f.write(cls.prettify(root))

    # -------------------------------------------------------------
    # SAVE & LOAD GAME STATE XML
    # -------------------------------------------------------------
    @classmethod
    def save_game_state(
        cls,
        board: List[List[str]],
        current_player: str,
        game_mode: str,
        scores: Dict[str, int],
        move_history: List[Dict[str, Any]],
    ) -> str:
        """Serializes current game state and move history into XML."""
        root = ET.Element("SavedGame", timestamp=datetime.now().isoformat())

        # Metadata
        meta = ET.SubElement(root, "Metadata")
        ET.SubElement(meta, "CurrentPlayer").text = current_player
        ET.SubElement(meta, "GameMode").text = game_mode

        # Scores
        scores_elem = ET.SubElement(root, "Scores")
        ET.SubElement(scores_elem, "ScoreX").text = str(scores.get("X", 0))
        ET.SubElement(scores_elem, "ScoreO").text = str(scores.get("O", 0))
        ET.SubElement(scores_elem, "Draws").text = str(scores.get("Draws", 0))

        # Board Matrix
        board_elem = ET.SubElement(root, "Board", rows="3", cols="3")
        for r in range(3):
            for c in range(3):
                val = board[r][c]
                ET.SubElement(board_elem, "Cell", row=str(r), col=str(c)).text = val if val else ""

        # Move History
        history_elem = ET.SubElement(root, "MoveHistory")
        for move in move_history:
            ET.SubElement(
                history_elem,
                "Move",
                turn=str(move["turn"]),
                player=move["player"],
                row=str(move["row"]),
                col=str(move["col"]),
                time=move.get("time", "")
            )

        xml_data = cls.prettify(root)
        with open(cls.SAVE_FILE, "w", encoding="utf-8") as f:
            f.write(xml_data)
        return cls.SAVE_FILE

    @classmethod
    def load_game_state(cls) -> Optional[Dict[str, Any]]:
        """Deserializes saved game state from savegame.xml."""
        if not os.path.exists(cls.SAVE_FILE):
            return None

        tree = ET.parse(cls.SAVE_FILE)
        root = tree.getroot()

        current_player = root.findtext("Metadata/CurrentPlayer", "X")
        game_mode = root.findtext("Metadata/GameMode", "PvP")

        scores = {
            "X": int(root.findtext("Scores/ScoreX", "0")),
            "O": int(root.findtext("Scores/ScoreO", "0")),
            "Draws": int(root.findtext("Scores/Draws", "0")),
        }

        board = [["" for _ in range(3)] for _ in range(3)]
        for cell in root.findall("Board/Cell"):
            r = int(cell.attrib["row"])
            c = int(cell.attrib["col"])
            board[r][c] = cell.text or ""

        move_history = []
        for move in root.findall("MoveHistory/Move"):
            move_history.append({
                "turn": int(move.attrib["turn"]),
                "player": move.attrib["player"],
                "row": int(move.attrib["row"]),
                "col": int(move.attrib["col"]),
                "time": move.attrib.get("time", ""),
            })

        return {
            "current_player": current_player,
            "game_mode": game_mode,
            "scores": scores,
            "board": board,
            "move_history": move_history,
        }

    # -------------------------------------------------------------
    # LOG FINISHED MATCH TO XML ARCHIVE
    # -------------------------------------------------------------
    @classmethod
    def log_match_history(cls, winner: str, total_moves: int, move_history: List[Dict[str, Any]]) -> None:
        """Appends finished match telemetry to match_history.xml."""
        if os.path.exists(cls.HISTORY_FILE):
            tree = ET.parse(cls.HISTORY_FILE)
            root = tree.getroot()
        else:
            root = ET.Element("MatchLogArchive")
            tree = ET.ElementTree(root)

        match_elem = ET.SubElement(
            root,
            "Match",
            id=str(len(root) + 1),
            date=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            winner=winner,
            totalMoves=str(total_moves),
        )

        for m in move_history:
            ET.SubElement(
                match_elem,
                "Action",
                step=str(m["turn"]),
                player=m["player"],
                coordinate=f"({m['row']},{m['col']})",
            )

        with open(cls.HISTORY_FILE, "w", encoding="utf-8") as f:
            f.write(cls.prettify(root))
