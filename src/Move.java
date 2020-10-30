import java.util.Objects;

public class Move {
    protected Player player;
    protected Pieces movingMan;
    protected int originalRow;
    protected int originalColumn;
    protected int newRow;
    protected int newColumn;

    public Move(Player player, Pieces movingMan, int originalRow, int originalColumn, int newRow, int newColumn) {
        this.player = player;
        this.movingMan = movingMan;
        this.originalRow = originalRow;
        this.originalColumn = originalColumn;
        this.newRow = newRow;
        this.newColumn = newColumn;
    }

    public int getOriginalRow() {
        return originalRow;
    }

    public int getOriginalColumn() {
        return originalColumn;
    }

    public Pieces getMan() {
        return movingMan;
    }

    public int getNewRow() {
        return newRow;
    }

    public int getNewColumn() {
        return newColumn;
    }

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
                newRow == move.newRow &&
                newColumn == move.newColumn &&
                player.equals(move.player) &&
                movingMan.equals(move.movingMan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, movingMan, originalRow, originalColumn, newRow, newColumn);
    }
}
