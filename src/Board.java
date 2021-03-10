import java.util.ArrayList;
import java.util.List;

public class Board {
    /*
    Storing information about the playing board.
     */
    private final StringBuilder boardString;
    private final int size;
    private final Pieces[][] coordinates;

    public Board(int size) {
        this.size = size;
        this.boardString = new StringBuilder();
        this.coordinates = new Pieces[size][size];
    }

    public int getSize() {
        return size;
    }

    public void placeMan(Pieces man, Coordinate coordinate) {
        coordinates[coordinate.getRow()][coordinate.getColumn()] = man;
    }

    public void removeMan(Coordinate coordinate) {
        coordinates[coordinate.getRow()][coordinate.getColumn()] = Pieces.EMPTY;
    }

    public Pieces getCoordinate(Coordinate coordinate) {
        return coordinates[coordinate.getRow()][coordinate.getColumn()];
    }

    public boolean isOccupied(Coordinate coordinate) {
        return (coordinates[coordinate.getRow()][coordinate.getColumn()] != Pieces.EMPTY);
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

    public void moveOrJump(Move move) {
        if (move.isJump()) jump(Utils.convertMoveIntoJump(move));
        else move(move);
    }

    public void undoMoveOrJump(Move move) {
        if (move.isJump()) undoJump(Utils.convertMoveIntoJump(move));
        else undoMove(move);
    }

    private void move(Move move) {
        removeMan(move.getOriginal());
        placeMan(move.getMan(), move.getNew());
    }

    private void undoMove(Move move) {
        removeMan(move.getNew());
        placeMan(move.getMan(), move.getOriginal());
    }

    private void jump(Jump jump) {
        removeMan(jump.getOriginal());
        removeMan(jump.getJumped());
        placeMan(jump.getMan(), jump.getNew());
    }

    private void undoJump(Jump jump) {
        placeMan(jump.getMan(), jump.getOriginal());
        placeMan(jump.getJumpedMan(), jump.getJumped());
        removeMan(jump.getNew());
    }

    public void promote(Pieces man, Coordinate coordinate) {
        if (man.isWhite()) coordinates[coordinate.getRow()][coordinate.getColumn()] = Pieces.WHITE_KING;
        else coordinates[coordinate.getRow()][coordinate.getColumn()] = Pieces.BLACK_KING;
    }

    public List<Coordinate> getCoordinatesList(Player player) {
        List<Coordinate> coordinates = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Coordinate coordinate = new Coordinate(row, column);
                Pieces currentPiece = getCoordinate(coordinate);
                if (currentPiece != Pieces.EMPTY) {
                    if (currentPiece.isWhite() && player.isWhite()) {
                        coordinates.add(coordinate);
                    } else if (!currentPiece.isWhite() && !player.isWhite()) {
                        coordinates.add(coordinate);
                    }
                }
            }
        }
        return coordinates;
    }

    public int getNumberOf(boolean white, boolean king) {
        int result = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Pieces currentPiece = getCoordinate(new Coordinate(row, column));
                if (white == currentPiece.isWhite() && king == currentPiece.isKing()) result++;
            }
        }
        return result;
    }
}
