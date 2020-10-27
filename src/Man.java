public class Man {
    private Pieces value; // -1 is WHITE == o, 1 is BLACK == x
    private int row;
    private int column;

    public Man(Pieces value, int row, int column) {
        this.value = value;
        this.row = row;
        this.column = column;
    }

    public void promote() {
        if (value == Pieces.BLACK) value = Pieces.BLACK_KING;
        if (value == Pieces.WHITE) value = Pieces.WHITE_KING;
    }

    public Pieces getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isKing() {
        return (value == Pieces.BLACK_KING || value == Pieces.WHITE_KING);
    }

    public boolean isWhite() {
        return (value == Pieces.WHITE || value == Pieces.WHITE_KING);
    }
}
