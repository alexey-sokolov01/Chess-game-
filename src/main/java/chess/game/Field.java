package chess.game;

import chess.pieces.Color;
import chess.pieces.Empty;
import chess.pieces.Piece;

public class Field {
    private boolean attackedByWhite = false;
    private boolean attackedByBlack = false;
    private Piece piece;

    public Field(Piece piece) {
        setPiece(piece);
    }
    
    public boolean isOccupied() {
        return !piece.equals(new Empty());
    }

    public boolean isAttacked(Color color) {
        return color == Color.WHITE ? attackedByWhite : attackedByBlack;
    }

    public void setAttacked(Color color) {
        if (color == Color.NOBODY) {
            attackedByBlack = false;
            attackedByWhite = false;
        }
        if (color == Color.WHITE) {
            attackedByWhite = true;
        }
        if (color == Color.BLACK) {
            attackedByBlack = true;
        }
    }

    public void clearAttacked() {
        attackedByWhite = false;
        attackedByBlack = false;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void setField(Color attackedBy, Piece pieceOn) {
        setAttacked(attackedBy);
        setPiece(pieceOn);
    }

    public Field copyField() {
        Field copyField = new Field(piece);
        if (attackedByWhite) {
            copyField.setAttacked(Color.WHITE);
        }
        if (attackedByBlack) {
            copyField.setAttacked(Color.BLACK);
        }
        return copyField;
    }
}
