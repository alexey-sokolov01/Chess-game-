package chess.game;

import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.PieceType;
import chess.pieces.Queen;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chess.Main;
import chess.pieces.Bishop;
import chess.pieces.Color;
import chess.pieces.Empty;
import chess.pieces.King;
import chess.pieces.Knight;


public class Board {
    private final Field[][] board = new Field[8][8];
    private final Piece[] pieces = new Piece[32];
    private final List<Move> log = new ArrayList<>();
    private List<Move> possibleMovesWhite = new ArrayList<>();
    private List<Move> possibleMovesBlack = new ArrayList<>();
    private King whiteKing;
    private King blackKing;
    private Color curPlayerColor;
    
    public Board() {
        setUpPieces();
    }

    public void run(Main ui, Player playerOne, Player playerTwo) {
        Player curPlayer = playerOne.getColor() == Color.WHITE ? playerOne : playerTwo;
        curPlayerColor = Color.WHITE;
        updateLegalMoves();

        while (!isGameOver()) {
            // List<Move> possibleMoves = curPlayerColor == Color.WHITE ? possibleMovesWhite : possibleMovesBlack;
            // System.out.println("Turn " + curPlayerColor.toString() + ", possible moves: " + possibleMoves);
            boolean moveDone = false;

            while (!moveDone) {
                Move move = curPlayer.getPlayerMove(this, ui);
                moveDone = makeMove(move);
            }

            checkEnPassant();
            checkCastling();
            if (checkPawnPromotion()) {
                Position pos = log.getLast().getTo();
                PieceType type = ui.getPromotionChoice(pos);
                promotePawn(pos, type);
            }

            ui.updateScreen(this);
            curPlayer = curPlayer == playerOne ? playerTwo : playerOne;
            setCurPlayerColor(curPlayer.getColor());
            updateLegalMoves();
        }

        String GameOverMessage;
        Color winner = getWinner();
        if (winner != Color.NOBODY) {
            GameOverMessage = winner.toString() + " won by checkmate";
        } else {
            GameOverMessage = "Draw by stalemate";
        }
        ui.showGameOverScreen(GameOverMessage);
    }

