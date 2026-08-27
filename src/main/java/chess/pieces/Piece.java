package chess.pieces;

import java.util.ArrayList;
import java.util.List;

import chess.game.Board;
import chess.game.Move;
import chess.game.Position;

public abstract class Piece {
    protected final Board game;
    protected final Color color;    
    protected Position pos;
    protected List<Move> possibleMoves = new ArrayList<Move>();
    protected List<Position> attackedFields = new ArrayList<Position>();
    protected boolean inGame = true;
    protected boolean hasMoved = false;


    public Piece(Color color, int row, int col, Board game) {
        this.color = color;
        this.pos = new Position(row, col);
        this.game = game;
    }

    public Piece(Color color, Position pos, Board game) {
        this.color = color;
        this.pos = pos;
        this.game = game;
    }

    protected Piece(Color color, Board game) {
        this.color = color;
        this.game = game;
    }

	public Color getColor() {
        return color;
    }

    public Position getPos() {
        return pos;
    }

    public void setPosition(int row, int col) {
        pos.setRow(row);
        pos.setCol(col);
    }

    public void setPosition(Position pos) {
        this.pos = pos;
    }

    public List<Move> getPossibleMoves() {
        return new ArrayList<>(possibleMoves);
    }

    public abstract void updatePossibleMoves();

    public void removePossibleMove(Move move) {
        possibleMoves.remove(move);
    }

    private void setPossibleMoves(List<Move> possibleMoves) {
        this.possibleMoves = new ArrayList<>(possibleMoves);
    }

    public List<Position> getAttackedFields() {
        return new ArrayList<>(attackedFields);
    }

    public boolean isInGame() {
        return inGame;
    }

    public void takePiece() {
        inGame = false;
        possibleMoves.clear();
    }

    public boolean hasMovedYet() {
        return hasMoved;
    }

    public void moved() {
        hasMoved = true;
    }

    public abstract PieceType getPieceType();

    public Piece copyPiece(Board gameCopy) {
        Piece piece = copySpecificPiece(gameCopy);
        piece.setPossibleMoves(possibleMoves);
        if (!isInGame()) {
            piece.takePiece();
        }
        if (hasMoved) {
            piece.moved();
        }
        return piece;
    }

    public abstract Piece copySpecificPiece(Board gameCopy);

    public boolean gettingPromoted() {
        return false;
    }

    @Override 
    public int hashCode() {
        int res = 17;
        res = 31 * res + getPieceType().hashCode();
        res = 31 * res + color.hashCode();
        res = 31 * res + pos.hashCode();
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Piece other)) {
            return false;
        }
        return other.getPieceType() == getPieceType()
                && other.getColor() == color
                && pos.equals(other.getPos());
    }

    @Override
    public String toString() {
        return getPieceType().toString();
    }
}
