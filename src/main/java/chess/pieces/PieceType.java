package chess.pieces;

public enum PieceType {
    PAWN("pawn"),
    KNIGHT("knight"),
    BISHOP("bishop"),
    ROOK("rook"),
    QUEEN("queen"),
    KING("king"),
    EMPTY("empty");

    private String name;

    PieceType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
