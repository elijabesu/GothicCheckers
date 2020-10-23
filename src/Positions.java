public class Positions {
    private boolean occupied;
    private int[][] coordinates;

    public Positions(int size) {
        coordinates = new int[size][size];
        occupied = false;
    }

    public int getValue(int row, int column) {
        return coordinates[row][column];
    }

    public void placeMan(int row, int column, int value) {
        if (coordinates[row][column] == 0) occupied = true;
        coordinates[row][column] = value;
    }

    public void removeMan(int row, int column) {
        coordinates[row][column] = 0;
        occupied = false;
    }
}
