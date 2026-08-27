package chess.game;

public class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        setRow(row);
        setCol(col);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) throws IllegalArgumentException {
        if (row < 0 || row > 7) {
            throw new IllegalArgumentException("Error: row out of bounds");
        }
        this.row = row;
    }

    public void setCol(int col) throws IllegalArgumentException {
        if (col < 0 ||col > 7) {
            throw new IllegalArgumentException("Error: column out of bounds");
        }
        this.col = col;
    }


    public Position copyPos() {
        return new Position(row, col);
    }

    @Override
    public int hashCode() {
        int res = 17;
        res = 31 * res + row;
        res = 31 * res + col;
        return res;
    }

    @Override 
    public boolean equals(Object obj) {
        if (!(obj instanceof Position other)) {
            return false;
        }
        return other.getRow() == row && other.getCol() == col;
    }

    @Override
    public String toString() {
        char letter = (char) ('a' + col);
        int number = row + 1;
        return "" + letter + number;
    }
}
