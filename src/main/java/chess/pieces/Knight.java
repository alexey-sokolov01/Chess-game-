package chess.pieces;

import chess.game.Board;
import chess.game.Move;
import chess.game.Position;

public class Knight extends Piece {

    public Knight(Color color, int row, int col, Board game) {
        super(color, row, col, game);
    }

    public Knight(Color color, Position pos, Board game) {
        super(color, pos, game);
    }

    @Override
    public void updatePossibleMoves() {
        possibleMoves.clear();
        attackedFields.clear();

        int row = pos.getRow();
        int col = pos.getCol();

        int r;
        int c;

        for (int i = 0; i < 8; ++i) {
            switch (i) {
                case 0 -> {r = 2; c = 1;}
                case 1 -> {r = 2; c = -1;}
                case 2 -> {r = -2; c = 1;}
                case 3 -> {r = -2; c = -1;}
                case 4 -> {r = 1; c = 2;}
                case 5 -> {r = -1; c = 2;}
                case 6 -> {r = 1; c = -2;}
                case 7 -> {r = -1; c = -2;}
                default -> throw new IllegalStateException("i stops at 7");
            };
            
            if (row + r >= 0 && row + r <= 7 && col + c >= 0 && col + c <= 7) {
                Position checkedPos = new Position(row + r, col + c);
                if (!game.getField(checkedPos).isOccupied() || game.getPiece(checkedPos).getColor() == color.flip()) {
                    Move move = new Move(this, pos, checkedPos);
                    possibleMoves.add(move);
                    game.addPossibleMove(color, move);
                }
                attackedFields.add(checkedPos);
                game.setFieldAttacked(checkedPos, color);
            }
        }
    }
    
    @Override
    public PieceType getPieceType() {
        return PieceType.KNIGHT;
    }

    @Override
    public Piece copySpecificPiece(Board gameCopy) {
        return new Knight(color, pos.copyPos(), gameCopy);
    }
}
