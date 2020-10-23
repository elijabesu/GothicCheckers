import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private List<Man> men;
    private History history;

    public Game() {
        board = new Board(8);
        men = new ArrayList<>();
        history = new History();
    }

    public String displayBoard() {
        return board.displayBoard();
    }

    public void generateMen() {
        // black
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < board.getSize(); j++) {
                men.add(new Man(1, i, j));
            }
        }

        // white
        for (int i = board.getSize() - 2; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                men.add(new Man(-1, i, j));
            }
        }
    }

    public void placeAllMenOnBoard() {
        men.stream().forEach(man -> board.placeMan(man));
    }

    public boolean move(boolean player, Man man, int row, int column) {
        Move move = new Move(
                man.getRow(), man.getColumn(), man.getValue(), // the man we are currently moving
                row, column, board.getCoordinate(row, column)); // the position where we want to move

        if (!move.isValid(player)) return false;

        man.setRow(row);
        man.setColumn(column);
        board.moved(move);
        history.add(move);
        return true;
    }

    public boolean jump(boolean player, Man man,
                        int jumpedRow, int jumpedColumn,
                        int row, int column) {
        Man jumpedMan = getManByPosition(jumpedRow, jumpedColumn);
        if (jumpedMan == null) return false;

        Jump jump = new Jump(
                man.getRow(), man.getColumn(), man.getValue(), // the man we are currently moving
                jumpedRow, jumpedColumn, jumpedMan.getValue(),
                row, column, board.getCoordinate(row, column)); // the position where we want to move

        if (!jump.isValid(player)) return false;

        man.setRow(row);
        man.setColumn(column);
        board.jumpedOver(jump);
        history.add(jump);
        return true;
    }

    public Man getManByPosition(int row, int column) {
        if (board.getCoordinate(row, column) == 0) return null;
        return men.stream()
                .filter(man -> man.getRow() == row)
                .filter(man -> man.getColumn() == column)
                .findFirst().get();
    }

    public String getHistory() {
        return history.toString();
    }
}
