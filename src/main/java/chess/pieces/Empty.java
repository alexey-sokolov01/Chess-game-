package chess.pieces;

import chess.game.Board;

public class Empty extends Piece{

    public Empty() {
        super(Color.NOBODY, null);
    }

	@Override
	public void updatePossibleMoves() {
		return;
	}

	@Override
	public PieceType getPieceType() {
		return PieceType.EMPTY;
	}

	@Override
	public Piece copySpecificPiece(Board gameCopy) {
		return this;
	}

    @Override
    public int hashCode() {
        int res = 17;
        res = 31 * res + getPieceType().hashCode();
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Empty;
    }
    
}
