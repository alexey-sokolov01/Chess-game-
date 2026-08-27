package chess.pieces;

public enum Color {
    WHITE("white"),
    BLACK("black"),
    NOBODY ("nobody");

    private String name;

    Color(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public Color flip() {
        if (this == NOBODY) {
            throw new IllegalArgumentException("Cannot flip " + name);
        }
        if (this == WHITE) {
            return BLACK;
        }
        return WHITE;
    }
}
