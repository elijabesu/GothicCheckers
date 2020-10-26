import java.util.ArrayList;
import java.util.List;

public class Hint {
    private List<Move> possibilities;
    private Man movingMan;
    private Coordinates coordinates;
    private Player player;

    public Hint(Player player, Man movingMan, Coordinates coordinates) {
        this.possibilities = new ArrayList<>();
        this.movingMan = movingMan;
        this.coordinates = coordinates;
        this.player = player;

        generateMoves();
    }

    private void generateMoves() {
        int[] rows = Utils.generateArrayOfThree(movingMan.getRow());
        int[] columns = Utils.generateArrayOfThree(movingMan.getColumn());

        for (int row = 0; row < rows.length; row++) {
            if (rows[row] < 0 || rows[row] > 7) continue;
            for (int column = 0; column < columns.length; column++) {
                if (columns[column] < 0 || columns[column] > 7) continue;
                if (coordinates.isOccupied(rows[row], columns[column])) continue;//generateJump(rows[row], columns[column]);
                else {
                    Move maybeMove = new Move(player, movingMan, rows[row], columns[column], 0);
                    if (maybeMove.isValid()) possibilities.add(maybeMove);
                }
            }
        }
    }

    private void generateJumps() {
        // TODO
    }

    private void generateJump(int row, int column) {
        // TODO
        int rowDifference = movingMan.getRow() - row;
        int columnDifference = movingMan.getColumn() - column;
        if (rowDifference == -1 && columnDifference == 0) { // smer dolu 0 -> 7, tzn. napr. getRow() 1 - row 2 = -1, tzn. row je vetsi, ale je niz
            if (coordinates.isOccupied(row + 1, column)) return;
            Jump maybeJump = new Jump(player, movingMan, row, column, coordinates.getValue(row, column),
                    row + 1, column, 0);
            if (maybeJump.isValid()) possibilities.add(maybeJump);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Possible moves:");
        for (Move move: possibilities) {
            str.append(System.lineSeparator());
            str.append(move.toStringWithoutPlayer());
        }
        return str.toString();
    }
}
