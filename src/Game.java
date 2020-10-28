import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private final List<Man> activeMen;
    private final Rules rules;
    private final History history;
    private int movesWithoutJump;
    private boolean playerBool; // true == player1, false == player2

    public Game() {
        board = new Board(8);
        activeMen = new ArrayList<>();
        rules = new Rules(board);
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

    public boolean move(Player player, Man movingMan, int newRow, int newColumn) {
        Move move = new Move(player, movingMan, // the man we are currently moving
                newRow, newColumn); // the position where we want to move

        if (!(rules.isValid(player, movingMan, move))) return false;
        if (rules.needsPromotion(movingMan, newRow)) movingMan.promote();
        afterMove(movingMan, move);
        return true;
    }

    public boolean jump(Player player, Man movingMan,
                        int jumpedRow, int jumpedColumn,
                        int newRow, int newColumn) {
        Man jumpedMan = getManByPosition(jumpedRow, jumpedColumn);
        if (jumpedMan == null) return false;

        Jump jump = new Jump(player, movingMan, // the man we are currently moving
                jumpedRow, jumpedColumn, jumpedMan.getValue().getNumberValue(),
                newRow, newColumn); // the position where we want to move

        if (!(rules.isValid(player, movingMan, jump))) return false;
        if (rules.needsPromotion(movingMan, newRow)) movingMan.promote();
        afterJump(player, movingMan, jump, jumpedMan);
        return true;
    }

    public boolean moveKing(Player player, Man movingMan, int newRow, int newColumn) {
        Move maybeMove = rules.getPossibleMoves(player, movingMan).stream()
                .filter(move -> move.getNewRow() == newRow && move.getNewColumn() == newColumn)
                .findFirst().orElse(null);
        if (maybeMove != null) {
            afterMove(movingMan, maybeMove);
            return true;
        }

        Jump maybeJump = rules.getPossibleJumps(player, movingMan).stream()
                .filter(jump -> jump.getNewRow() == newRow && jump.getNewColumn() == newColumn)
                .findFirst().orElse(null);
        if (maybeJump != null) {
            afterJump(player, movingMan, maybeJump,
                    getManByPosition(maybeJump.getJumpedRow(), maybeJump.getJumpedColumn()));
            return true;
        }
        return false;
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
        Hint hint = new Hint(player, movingMan, rules);
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

    private void afterJump(Player player, Man movingMan, Jump jump, Man jumpedMan) {
        player.addPoint();

        movingMan.setRow(jump.getNewRow());
        movingMan.setColumn(jump.getNewColumn());

        board.jumped(jump);
        history.add(jump);
        activeMen.remove(jumpedMan);
        movesWithoutJump = 0;
    }

    private void afterMove(Man movingMan, Move move) {
        movingMan.setRow(move.getNewRow());
        movingMan.setColumn(move.getNewColumn());

        board.moved(move);
        history.add(move);
        ++movesWithoutJump;
    }
}
