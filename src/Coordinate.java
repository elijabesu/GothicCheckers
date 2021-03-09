import java.util.Objects;

public class Coordinate {
    /*
    Storing information about the coordinate.
     */
    private final int row;
    private final int column;
    private Pieces piece;


    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }

    public Pieces getPiece() { return piece; }
    public void setPiece(Pieces piece) { this.piece = piece; }

    @Override
    public String toString() {
        return "" + Columns.values()[column] + Utils.convertRowToString(row);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
