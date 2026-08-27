package chess;

import java.util.ArrayList;
import java.util.List;

import chess.game.Board;
import chess.game.HumanPlayer;
import chess.game.Move;
import chess.game.Player;
import chess.game.Position;
import chess.pieces.Color;
import chess.pieces.Empty;
import chess.pieces.Piece;
import chess.pieces.PieceType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Main extends Application{
    private final Button[][] fields = new Button[8][8];
    private final Board game = new Board();
    private Thread gameThread;
    private Position selectedTo = null;
    private Position selectedFrom = null;
    private List<Move> possibleMoves = new ArrayList<>();
    private Color curPlayer = Color.WHITE;
    private PieceType promotionChoice;

    @Override
    public void start(Stage stage) {
        GridPane board = new GridPane();

        Player playerOne = new HumanPlayer(Color.WHITE);
        Player playerTwo = new HumanPlayer(Color.BLACK);

        gameThread = new Thread(() -> game.run(this, playerOne, playerTwo), "Game-Thread");
        gameThread.setDaemon(true);
        gameThread.start();

        for (int row = 0; row < 8; ++row){
            //add numbers 1-8
            Label leftNumber = new Label(String.valueOf(8 - row));
            leftNumber.setPrefSize(30, 70);
            leftNumber.setAlignment(Pos.CENTER);
            board.add(leftNumber, 0, row + 1);

            Label rightNumber = new Label(String.valueOf(8 - row));
            rightNumber.setPrefSize(30, 70);
            rightNumber.setAlignment(Pos.CENTER);
            board.add(rightNumber, 9, row + 1);

            //add 8 fields, with outer loop total 8x8 fields
            for (int col = 0; col < 8; ++col){
                Button field = new Button();

                field.setMinSize(70, 70);
                field.setPrefSize(70, 70);
                field.setMaxSize(70, 70);

                if ((row + col) % 2 == 1){
                    field.setStyle("-fx-background-color: #f0d9b5");
                } else {
                    field.setStyle("-fx-background-color: #b58863");
                }

                int r = row;
                int c = col;

                field.setOnAction(e -> fieldClicked(new Position(r, c)));

                fields[row][col] = field;
                board.add(field, col + 1, 8 - row);
            }
        }

        //add letters a-h
        for (int col = 0; col < 8; ++col){
            char letter = (char) ('a' + col);

            Label topLetter = new Label(String.valueOf(letter));
            topLetter.setPrefSize(70, 30);
            topLetter.setAlignment(Pos.CENTER);
            board.add(topLetter, col + 1, 0);

            Label bottomLetter = new Label(String.valueOf(letter));
            bottomLetter.setPrefSize(70, 30);
            bottomLetter.setAlignment(Pos.CENTER);
            board.add(bottomLetter, col + 1, 9);
        }

        Scene scene = new Scene(board, 600, 600);

        stage.setTitle("Chess");
        stage.setScene(scene);

        updateScreen(game);

        stage.show();
    }

    @Override
    public void stop() {
        if (gameThread != null) {
            gameThread.interrupt();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private synchronized void fieldClicked(Position pos) {
        updateScreen(game);
        // System.out.println("CLICK: " + pos.toString() + " | THREAD: " + Thread.currentThread().getName());
        Piece piece = game.getPiece(pos);

        if (selectedFrom == null) {
            if (!piece.equals(new Empty()) && piece.getColor() == curPlayer){
                selectedFrom = pos;
                possibleMoves = piece.getPossibleMoves();

                // System.out.println(piece.toString().toUpperCase() + " selectedFrom: " + selectedFrom.toString() + "\npossibleMoves: " + possibleMoves);
                markPossibleMoves();
            }
        } 
        else {
            Move clickedMove = new Move(game.getPiece(selectedFrom), selectedFrom, pos);

            // System.out.println("Clicked Move: " + clickedMove.toString());
            // System.out.println("Contains:" + possibleMoves.contains(clickedMove));

            if (possibleMoves.contains(clickedMove)) {
                selectedTo = pos;
                possibleMoves.clear();

                // System.out.println("Valid target:" + selectedTo.toString());
                // System.out.println("notifyAll()");

                notifyAll();
            } 
            else {
                if (!piece.equals(new Empty()) && piece.getColor() == curPlayer) {
                    // System.out.println("Piece: " + piece.toString());

                    selectedFrom = pos;
                    possibleMoves = piece.getPossibleMoves();

                    // System.out.println("selectedFrom: " + selectedFrom.toString() + "\npossibleMoves: " + possibleMoves);
                    markPossibleMoves();
                } else {
                    // System.out.println("Invalid targt");

                    selectedFrom = null;
                    possibleMoves.clear();
                }
            }
        }
    }

    private void markPossibleMoves() {
        for (Move move : possibleMoves) {
            Position to = move.getTo();
            int row = to.getRow();
            int col = to.getCol();

            Circle marker = new Circle(8);
            marker.setStyle("-fx-fill: rgba(80, 80, 80, 0.6);");

            StackPane content = new StackPane();

            if (fields[row][col].getGraphic() != null) {
                content.getChildren().add(fields[row][col].getGraphic());
            }

            content.getChildren().add(marker);

            fields[row][col].setGraphic(content);
        }
    }

    public void updateScreen(Board game) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> updateScreen(game));
            return;
        }
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {

                Piece piece = game.getPiece(new Position(row, col));

                if (piece.equals(new Empty())) {
                    fields[row][col].setGraphic(null);
                } else {
                    fields[row][col].setGraphic(createPieceImage(piece.getColor(), piece.getPieceType()));
                }
            }
        }
    }

    private ImageView createPieceImage(Color color, PieceType type) {
        String path = "/pieces/" + color.toString()
                    + "-" + type.toString() + ".png";
        
        Image image = new Image(getClass().getResourceAsStream(path));

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    public synchronized Move getUserMove(Color player) {
        curPlayer = player;
        while (selectedTo == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Game thread was interupted while waiting for user input", e);
            }
        }

        Move result = new Move(game.getPiece(selectedFrom), selectedFrom, selectedTo);
        selectedFrom = null;
        selectedTo = null;
        return result;
    }

    public synchronized PieceType getPromotionChoice(Position pos) {
        promotionChoice = PieceType.EMPTY;

        Platform.runLater(() -> showPromotionPopup(pos));
        while (promotionChoice == PieceType.EMPTY) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Game thread was interrupted while waiting for user input", e);
            }
        }

        return promotionChoice;
    }

    private void showPromotionPopup(Position pos) {
        Color color = pos.getRow() == 0 ? Color.BLACK : Color.WHITE;

        Popup popup = new Popup();

        VBox box = new VBox();

        PieceType[] types = {
            PieceType.QUEEN,
            PieceType.ROOK,
            PieceType.BISHOP,
            PieceType.KNIGHT
        };

        for (PieceType type : types) {
            Button button = new Button();

            ImageView image = createPieceImage(color, type);
            button.setGraphic(image);

            button.setOnAction(e -> {promotionSelected(type); popup.hide();});

            box.getChildren().add(button);
        }

        popup.getContent().add(box);
        Button field = fields[pos.getRow()][pos.getCol()];
        Bounds bounds = field.localToScreen(field.getBoundsInLocal());

        double x = bounds.getMinX();
        double y;
        if (color == Color.WHITE) {
            y = bounds.getMaxY();
        } else {
            y = bounds.getMinY();
        }

        popup.show(field, x, y);
    }

    private synchronized void promotionSelected(PieceType type) {
        promotionChoice = type;
        notifyAll();
    }

    public void showGameOverScreen(String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showGameOverScreen(message));
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over");
        alert.setContentText(message);

        alert.showAndWait();
    }
}