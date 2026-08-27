package chess.pieces;

import chess.game.Board;
import chess.game.Field;
import chess.game.Move;
import chess.game.Position;

public class Pawn extends Piece {
    private int direction;
    private int startRow;

    public Pawn(Color color, int row, int col, Board game) {
        super(color, row, col, game);
        direction = this.color == Color.WHITE ? 1 : -1;
        startRow = this.color == Color.WHITE ? 1 : 6;
    }

    public Pawn(Color color, Position pos, Board game) {
        super(color, pos, game);
        direction = this.color == Color.WHITE ? 1 : -1;
        startRow = this.color == Color.WHITE ? 1 : 6;
    }

    @Override
    public void updatePossibleMoves() {
        possibleMoves.clear();
        attackedFields.clear();

        int row = pos.getRow();
        int col = pos.getCol();

        if (row + direction < 8 && row + direction >= 0) {

            Position oneForward = new Position(row + direction, col);
            if (!game.getField(oneForward).isOccupied()) {
                Move move = new Move(this, pos, oneForward);
                possibleMoves.add(move);
                game.addPossibleMove(color, move);
            }

            if (col > 0) {
                Position takeLeft = new Position(row + direction, col - 1);
                Field takeLeftField = game.getField(takeLeft);
                takeLeftField.setAttacked(color);
                attackedFields.add(takeLeft);
                game.setFieldAttacked(takeLeft, color);
                if (takeLeftField.isOccupied() && takeLeftField.getPiece().getColor() == color.flip()) {
                    Move move = new Move(this, pos, takeLeft);
                    possibleMoves.add(move);
                    game.addPossibleMove(color, move);
                }
            }
            
            if (col < 7) {
                Position takeRight = new Position(row + direction, col + 1);
                Field takeRightField = game.getField(takeRight);
                takeRightField.setAttacked(color);
                attackedFields.add(takeRight);
                game.setFieldAttacked(takeRight, color);
                if (takeRightField.isOccupied() && takeRightField.getPiece().getColor() == color.flip()) {
                    Move move = new Move(this, pos, takeRight);
                    possibleMoves.add(move);
                    game.addPossibleMove(color, move);
                }
            }

            if (row == startRow) {
                Position twoForward = new Position(row + 2 * direction, col);
                if (!game.getField(twoForward).isOccupied() && !game.getField(oneForward).isOccupied()) {
                    Move move = new Move(this, pos, twoForward);
                    possibleMoves.add(move);
                    game.addPossibleMove(color, move);
                }
            }

            if (row == startRow + 3 * direction) {
                checkEnPassant();
            }
        }
    }

    private void checkEnPassant() {
        Move lastMove = game.getLog().getLast();
        Piece lastPiece = lastMove.getPiece();
        int lastPieceCol = lastMove.getTo().getCol();
        if (lastPiece.getPieceType() == PieceType.PAWN 
                && lastMove.getTo().getRow() - lastMove.getFrom().getRow() == (-2) * direction
                && (lastPieceCol == pos.getCol() + 1 || lastPieceCol == pos.getCol() - 1)) {
            Move move = new Move(this, pos, pos.getRow() + direction, lastPieceCol);
            possibleMoves.add(move);
            game.addPossibleMove(color, move);
            
        }
    }

    @Override
    public boolean gettingPromoted() {
        int promotionRow = color == Color.WHITE ? 7 : 0;
        return pos.getRow() == promotionRow;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
    }

    @Override
    public Piece copySpecificPiece(Board gameCopy) {
        return new Pawn(color, pos.copyPos(), gameCopy);
    }
}
