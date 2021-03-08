import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static int getColumn(char column) {
        return Columns.valueOf(String.valueOf(column)).ordinal();
    }

    private static int getRow(char row) {
        int inputRow = Integer.parseInt(String.valueOf(row)); // 1 - 8
        return convertRow(inputRow);
    }

    private static int getJumpedColumn(int original, int next) {
        int difference = Math.abs(original - next);
        if (difference == 0) return original;
        if (difference != 2) return -1;
        if (original > next) return original - 1;
        return original + 1;
    }

    private static int getJumpedRow(boolean bool, int original, int next) {
        int difference = Math.abs(original - next);
        if (difference == 0) return original;
        if (difference != 2) return -1;
        if (bool) { // if WHITE
            if (original > next) return original - 1;
        }
        return original + 1;
    }

    public static Coordinate getCoordinate(String string, int row, int column) {
        return new Coordinate(getRow(string.charAt(row)), getColumn(string.charAt(column)));
    }

    private static List<Coordinate> getCoordinates(String string) {
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(getCoordinate(string, 1, 0));
        coordinates.add(getCoordinate(string, 7, 6));
        return coordinates;
    }

    public static List<Coordinate> getCoordinates(String string, boolean bool) {
        List<Coordinate> coordinates = new ArrayList<>(getCoordinates(string));
        coordinates.add(new Coordinate(getJumpedRow(bool, coordinates.get(0).getRow(), coordinates.get(1).getRow()),
                getJumpedColumn(coordinates.get(0).getColumn(), coordinates.get(1).getColumn())));
        return coordinates;
    }

    private static int convertRow(int row) {
        return -(row - 8);
    }

    public static String whichMan(Pieces man) {
        switch (man) {
            case BLACK: return "x";
            case BLACK_KING: return "X";
            case WHITE: return "o";
            case WHITE_KING: return "O";
        }
        return "-";
    }

    public static int convertRowToString(int row) {
        return 8 - row;
    }

    public static List<Integer> generateListOfAvailable(int middle, int extra) {
        List<Integer> available = new ArrayList<>();
        for (int i: new int[] {middle - (extra + 1), middle, middle + (extra + 1)}) {
            if (i >= 0 && i <= 7) available.add(i);
        }
        return available;
    }

    public static boolean listOfArraysContains(List<Coordinate> coordinates, Coordinate lookingFor) {
        for (Coordinate coordinate: coordinates) {
            if (coordinate.equals(lookingFor)) return true;
        }
        return false;
    }

    public static void ignorePositions(Coordinate starting, Coordinate next, List<Coordinate> skipPositions) {
        if (starting.getRow() < next.getRow()) {
            for (int row = starting.getRow(); row < 8; row++) {
                ignoreRow(row, starting.getColumn(), next.getColumn(), skipPositions);
            }
        } else {
            for (int row = starting.getRow(); row > 0; row--) {
                ignoreRow(row, starting.getColumn(), next.getColumn(), skipPositions);
            }
        }
    }

    private static void ignoreRow(int row, int startingColumn, int nextColumn, List<Coordinate> skipPositions) {
        if (startingColumn < nextColumn) {
            for (int column = startingColumn; column < 8; column++) {
                skipPositions.add(new Coordinate(row, column));
            }
        } else {
            for (int column = startingColumn; column > 0; column--) {
                skipPositions.add(new Coordinate(row, column));
            }
        }
    }

    public static boolean containsMinusCoordinate(List<Coordinate> coordinates) {
        for (Coordinate coordinate: coordinates) {
            if (coordinate.getRow() < 0 || coordinate.getColumn() < 0) return true;
        }
        return false;
    }
}
