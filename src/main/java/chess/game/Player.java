package chess.game;

import chess.Main;
import chess.pieces.Color;

public abstract class Player {
    protected Color color;

    public Player(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Error: Player color is null");
        }
        if (color == Color.NOBODY) {
            throw new IllegalArgumentException("Error: Player color is nobody");
        }
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract Move getPlayerMove(Board game, Main ui);
}
