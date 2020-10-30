import java.util.Objects;

public class Jump extends Move {
    private final int jumpedRow;
    private final int jumpedColumn;
    private final Pieces jumpedMan;

    public Jump(Player player, Pieces movingMan, int originalRow, int originalColumn,
                int jumpedRow, int jumpedColumn, Pieces jumpedMan,
                int newRow, int newColumn) {
        super(player, movingMan, originalRow, originalColumn, newRow, newColumn);
        this.jumpedRow = jumpedRow;
        this.jumpedColumn = jumpedColumn;
        this.jumpedMan = jumpedMan;
    }

    public int getJumpedRow() {
        return jumpedRow;
    }

    public int getJumpedColumn() {
        return jumpedColumn;
    }

    public Pieces getJumpedMan() {
        return jumpedMan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Jump jump = (Jump) o;
        return jumpedRow == jump.jumpedRow &&
                jumpedColumn == jump.jumpedColumn &&
                jumpedMan == jump.jumpedMan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), jumpedRow, jumpedColumn, jumpedMan);
    }
}
