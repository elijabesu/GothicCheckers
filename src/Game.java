import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private final Rules rules;
    private final History history;
    private int movesWithoutJump;
    private boolean playerBool; // true == player1, false == player2

    public Game() {
        board = new Board(8);
        rules = new Rules(board);
        history = new History();
        movesWithoutJump = 0;
        playerBool = true;

        generateMen();
    }

    public String displayBoard() {
        return board.displayBoard();
    }

    public void generateMen() {
        // black
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < board.getSize(); j++) {
                board.placeMan(Pieces.BLACK, i, j);
            }
        }

        // white
        for (int i = board.getSize() - 2; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                board.placeMan(Pieces.WHITE, i, j);
            }
        }
    }

    public boolean move(Player player, Pieces movingMan, int[] coordinates) {
        Move move = new Move(player, movingMan, coordinates[0], coordinates[1], // the man we are currently moving
                coordinates[2], coordinates[3]); // the position where we want to move

        if (!(rules.isValid(player, movingMan, move))) return false;
        afterMove(move);
        return true;
    }

    public boolean jump(Player player, Pieces movingMan, int[] coordinates) {
        Pieces jumpedMan = getManByPosition(coordinates[4], coordinates[5]);
        if (jumpedMan == null) return false;

        Jump jump = new Jump(player, movingMan, coordinates[0], coordinates[1], // the man we are currently moving
                coordinates[4], coordinates[5], jumpedMan,
                coordinates[2], coordinates[3]); // the position where we want to move

        if (!(rules.isValid(player, movingMan, jump))) return false;
        afterJump(player, jump);
        return true;
    }

    public boolean moveKing(Player player, Pieces movingMan, int[] coordinates) {
        Move maybeMove = rules.getPossibleMoves(player, movingMan, coordinates[0], coordinates[1]).stream()
                .filter(move -> move.getNewRow() == coordinates[2] && move.getNewColumn() == coordinates[3])
                .findFirst().orElse(null);
        if (maybeMove != null) {
            afterMove(maybeMove);
            return true;
        }

        Jump maybeJump = rules.getPossibleJumps(player, movingMan, coordinates[0], coordinates[1]).stream()
                .filter(jump -> jump.getNewRow() == coordinates[2] && jump.getNewColumn() == coordinates[3])
                .findFirst().orElse(null);
        if (maybeJump != null) {
            afterJump(player, maybeJump);
            return true;
        }
        return false;
    }

    public Pieces getManByPosition(int row, int column) {
        return board.getCoordinate(row, column);
    }

    public String getHistory() {
        return history.toString();
    }

    public boolean save() {
        try {
            history.save();
            return true;
        } catch (Exception e) {
            System.out.println("Error saving a file: " + e);
            return false;
        }
    }

    public boolean load(Game game, Player player1, Player player2, String fileName) {
        try {
            history.load(game, player1, player2, fileName);
            return true;
        } catch (Exception e) {
            System.out.println("Error loading a file: " + e);
            return false;
        }
    }

    public String hint(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        Hint hint = new Hint(player, movingMan, originalRow, originalColumn, rules);
        return hint.toString();
    }

    public boolean shouldEnd(Player player1, Player player2) {
        return movesWithoutJump == 30 || player1.getPoints() == 16 || player2.getPoints() == 16;
    }

    public boolean getPlayerBool() {
        return playerBool;
    }

    public void switchPlayers() {
        if (playerBool) playerBool = false;
        else playerBool = true;
    }

    private void afterJump(Player player, Jump jump) {
        player.addPoint();

        board.jumped(jump);

        if (rules.needsPromotion(jump.getMan(), jump.getNewRow()))
            board.promoted(jump.getMan(), jump.getNewRow(), jump.getNewColumn());

        history.add(jump);
        movesWithoutJump = 0;
    }

    private void afterMove(Move move) {
        board.moved(move);

        if (rules.needsPromotion(move.getMan(), move.getNewRow()))
            board.promoted(move.getMan(), move.getNewRow(), move.getNewColumn());

        history.add(move);
        ++movesWithoutJump;
    }
}
