import java.util.ArrayList;
import java.util.List;

public class Hint {
    private final Player player;
    private final Man movingMan;
    private final Board board;
    private final List<Move> possibilities;
    private final List<Jump> jumps;   // separating it for when we have to choose the best possible move

    public Hint(Player player, Man movingMan, Board board) {
        this.player = player;
        this.movingMan = movingMan;
        this.board = board;
        this.possibilities = new ArrayList<>();
        this.jumps = new ArrayList<>();

        generateMoves();
    }

    private void generateMoves() {
        if (player.isWhite() != movingMan.isWhite()) return;

        int[] rows = Utils.generateArrayOfThree(movingMan.getRow());
        int[] columns = Utils.generateArrayOfThree(movingMan.getColumn());

        for (int row: rows) {
            if (row < 0 || row > 7) continue;
            for (int column: columns) {
                if (column < 0 || column > 7) continue;
                if (board.isOccupied(row, column)) generateJump(row, column);
                else {
                    Move maybeMove = new Move(player, movingMan, row, column, 0);
                    if (maybeMove.isValid()) possibilities.add(maybeMove);
                }
            }
        }
    }

    private void generateJump(int row, int column) {
        if (board.getCoordinate(row, column) == movingMan.getValue().getValue()) return;

        int nextRow = row - (movingMan.getRow() - row);
        int nextColumn = column - (movingMan.getColumn() - column);

        if (board.isOccupied(nextRow, nextColumn)) return;
        Jump maybeJump = new Jump(player, movingMan,
                row, column, board.getCoordinate(row, column), // jumpedRow, jumpedColumn, jumpedValue
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
