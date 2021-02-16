import java.util.ArrayList;
import java.util.List;

public class Board {
    private final StringBuilder boardString;
    private final int size;
    private Pieces[][] coordinates;

    public Board(int size) {
        this.size = size;
        this.boardString = new StringBuilder();
        this.coordinates = new Pieces[size][size];
    }

    public Board(int size, Pieces[][] coordinates) {
        this(size);
        this.coordinates = coordinates;
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
            if (coordinates[row][column] == null) coordinates[row][column] = Pieces.EMPTY;
            switch (coordinates[row][column]) {
                case EMPTY -> boardString.append(Symbols.EMPTY.getSymbol());
                case WHITE -> boardString.append(Symbols.WHITE.getSymbol());
                case WHITE_KING -> boardString.append(Symbols.WHITE_KING.getSymbol());
                case BLACK -> boardString.append(Symbols.BLACK.getSymbol());
                case BLACK_KING -> boardString.append(Symbols.BLACK_KING.getSymbol());
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

    public void placeMan(Pieces man, int row, int column) {
        coordinates[row][column] = man;
    }

    public void removeMan(int row, int column) {
        coordinates[row][column] = Pieces.EMPTY;
    }

    public Pieces getCoordinate(int row, int column) {
        return coordinates[row][column];
    }

    public boolean isOccupied(int row, int column) {
        return (coordinates[row][column] != Pieces.EMPTY);
    }

    public void moved(Move move) {
        removeMan(move.getOriginalRow(), move.getOriginalColumn());
        placeMan(move.getMan(), move.getNewRow(), move.getNewColumn());
    }

    public void jumped(Jump jump) {
        removeMan(jump.getOriginalRow(), jump.getOriginalColumn());
        removeMan(jump.getJumpedRow(), jump.getJumpedColumn());
        placeMan(jump.getMan(), jump.getNewRow(), jump.getNewColumn());
    }

    public void promoted(Pieces man, int row, int column) {
        if (man.isWhite()) coordinates[row][column] = Pieces.WHITE_KING;
        else coordinates[row][column] = Pieces.BLACK_KING;
    }

    public List<int[]> getCoordinatesList(Player player) {
        List<int[]> coordList = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Pieces currentPiece = getCoordinate(row, column);
                if (currentPiece != Pieces.EMPTY) {
                    if (currentPiece.isWhite() && player.isWhite()) {
                        if (currentPiece.isKing()) coordList.add(new int[]{row, column, 1});
                        else coordList.add(new int[]{row, column, 0});
                    } else if (!currentPiece.isWhite() && !player.isWhite()) {
                        if (currentPiece.isKing()) coordList.add(new int[]{row, column, 1});
                        else coordList.add(new int[]{row, column, 0});
                    }
                }
            }
        }
        return coordList;
    }

    public int getNumberOfWhiteKings() {
        int result = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Pieces currentPiece = getCoordinate(row, column);
                if (currentPiece.isWhite() && currentPiece.isKing()) result++;
            }
        }
        return result;
    }

    public int getNumberOfBlackKings() {
        int result = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Pieces currentPiece = getCoordinate(row, column);
                if (!currentPiece.isWhite() && currentPiece.isKing()) result++;
            }
        }
        return result;
    }

    public int getNumberOfWhiteMen() {
        int result = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Pieces currentPiece = getCoordinate(row, column);
                if (currentPiece.isWhite() && !currentPiece.isKing()) result++;
            }
        }
        return result;
    }

    public int getNumberOfBlackMen() {
        int result = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Pieces currentPiece = getCoordinate(row, column);
                if (!currentPiece.isWhite() && !currentPiece.isKing()) result++;
            }
        }
        return result;
    }

    public Board clone() {
        Pieces[][] clonedCoordinates = new Pieces[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                clonedCoordinates[row][column] = coordinates[row][column];
            }
        }
        return new Board(size, clonedCoordinates);
    }
}
