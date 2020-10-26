import java.util.ArrayList;
import java.util.List;

public class Hint {
    private Player player;
    private Man movingMan;
    private Coordinates coordinates;
    private List<Move> possibilities;
    private List<Jump> jumps;   // separating it for when we have to choose the best possible move

    public Hint(Player player, Man movingMan, Coordinates coordinates) {
        this.player = player;
        this.movingMan = movingMan;
        this.coordinates = coordinates;
        this.possibilities = new ArrayList<>();
        this.jumps = new ArrayList<>();

        generateMoves();
    }

    private void generateMoves() {
        if (player.isWhite() != movingMan.isWhite()) return;

        int[] rows = Utils.generateArrayOfThree(movingMan.getRow());
        int[] columns = Utils.generateArrayOfThree(movingMan.getColumn());

        for (int row = 0; row < rows.length; row++) {
            if (rows[row] < 0 || rows[row] > 7) continue;
            for (int column = 0; column < columns.length; column++) {
                if (columns[column] < 0 || columns[column] > 7) continue;
                if (coordinates.isOccupied(rows[row], columns[column])) generateJump(rows[row], columns[column]);
                else {
                    Move maybeMove = new Move(player, movingMan, rows[row], columns[column], 0);
                    if (maybeMove.isValid()) possibilities.add(maybeMove);
                }
            }
        }
    }

    private void generateJump(int row, int column) {
        if (coordinates.getValue(row, column) == movingMan.getValue()) return;

        int nextRow = row - (movingMan.getRow() - row);
        int nextColumn = column - (movingMan.getColumn() - column);

        if (coordinates.isOccupied(nextRow, nextColumn)) return;
        Jump maybeJump = new Jump(player, movingMan,
                row, column, coordinates.getValue(row, column), // jumpedRow, jumpedColumn, jumpedValue
                nextRow, nextColumn, 0);
        if (maybeJump.isValid()) jumps.add(maybeJump);
    }

    @Override
    public String toString() {
        if (player.isWhite() != movingMan.isWhite()) return "Invalid move.";

        if (possibilities.size() == 0) return "No possible moves.";

        StringBuilder str = new StringBuilder();
        str.append("Possible moves:");

        for (Jump jump: jumps) {
            str.append(System.lineSeparator());
            str.append(jump.toStringWithoutPlayer());
        }

        for (Move move: possibilities) {
            str.append(System.lineSeparator());
            str.append(move.toStringWithoutPlayer());
        }

        return str.toString();
    }
}
