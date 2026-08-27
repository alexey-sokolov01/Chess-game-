# Chess

A complete chess game written in Java with a JavaFX GUI.

The project was built as a personal learning project to practice object-oriented programming, java,
game logic, threading, and artificial intelligence (in progress).

## Features
- Full 8x8 chess board with JavaFX GUI
- White and black pieces with graphical piece assets
- Legal move calculation for each chess piece
- Turn-based gameplay
- Capturing pieces
- Check detection
- Checkmate detection
- Prevention of moves that leave the own king in check
- Castling
    - Kingside castling
    - Queenside castling
    - Castling restriction if king or rook has moved
    - Preventing castling through attacked fields
- En passant
- Pawn promotion with piece selection
- Game-over screen
- Possible move highlighting
- Seperate game thread and JavaFX application thread

## Technologies

- Java 26
- JavaFX 26
- Maven

## Requirements

- JDK 26
- Maven

## Running the game

Clone the repository and open in project directory:

```bash
git clone <repository-url>
cd Chess
```

Run the application:

```bash
mvn clean javafx:run
```


## Project Structure

```text
scr/
├── main/
    ├── java/
    |   └── chess/
    |       ├── Main.java
    |       ├── game/
    |       |   ├── Board.java
    |       |   ├── Field.java
    |       |   ├── HumanPlayer.java
    |       |   ├── Move.java
    |       |   ├── Player.java
    |       |   └── Position.java
    |       └── pieces/
    |           ├── Color.java
    |           ├── PieceType.java
    |           ├── Piece.java
    |           ├── Empty.java
    |           ├── Pawn.java
    |           ├── Knight.java
    |           ├── Bishop.java
    |           ├── Rook.java
    |           ├── Queen.java
    |           └── King.java
    └── resources/
        └── pieces
```

'Board' contains the main game state and move handling.
The individual chess pieces are implemented as subclasses of 'Piece'.
'Main' contains the JavaFX UI and coordinates communication with the game thread.

## Chess logic 

Each chess piece calculates its possible move based on the the current board state.

Before a move is considered legal, the game creates a copy of the current board and
simulates the move. Moves that leave the player's own king in check are removed from
the legal move list.

This mechanism is also used to detect other scenarios like checkmate or pinned pieces.

## Status

The game is currently fully playable and supports standard chess reuls.

## Future Improvements

- Computer opponent / chess AI

Further possible future additions include:

- Minimax with alpha-beta pruning
- Move history display
- Undo functionality
- Draw rules such as threefold repitition and the fifty-move rule
- Improved UI and animations
- Chess notation (SAN)

## License

This project was created for educational and personal use.