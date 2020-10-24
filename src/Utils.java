public class Utils {
    public static int getDifference(int x, int y) {
        return Math.abs(x - y);
    }

    public static int getColumn(char column) {
        return Columns.valueOf(String.valueOf(column)).ordinal();
    }

    public static int getRow(char row) {
        int inputRow = Integer.parseInt(String.valueOf(row)); // 1 - 8
        return convertRow(inputRow);
    }

    public static int getJumpedColumn(int original, int next) {
        int difference = Utils.getDifference(original, next);
        if (difference == 0) return original;
        if (difference != 2) return -1;
        return original + 1;
    }

    public static int getJumpedRow(boolean bool, int original, int next) {
        int difference = Utils.getDifference(original, next);
        if (difference == 0) return original;
        if (difference != 2) return -1;
        if (bool) return convertRow(original + 1);
        return original + 1;
    }

    public static int[] getCoordinates(String string) {
        int originalColumn = getColumn(string.charAt(0)); // A - H
        int originalRow = getRow(string.charAt(1)); // 1 - 8

        int nextColumn = getColumn(string.charAt(6)); // A - H
        int nextRow = getRow(string.charAt(7)); // 1 - 8

        return new int[] {originalRow, originalColumn, nextRow, nextColumn, 0, 0};
    }

    public static int[] getCoordinates(String string, boolean bool) {
        int[] coordinates = getCoordinates(string);

        coordinates[4] = Utils.getJumpedRow(bool, coordinates[0], coordinates[2]);
        coordinates[5] = Utils.getJumpedColumn(coordinates[1], coordinates[3]);

        return coordinates;
    }

    private static int convertRow(int row) {
        int[] rows = new int[] {8, 7, 6, 5, 4, 3, 2, 1};
        for (int i = 0; i < rows.length; i++) {
            if (rows[i] == row) return i;
        }
        return -1;
    }

    public static String whichMan(int man) {
        switch (man) {
            case 1: return "x";
            case 2: return "X";
            case -1: return "o";
            case -2: return "O";
        }
        return "-";
    }

    public static boolean containsMinus(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] < 0) return true;
        }
        return false;
    }

    public static int convertRowForToString(int row) {
        int[] rows = new int[] {8, 7, 6, 5, 4, 3, 2, 1};
        return rows[row];
    }
}
