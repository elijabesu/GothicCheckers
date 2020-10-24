import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private final List<Man> activeMen;
    private final History history;

    public Game() {
        board = new Board(8);
        activeMen = new ArrayList<>();
        history = new History();
    }

    public String displayBoard() {
        return board.displayBoard();
    }

    public void generateMen() {
        // black
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < board.getSize(); j++) {
                activeMen.add(new Man(1, i, j));
            }
        }

        // white
        for (int i = board.getSize() - 2; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                activeMen.add(new Man(-1, i, j));
            }
        }
    }

    public void placeAllMenOnBoard() {
        activeMen.forEach(board::placeMan);
    }

    public boolean move(Player player, Man man, int row, int column) {
        Move move = new Move(player,
                man.getRow(), man.getColumn(), man.getValue(), // the man we are currently moving
                row, column, board.getCoordinate(row, column)); // the position where we want to move

        if (!move.isValid()) return false;

        man.setRow(row);
        man.setColumn(column);
        board.moved(move);
        history.add(move);
        return true;
    }

    public boolean jump(Player player, Man man,
                        int jumpedRow, int jumpedColumn,
                        int row, int column) {
        Man jumpedMan = getManByPosition(jumpedRow, jumpedColumn);
        if (jumpedMan == null) return false;

        Jump jump = new Jump(player,
                man.getRow(), man.getColumn(), man.getValue(), // the man we are currently moving
                jumpedRow, jumpedColumn, jumpedMan.getValue(),
                row, column, board.getCoordinate(row, column)); // the position where we want to move

        if (!jump.isValid()) return false;

        player.addPoint();

        man.setRow(row);
        man.setColumn(column);

        board.jumpedOver(jump);
        history.add(jump);
        activeMen.remove(jumpedMan);

        return true;
    }

    public Man getManByPosition(int row, int column) {
        //if (board.getCoordinate(row, column) == 0) return null;
        return activeMen.stream()
                .filter(man -> man.getRow() == row)
                .filter(man -> man.getColumn() == column)
                .findFirst().orElse(null);
    }

    public String getHistory() {
        return history.toString();
    }
}