    private void setUpPieces() {
        int i = 0;
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                switch (row) {
                    case 6 -> { 
                        pieces[i] = new Pawn(Color.BLACK, row, col, this);
                        board[row][col] = new Field(pieces[i]);
                        ++i;
                    }
                    case 1 -> {
                        pieces[i] = new Pawn(Color.WHITE, row, col, this);
                        board[row][col] = new Field(pieces[i]);
                        ++i;
                    } 
                    case 7 -> {
                        pieces[i] = switch (col) {
                            case 0, 7 -> new Rook(Color.BLACK, row, col, this);
                            case 1, 6 -> new Knight(Color.BLACK, row, col, this);
                            case 2, 5 -> new Bishop(Color.BLACK, row, col, this);
                            case 3 -> new Queen(Color.BLACK, row, col, this);
                            default -> new King(Color.BLACK, row, col, this);
                            
                        };
                        board[row][col] = new Field(pieces[i]);
                        ++i;
                    } 
                    case 0 -> {
                        pieces[i] = switch(col) {
                        case 0, 7 -> new Rook(Color.WHITE, row, col, this);
                        case 1, 6 -> new Knight(Color.WHITE, row, col, this);
                        case 2,5 -> new Bishop(Color.WHITE, row, col, this);
                        case 3 -> new Queen(Color.WHITE, row, col, this);
                        default -> new King(Color.WHITE, row, col, this);
                        };
                        board[row][col] = new Field(pieces[i]);
                        ++i;
                    } 
                    default -> {
                        board[row][col] = new Field(new Empty());
                    }
                };
            }
        }
    }

    public Piece getPiece(Position pos) {
        return board[pos.getRow()][pos.getCol()].getPiece();
    }

    public Piece[] getAllPieces() {
        return Arrays.copyOf(pieces, pieces.length);
    }

    private void setPieceAt(int index, Piece piece) {
        pieces[index] = piece;
    } 

    public Field getField(Position pos) {
        return board[pos.getRow()][pos.getCol()];
    }

    public Field getField(int row, int col) {
        return getField(new Position(row, col)) ;
    }

    private void setFieldAt(int row, int col, Field field) {
        board[row][col].setField(Color.NOBODY, field.getPiece());
        Field fieldNew = board[row][col];
        if (field.isAttacked(Color.WHITE)) {
            fieldNew.setAttacked(Color.WHITE);
        }
        if (field.isAttacked(Color.BLACK)) {
            fieldNew.setAttacked(Color.BLACK);
        }
    }

    public List<Move> getLog() {
        return new ArrayList<>(log);
    }

    private void setLog(List<Move> log) {
        this.log.clear();
        for (Move move : log) {
            this.log.add(move);
        }
    }

    private int getPieceIndex(Piece piece) {
        for (int i = 0; i < pieces.length; ++i) {
            if (pieces[i] == piece) {
                return i;
            }
        }
        return -1;
    }

    public King getKing(Color color) {
        if (whiteKing == null || blackKing == null) {
            for (Piece piece : pieces) {
                if (piece.getPieceType() == PieceType.KING) {
                    if (piece.getColor() == Color.WHITE) {
                        whiteKing = (King) piece;
                    } else {
                        blackKing = (King) piece;
                    }
                }
            }
        }
        King king = color == Color.WHITE ? whiteKing : blackKing;
        return king;
    }

    private void promotePawn(Position pos, PieceType type) {
        Piece pawn = getPiece(pos);

        if (pawn == null || pawn.getPieceType() != PieceType.PAWN) {
            throw new IllegalArgumentException("No pawn at position");
        }
        
        int pieceIndex = getPieceIndex(pawn);


        Piece promotedPiece = switch (type) {
            case QUEEN -> new Queen(pawn.getColor(), pos, this);
            case ROOK -> new Rook(pawn.getColor(), pos, this);
            case BISHOP -> new Bishop(pawn.getColor(), pos, this);
            case KNIGHT -> new Knight(pawn.getColor(), pos, this);
            default -> throw new IllegalArgumentException("Invalid Promotion");
        };

        board[pos.getRow()][pos.getCol()].setPiece(promotedPiece);
        pieces[pieceIndex] = promotedPiece;
    }

    private boolean checkPawnPromotion() {
        Move lastMove = log.getLast();
        int row = lastMove.getTo().getRow();
        return lastMove.getPiece().getPieceType() == PieceType.PAWN
                && (row == 0 || row == 7);
    }

    private void checkEnPassant() {
        Move lastMove = log.getLast();
        Piece lastPiece = lastMove.getPiece();
        if (lastMove.getTo().getCol() != lastMove.getFrom().getCol()){
            
            Move penultimateMove = log.get(log.size() - 2);
            if (lastPiece.getPieceType() == PieceType.PAWN 
                    && penultimateMove.getPiece().getPieceType() == PieceType.PAWN
                    && (penultimateMove.getTo().getRow() - penultimateMove.getFrom().getRow()) % 2 == 0
                    && penultimateMove.getTo().getRow() == lastMove.getFrom().getRow()) {
                int direction = lastPiece.getColor() == Color.WHITE ? 1 : -1;
                Position pos = new Position(lastMove.getTo().getRow() - direction, lastMove.getTo().getCol());
                takePiece(pos);
                getField(pos).setPiece(new Empty());
            }
        }
    }

    private void checkCastling() {
        Move lastMove = log.getLast();
        int rowDifference = lastMove.getFrom().getCol() - lastMove.getTo().getCol();
        if (lastMove.getPiece().getPieceType() == PieceType.KING
                && (rowDifference == 2 || rowDifference == -2)) {
            
            Position fromRook;
            Position toRook;
            if (lastMove.getTo().getCol() == 6) {
                fromRook = new Position(lastMove.getTo().getRow(), 7);
                toRook = new Position(lastMove.getTo().getRow(), 5);
            } else {
                fromRook = new Position(lastMove.getTo().getRow(), 0);
                toRook = new Position(lastMove.getTo().getRow(), 3);
            }
            Piece rook = getPiece(fromRook);
            Field fromFieldRook = board[fromRook.getRow()][fromRook.getCol()];
            Field toFieldRook = board[toRook.getRow()][toRook.getCol()];

            fromFieldRook.setPiece(new Empty());
            toFieldRook.setPiece(rook);
            rook.setPosition(toRook);
        }
    }

    public boolean isMovePossible(Move move) {
        return move.getPiece().getPossibleMoves().contains(move);
    }

    public boolean makeMove(Move move) {
        if (!isMovePossible(move)) {
            return false;
        }
        Piece piece = move.getPiece();
        Position from = move.getFrom();
        Position to = move.getTo();
        Field fromField = board[from.getRow()][from.getCol()];
        Field toField = board[to.getRow()][to.getCol()];

        fromField.setPiece(new Empty());
        
        if (toField.isOccupied()) {
            takePiece(to);
        }
        toField.setPiece(piece);

        piece.setPosition(to);
        if (!piece.hasMovedYet()) {
            piece.moved();
        }

        log.add(move);
        return true;
    }

    private void takePiece(Position pos) {
        getPiece(pos).takePiece();
    }

    private void updateLegalMoves() {
        clearPossibleMovesWhite();
        clearPossibleMovesBlack();
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                clearAttackedField(new Position(row, col));;
            }
        }
        for (Piece piece : pieces) {
            if (piece.isInGame()) {
                piece.updatePossibleMoves();
                for (Move move : piece.getPossibleMoves()) {
                    Board simulator = copyGame();
                    Piece simulatorPiece = getSamePiece(simulator, piece);
                    simulator.makeMove(move.copyMove(simulatorPiece));

                    for (int row = 0; row < 8; ++row) {
                        for (int col = 0; col < 8; ++col) {
                            simulator.clearAttackedField(new Position(row, col));;
                        }
                    }

                    for (Piece simulationPiece : simulator.getAllPieces()) {
                        if (simulationPiece.isInGame()) {
                            simulationPiece.updatePossibleMoves();
                            // System.out.println("Possible moves of " + simulationPiece.getColor().toString() + " " + simulationPiece.toString() + ": " + simulationPiece.getPossibleMoves());
                        }
                    }

                    if (simulator.isChecked(piece.getColor())) {
                        piece.removePossibleMove(move);
                        List<Move> localList = piece.getColor() == Color.WHITE ? possibleMovesWhite : possibleMovesBlack;
                        localList.remove(move);
                    }
                }
            }
        }
        //check again for the kings because of newly set attacked fields
        //too lazy to solve efficiently
        for (Piece piece : pieces) {
            if (piece.getPieceType() == PieceType.KING) {
                piece.updatePossibleMoves();
                for (Move move : piece.getPossibleMoves()) {
                    Board simulator = copyGame();
                    Piece simulatorPiece = getSamePiece(simulator, piece);
                    simulator.makeMove(move.copyMove(simulatorPiece));

                    for (int row = 0; row < 8; ++row) {
                        for (int col = 0; col < 8; ++col) {
                            simulator.clearAttackedField(new Position(row, col));;
                        }
                    }

                    for (Piece simulationPiece : simulator.getAllPieces()) {
                        if (simulationPiece.isInGame()) {
                            simulationPiece.updatePossibleMoves();
                            // System.out.println("Possible moves of " + simulationPiece.getColor().toString() + " " + simulationPiece.toString() + ": " + simulationPiece.getPossibleMoves());
                        }
                    }

                    if (simulator.isChecked(piece.getColor())) {
                        piece.removePossibleMove(move);
                        List<Move> localList = piece.getColor() == Color.WHITE ? possibleMovesWhite : possibleMovesBlack;
                        localList.remove(move);
                    }
                }
            }
        }
    }

    public void setFieldAttacked(Position position, Color color) {
        board[position.getRow()][position.getCol()].setAttacked(color);
    }

    private void clearAttackedField(Position position) {
        board[position.getRow()][position.getCol()].clearAttacked();
    }

    public void addPossibleMove(Color color, Move move) {
        if (color == Color.WHITE) {
            possibleMovesWhite.add(move);
        } else {
            possibleMovesBlack.add(move);
        }
    }

    public Color getCurPlayerColor() {
        return curPlayerColor;
    }

    public void setCurPlayerColor(Color player) {
        curPlayerColor = player;
    }

    public boolean isChecked(Color player) {
        if (whiteKing == null || blackKing == null) {
            for (Piece piece : pieces) {
                if (piece.getPieceType() == PieceType.KING) {
                    if (piece.getColor() == Color.WHITE) {
                        whiteKing = (King) piece;
                    } else {
                        blackKing = (King) piece;
                    }
                }
            }
        }
        if (player == Color.WHITE) {
            return whiteKing.isChecked();
        }
        return blackKing.isChecked();
    }

    public boolean isGameOver() {
        List<Move> currentMoves = curPlayerColor == Color.WHITE ? possibleMovesWhite : possibleMovesBlack;
        return currentMoves.isEmpty();
    }

    public Color getWinner() {

        if (isChecked(Color.WHITE)) {
            return Color.BLACK;
        }
        if (isChecked(Color.BLACK)) {
            return Color.WHITE;
        }
        
        return Color.NOBODY;
    }

    private void clearPossibleMovesWhite() {
        if (!possibleMovesWhite.isEmpty()) {
            possibleMovesWhite.clear();
        }
    }

    private void clearPossibleMovesBlack() {
        if (!possibleMovesBlack.isEmpty()) {
            possibleMovesBlack.clear();
        }
    }

    public Board copyGame() {
        Board copy = new Board();
        for (int i = 0; i < pieces.length; ++i) {
            copy.setPieceAt(i, pieces[i].copyPiece(copy));
        }
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                copy.setFieldAt(row, col, board[row][col].copyField());
                Field copyField = copy.getField(row, col);
                if (copyField.getPiece().getPieceType() != PieceType.EMPTY) {
                    for (Piece copyPiece : copy.getAllPieces()) {
                        if (copyPiece.equals(copyField.getPiece())) {
                            copyField.setPiece(copyPiece);
                        }
                    }
                }
            }
        }
        copy.setLog(log);
        return copy;
    }

    public Piece getSamePiece(Board gameOther, Piece pieceOriginal) {
        for (Piece piece : gameOther.getAllPieces()) {
            if (piece.equals(pieceOriginal)) {
                return piece;
            }
        }
        throw new IllegalStateException("No equal piece found in getSamePiece()");
    }
}
