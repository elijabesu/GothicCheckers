package shared;

import ui.*;
import gui.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    /*
    All utility methods.
     */

    private static int getRow(char row) {
        int inputRow = Integer.parseInt(String.valueOf(row)); // 1 - 8
        return convertRow(inputRow);
    }

    private static int getColumn(char column) {
        return Columns.valueOf(String.valueOf(column)).ordinal();
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

    private static int getJumpedColumn(int original, int next) {
        int difference = Math.abs(original - next);
        if (difference == 0) return original;
        if (difference != 2) return -1;
        if (original > next) return original - 1;
        return original + 1;
    }

    public static Coordinate getJumpedCoordinate(boolean isWhite, Coordinate original, Coordinate next) {
        int jumpedRow = getJumpedRow(isWhite, original.getRow(), next.getRow());
        int jumpedColumn = getJumpedColumn(original.getColumn(), next.getColumn());

        if (jumpedRow == -1 || jumpedColumn == -1) return null;
        Coordinate jumpedCoordinate = new Coordinate(jumpedRow, jumpedColumn);
        if (jumpedCoordinate.equals(original) || jumpedCoordinate.equals(next)) return null;
        return jumpedCoordinate;
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
        return - (row - 8);
    }

    public static int convertRowToString(int row) {
        return 8 - row;
    }
    public static String convertColumnToString(int column) {
        String[] strings = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
        return strings[column];
    }

    public static String whichMan(Pieces man) {
        return switch (man) {
            case BLACK -> "x";
            case BLACK_KING -> "X";
            case WHITE -> "o";
            case WHITE_KING -> "O";
            default -> "-";
        };
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

    public static ui.Jump convertMoveIntoJump(ui.Move move) {
        return new ui.Jump(move.getPlayer(), move.getMan(), move.getOriginal(),
                move.getJumped(), move.getJumpedMan(), move.getNew());
    }

    public static gui.brain.Jump convertMoveIntoJump(gui.brain.Move move) {
        return new gui.brain.Jump(move.getPlayer(), move.getMan(), move.getJumpedMan(),
                move.getOriginal(), move.getJumped(), move.getNew());
    }

    public static double getValueDependingOnColour(boolean isWhite, int white, int whiteKings,
                                                int black, int blackKings, double kingWeight) {
        double whiteValue = whiteKings * kingWeight + white;
        double blackValue = blackKings * kingWeight + black;

        if (isWhite) return whiteValue - blackValue;
        return blackValue - whiteValue;
    }

    public static double getValueDependingOnColour(boolean isWhite, double one, double two) {
        if (isWhite) return Math.min(one, two);
        return Math.max(one, two);
    }

    public static double getValueDependingOnColour(boolean isWhite, List<Double> collection) {
        if (isWhite) return Collections.min(collection);
        return Collections.max(collection);
    }

    public static String checkExtension(File file) {
        String path = file.getAbsolutePath();
        if (!path.endsWith(".txt")) path += ".txt";
        return path;
    }
}
