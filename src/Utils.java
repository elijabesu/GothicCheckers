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

    public static int[] getCoordinate(String string, int row, int column) {
        return new int[] {getRow(string.charAt(row)), getColumn(string.charAt(column))};
    }

    private static int[] getCoordinates(String string) {
        int[] original = getCoordinate(string, 1, 0); // row (1 - 8), column (A - H)
        int[] next = getCoordinate(string, 7, 6);

        return new int[] {original[0], original[1], next[0], next[1], 0, 0};
    }

    public static int[] getCoordinates(String string, boolean bool) {
        int[] coordinates = getCoordinates(string);

        coordinates[4] = getJumpedRow(bool, coordinates[0], coordinates[2]);
        coordinates[5] = getJumpedColumn(coordinates[1], coordinates[3]);

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

    public static boolean containsMinus(int[] array) {
        for (int i: array) {
            if (i < 0) return true;
        }
        return false;
    }

    public static int convertRowForToString(int row) {
        int[] rows = new int[] {8, 7, 6, 5, 4, 3, 2, 1};
        return rows[row];
    }

    public static int[] generateArrayOfThree(int middle) {
        return new int[] {middle - 1, middle, middle + 1};
    }

    public static boolean listOfArraysContains(List<int[]> listOfIntArrays, int row, int column) {
        for (int[] arrayInList: listOfIntArrays) {
            if (arrayInList[0] == row && arrayInList[1] == column) return true;
        }
        return false;
    }

    public static void ignorePositions(int startingRow, int startingColumn, int nextRow, int nextColumn, List<int[]> skipPositions) {
        if (startingRow < nextRow) {
            for (int row = startingRow; row < 8; row++) {
                ignoreRow(row, startingColumn, nextColumn, skipPositions);
            }
        } else {
            for (int row = startingRow; row > 0; row--) {
                ignoreRow(row, startingColumn, nextColumn, skipPositions);
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
}
