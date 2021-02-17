import java.util.Objects;

public class Move {
    protected final Player player;
    protected final Pieces movingMan;
    protected final int originalRow;
    protected final int originalColumn;
    protected final int jumpedRow;
    protected final int jumpedColumn;
    protected final Pieces jumpedMan;
    protected final int newRow;
    protected final int newColumn;
    protected double evaluation;
    protected final boolean isJump;

    public Move(Player player, Pieces movingMan, int originalRow, int originalColumn,
                int newRow, int newColumn) {
        this(player, movingMan, originalRow, originalColumn,
                -1, -1, null,
                newRow, newColumn, 0, false);
    }

    public Move(Player player, Pieces movingMan, int originalRow, int originalColumn,
                int jumpedRow, int jumpedColumn, Pieces jumpedMan,
                int newRow, int newColumn) {
        this(player, movingMan, originalRow, originalColumn,
                jumpedRow, jumpedColumn, jumpedMan,
                newRow, newColumn, 0, true);
    }
    public Move(Player player, Pieces movingMan, int originalRow, int originalColumn,
                int jumpedRow, int jumpedColumn, Pieces jumpedMan,
                int newRow, int newColumn, double evaluation, boolean isJump) {
        this.player = player;
        this.movingMan = movingMan;
        this.originalRow = originalRow;
        this.originalColumn = originalColumn;
        this.jumpedRow = jumpedRow;
        this.jumpedColumn = jumpedColumn;
        this.jumpedMan = jumpedMan;
        this.newRow = newRow;
        this.newColumn = newColumn;
        this.evaluation = evaluation;
        this.isJump = isJump;
    }

    public int getOriginalRow() { return originalRow; }

    public int getOriginalColumn() { return originalColumn; }

    public Pieces getMan() { return movingMan; }

    public int getNewRow() { return newRow; }

    public int getNewColumn() { return newColumn; }

    public void setEvaluation(double evaluation) { this.evaluation = evaluation; }

    public double getEvaluation() { return evaluation; }

    public boolean isJump() { return this.isJump; }

    public int getJumpedRow() { return jumpedRow; }

    public int getJumpedColumn() { return jumpedColumn; }

    public Pieces getJumpedMan() { return jumpedMan; }

    public Player getPlayer() { return this.player; }

    @Override
    public String toString() {
        return toStringWithoutPlayer() + " (" + player.getName() + ", " + Utils.whichMan(movingMan) + ")";
    }

    public String toStringWithoutPlayer() {
        return "" + Columns.values()[originalColumn] + Utils.convertRowForToString(originalRow) + " -> " +
                Columns.values()[newColumn] + Utils.convertRowForToString(newRow);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return originalRow == move.originalRow &&
                originalColumn == move.originalColumn &&
                jumpedRow == move.jumpedRow &&
                jumpedColumn == move.jumpedColumn &&
                newRow == move.newRow &&
                newColumn == move.newColumn &&
                Double.compare(move.evaluation, evaluation) == 0 &&
                isJump == move.isJump &&
                Objects.equals(player, move.player) &&
                movingMan == move.movingMan &&
                jumpedMan == move.jumpedMan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, movingMan, originalRow, originalColumn, jumpedRow, jumpedColumn, jumpedMan, newRow, newColumn, evaluation, isJump);
    }
}
