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
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.addAll(getCoordinates(string));
        coordinates.add(new Coordinate(getJumpedRow(bool, coordinates.get(0).getRow(), coordinates.get(1).getRow()),
                getJumpedColumn(coordinates.get(0).getColumn(), coordinates.get(1).getColumn())));
        return coordinates;
    }

    private static int convertRow(int row) {
        int[] rows = new int[] {8, 7, 6, 5, 4, 3, 2, 1};
        for (int i = 0; i < rows.length; i++) {
            if (rows[i] == row) return i;
        }
        return -1;
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

    public static int convertRowForToString(int row) {
        int[] rows = new int[] {8, 7, 6, 5, 4, 3, 2, 1};
        return rows[row];
    }

    public static List<Integer> generateListOfAvailable(int middle) {
        int[] arrayOfThree = new int[] {middle - 1, middle, middle + 1};
        List<Integer> available = new ArrayList<>();

        for (int i = 0; i < arrayOfThree.length; i++) {
            if (arrayOfThree[i] >= 0 || arrayOfThree[i] <= 7) available.add(arrayOfThree[i]);
        }
        return available;
    }

    public static boolean listOfArraysContains(List<int[]> listOfIntArrays, int row, int column) {
        for (int[] arrayInList: listOfIntArrays) {
            if (arrayInList[0] == row && arrayInList[1] == column) return true;
        }
        return false;
    }

    public static void ignorePositions(Coordinate starting, Coordinate next, List<int[]> skipPositions) {
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

    private static void ignoreRow(int row, int startingColumn, int nextColumn, List<int[]> skipPositions) {
        if (startingColumn < nextColumn) {
            for (int column = startingColumn; column < 8; column++) {
                skipPositions.add(new int[] {row, column});
            }
        } else {
            for (int column = startingColumn; column > 0; column--) {
                skipPositions.add(new int[] {row, column});
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
