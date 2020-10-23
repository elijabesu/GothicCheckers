public class Board {
    private StringBuilder boardString;
    private int size;
    //private int[][] board;
    private Positions coordinates;

    public Board(int size) {
        this.size = size;
        this.boardString = new StringBuilder();
        //this.board = new int[size][size];
        coordinates = new Positions(size);
    }

    public String displayBoard() {
        boardString.delete(0, boardString.length());
        appendColumnsNames();
        for (int row = 0; row < size; row++) {
            boardString.append(Symbols.LINE.getSymbol());
            boardString.append(Columns.values()[row].ordinal() + 1);
            boardString.append(Symbols.EMPTY.getSymbol());
            appendColumns(row);
        }
        boardString.append(Symbols.LINE.getSymbol());

        return boardString.toString();
    }

    private void appendColumns(int row) {
        for (int column = 0; column < size; column++) {
            boardString.append(Symbols.VERTICAL.getSymbol());
            //switch (board[row][column]) {
            switch (coordinates.getValue(row, column)) {
                case 0:
                    boardString.append(Symbols.EMPTY.getSymbol());
                    break;
                case -1:
                    boardString.append(Symbols.WHITE.getSymbol());
                    break;
                case -2:
                    boardString.append(Symbols.WHITE_KING.getSymbol());
                    break;
                case 1:
                    boardString.append(Symbols.BLACK.getSymbol());
                    break;
                case 2:
                    boardString.append(Symbols.BLACK_KING.getSymbol());
                    break;
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
        //board[man.getRow()][man.getColumn()] = man.getValue();
        coordinates.placeMan(man.getRow(), man.getColumn(), man.getValue());
    }

    public int getCoordinate(int row, int column) {
        //return board[row][column];
        return coordinates.getValue(row, column);
    }

    public void moved(Move move) {
        //board[move.getOriginalRow()][move.getOriginalColumn()] = 0;
        coordinates.removeMan(move.getOriginalRow(), move.getOriginalColumn());
        //board[move.getNewRow()][move.getNewColumn()] = move.getMan();
        coordinates.placeMan(move.getNewRow(), move.getNewColumn(), move.getMan());
    }
}
