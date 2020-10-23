public class Man {
    private int value; // -1 is WHITE == o, 1 is BLACK == x
    private int row;
    private int column;

    public Man(int value, int row, int column) {
        this.value = value;
        this.row = row;
        this.column = column;
    }

    public void promote() {
        if (value == 1) value = 2;
        if (value == -1) value = -2;
    }

    public int getValue() {
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
}
