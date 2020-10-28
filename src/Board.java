public class Board {
    private final StringBuilder boardString;
    private final int size;
    private final int[][] coordinates;

    public Board(int size) {
        this.size = size;
        this.boardString = new StringBuilder();
        this.coordinates = new int[size][size];
    }

    public String displayBoard() {
        boardString.delete(0, boardString.length());
        appendColumnsNames();
        for (int row = 0; row < size; row++) {
            boardString.append(Symbols.LINE.getSymbol());
            boardString.append(Columns.values()[size - row - 1].ordinal() + 1);
            boardString.append(Symbols.EMPTY.getSymbol());
            appendColumns(row);
        }
        boardString.append(Symbols.LINE.getSymbol());

        return boardString.toString();
    }

    private void appendColumns(int row) {
        for (int column = 0; column < size; column++) {
            boardString.append(Symbols.VERTICAL.getSymbol());
            switch (coordinates[row][column]) {
                case 0 -> boardString.append(Symbols.EMPTY.getSymbol());
                case -1 -> boardString.append(Symbols.WHITE.getSymbol());
                case -2 -> boardString.append(Symbols.WHITE_KING.getSymbol());
                case 1 -> boardString.append(Symbols.BLACK.getSymbol());
                case 2 -> boardString.append(Symbols.BLACK_KING.getSymbol());
            }
        }
        boardString.append(Symbols.VERTICAL.getSymbol());
    }

    private void appendColumnsNames() {
        boardString.append(Symbols.EMPTY.getSymbol());
        for (int i = 0; i < size; i++) {
            boardString.append(Symbols.EMPTY.getSymbol());
            boardString.append(Columns.values()[i]);
        }
    }

    public int getSize() {
        return size;
    }

    public void placeMan(Man man) {
        coordinates[man.getRow()][man.getColumn()] = man.getValue().getNumberValue();
    }

    public void removeMan(int row, int column) {
        coordinates[row][column] = 0;
    }

    public int getCoordinate(int row, int column) {
        return coordinates[row][column];
    }

    public boolean isOccupied(int row, int column) {
        return !(coordinates[row][column] == 0);
    }

    public void moved(Move move) {
        removeMan(move.getOriginalRow(), move.getOriginalColumn());
        placeMan(move.getMan());
    }

    public void jumped(Jump jump) {
        removeMan(jump.getOriginalRow(), jump.getOriginalColumn());
        removeMan(jump.getJumpedRow(), jump.getJumpedColumn());
        placeMan(jump.getMan());
    }
}
