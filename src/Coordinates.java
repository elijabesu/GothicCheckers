public class Coordinates {
    private final int[][] coordinates;

    public Coordinates(int size) {
        coordinates = new int[size][size];
    }

    public int getValue(int row, int column) {
        return coordinates[row][column];
    }

    public boolean isOccupied(int row, int column) {
        return !(coordinates[row][column] == 0);
    }

    public void placeMan(int row, int column, int value) {
        coordinates[row][column] = value;
    }

    public void removeMan(int row, int column) {
        coordinates[row][column] = 0;
    }
}
