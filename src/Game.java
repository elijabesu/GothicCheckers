import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private final List<Man> activeMen;
    private final History history;
    private int movesWithoutJump;
    private boolean playerBool; // true == player1, false == player2

    public Game() {
        board = new Board(8);
        activeMen = new ArrayList<>();
        history = new History();
        movesWithoutJump = 0;
        playerBool = true;

        generateMen();
        placeAllMenOnBoard();
    }

    public String displayBoard() {
        return board.displayBoard();
    }

    public void generateMen() {
        // black
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < board.getSize(); j++) {
                activeMen.add(new Man(Pieces.BLACK, i, j));
            }
        }

        // white
        for (int i = board.getSize() - 2; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                activeMen.add(new Man(Pieces.WHITE, i, j));
            }
        }
    }

    public void placeAllMenOnBoard() {
        activeMen.forEach(board::placeMan);
    }

    public boolean move(Player player, Man movingMan, int row, int column) {
        Move move = new Move(player, movingMan, // the man we are currently moving
                row, column, board.getCoordinate(row, column)); // the position where we want to move

        if (!move.isValid()) return false;

        if (move.needsPromotion()) movingMan.promote();

        movingMan.setRow(row);
        movingMan.setColumn(column);

        board.moved(move);
        history.add(move);
        ++movesWithoutJump;

        return true;
    }

    public boolean jump(Player player, Man movingMan,
                        int jumpedRow, int jumpedColumn,
                        int row, int column) {
        Man jumpedMan = getManByPosition(jumpedRow, jumpedColumn);
        if (jumpedMan == null) return false;

        Jump jump = new Jump(player, movingMan, // the man we are currently moving
                jumpedRow, jumpedColumn, jumpedMan.getValue().getValue(),
                row, column, board.getCoordinate(row, column)); // the position where we want to move

        if (!jump.isValid()) return false;

        player.addPoint();

        if (jump.needsPromotion()) movingMan.promote();

        movingMan.setRow(row);
        movingMan.setColumn(column);

        board.jumped(jump);
        history.add(jump);
        activeMen.remove(jumpedMan);
        movesWithoutJump = 0;

        return true;
    }

    public Man getManByPosition(int row, int column) {
        return activeMen.stream()
                .filter(man -> man.getRow() == row)
                .filter(man -> man.getColumn() == column)
                .findFirst().orElse(null);
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

    public String hint(Player player, Man movingMan) {
        Hint hint = new Hint(player, movingMan, board);
        return hint.toString();
    }

    public boolean shouldEnd() {
        return movesWithoutJump == 30;
    }

    public boolean getPlayerBool() {
        return playerBool;
    }

    public void switchPlayers() {
        if (playerBool) playerBool = false;
        else playerBool = true;
    }
}
