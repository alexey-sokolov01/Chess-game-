package chess.game;

import chess.pieces.Piece;

public class Move {
    private final Piece piece;
    private final Position from;
    private final Position to;

    public Move(Piece piece, Position from, Position to) {
        this.piece = piece;
        this.from = from;
        if (to.getRow() < 0 || to.getRow() > 7 || to.getCol() < 0 || to.getRow() > 7) {
            throw new IllegalArgumentException("Error: Move position out of bounds");
        }
        this.to = to;
    }

    public Move(Piece piece, Position from, int row, int col) {
        this.piece = piece;
        this.from = from;
        if (from.getRow() < 0 || from.getRow() > 7 || from.getCol() < 0 || from.getRow() > 7) {
            throw new IllegalArgumentException("Error: Move position out of bounds");
        }
        this.to = new Position(row, col);
    }

    public Piece getPiece() {
        return piece;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Move copyMove(Piece copyPiece) {
        return new Move(copyPiece, from.copyPos(), to.copyPos());
    }
    
    @Override
    public int hashCode() {
        int res = 17;
        res = 31 * res + piece.hashCode();
        res = 31 * res + from.hashCode();
        res = 31 * res + to.hashCode();
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move other)) {
            return false;
        }
        return piece.equals(other.getPiece()) && from.equals(other.getFrom()) && to.equals(other.getTo());
    }

    @Override
    public String toString() {
        return "MOVE: " + piece.toString().toUpperCase() + " " + from.toString() + " -> " + to.toString();
    }
}
