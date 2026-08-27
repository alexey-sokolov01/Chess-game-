package chess.pieces;

import chess.game.Board;
import chess.game.Move;
import chess.game.Position;

public class Rook extends Piece {

    public Rook(Color color, int row, int col, Board game) {
        super(color, row, col, game);
    }

    public Rook(Color color, Position pos, Board game) {
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

        for (int i = 0; i < 4; ++i) {
            switch (i) {
                case 0 -> {r = 0; c = -1;}
                case 1 -> {r = 0; c = 1;}
                case 2 -> {r = -1; c = 0;}
                case 3 -> {r = 1; c = 0;}
                default -> throw new IllegalStateException("i stops at 3");
            };

            int stepR = r;
            int stepC = c;

            while (row + r <= 7 && row + r >= 0 && col + c <= 7 && col + c >= 0) {

                Position checkedPos = new Position(row + r, col + c);
                if (game.getField(checkedPos).isOccupied()) {
                    if (game.getPiece(checkedPos).getColor() == color.flip()) {
                        Move move = new Move(this, pos, checkedPos);
                        possibleMoves.add(move);
                        game.addPossibleMove(color, move);
                    }
                    r = 10; //stop while loop
                } else {
                    Move move = new Move(this, pos, checkedPos);
                    possibleMoves.add(move);
                    game.addPossibleMove(color, move);
                    r += stepR;
                    c += stepC;
                }
                attackedFields.add(checkedPos);
                game.setFieldAttacked(checkedPos, color);
            }
        }
    }
    
    @Override
    public PieceType getPieceType() {
        return PieceType.ROOK;
    }

    @Override
    public Piece copySpecificPiece(Board gameCopy) {
        return new Rook(color, pos.copyPos(), gameCopy);
    }
}
