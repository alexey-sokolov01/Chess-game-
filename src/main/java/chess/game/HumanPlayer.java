package chess.game;

import chess.Main;
import chess.pieces.Color;

public class HumanPlayer extends Player {

    public HumanPlayer(Color color) {
        super(color);
    }

    @Override
    public Move getPlayerMove(Board game, Main ui) {
        return ui.getUserMove(color);
    }
    
}
