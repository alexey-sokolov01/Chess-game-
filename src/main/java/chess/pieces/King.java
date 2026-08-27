package chess.pieces;

import chess.game.Board;
import chess.game.Field;
import chess.game.Move;
import chess.game.Position;

public class King extends Piece {

    public King(Color color, int row, int col, Board game) {
        super(color, row, col, game);
    }

    public King(Color color, Position pos, Board game) {
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
                case 0 -> {r = -1; c = -1;}
                case 1 -> {r = -1; c = 1;}
                case 2 -> {r = 1; c = -1;}
                case 3 -> {r = 1; c = 1;}
                case 4 -> {r = 0; c = -1;}
                case 5 -> {r = 0; c = 1;}
                case 6 -> {r = -1; c = 0;}
                case 7 -> {r = 1; c = 0;}
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

        if (!hasMoved && !isChecked()) {
            checkCastling();
        }
    }

    private void checkCastling() {
        int row = pos.getRow();
        Field rook1Field = game.getField(new Position(row, 7));
        Field rook2Field = game.getField(new Position(row, 0));

        if (rook1Field.isOccupied() && rook1Field.getPiece().getPieceType() == PieceType.ROOK) {
            Rook rook1 = (Rook) rook1Field.getPiece();
            if (!rook1.hasMovedYet() && rook1.getColor() == color
                    && !game.getField(row, 6).isOccupied() && !game.getField(row, 6).isAttacked(color.flip())
                    && !game.getField(row, 5).isOccupied() && !game.getField(row, 5).isAttacked(color.flip())) {
                Move move = new Move(this, pos, new Position(row, 6));
                possibleMoves.add(move);
                game.addPossibleMove(color, move);
                }
        }

        if (rook2Field.isOccupied() && rook2Field.getPiece().getPieceType() == PieceType.ROOK) {
            Rook rook2 = (Rook) rook2Field.getPiece();
            if (!rook2.hasMovedYet() && rook2.getColor() == color
                    && !game.getField(row, 2).isOccupied() && !game.getField(row, 2).isAttacked(color.flip())
                    && !game.getField(row, 3).isOccupied() && !game.getField(row, 3).isAttacked(color.flip())
                    && !game.getField(row, 1).isOccupied()) {
                Move move = new Move(this, pos, new Position(row, 2));
                possibleMoves.add(move);
                game.addPossibleMove(color, move);
                }
        }
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.KING;
    }
    
    public boolean isChecked() {
        return game.getField(pos).isAttacked(color.flip());
    }

    @Override
    public Piece copySpecificPiece(Board gameCopy) {
        King copyKing = new King(color, pos.copyPos(), gameCopy);
        if (hasMovedYet()) {
            copyKing.moved();
        }
        return copyKing;
    }
}
