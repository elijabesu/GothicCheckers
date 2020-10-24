public class Move {
    protected Player player;
    protected int originalRow;
    protected int originalColumn;
    protected int movingMan;
    protected int newRow;
    protected int newColumn;
    protected int newPositionOriginalMan;

    public Move(Player player,
                int originalRow, int originalColumn, int movingMan,
                int newRow, int newColumn, int newMan) {
        this.player = player;
        this.originalRow = originalRow;
        this.originalColumn = originalColumn;
        this.movingMan = movingMan;
        this.newRow = newRow;
        this.newColumn = newColumn;
        this.newPositionOriginalMan = newMan;
    }

    public boolean isValid() {
        if (!basicValidation()) return false;

        int rowDifference = Utils.getDifference(originalRow, newRow);
        int columnDifference = Utils.getDifference(originalColumn, newColumn);

        if (rowDifference == 0 && columnDifference == 1) return true; // left and right
        if (rowDifference == 1 && (columnDifference == 0 | columnDifference == 1)) return true; // rest

        return false;
    }

    protected boolean basicValidation() {
        if (newPositionOriginalMan != 0) return false; // if the position is occupied -> NOPE

        if (player.isWhite()) {
            if (movingMan > 0) return false; // if the WHITE player is trying to move any of the BLACK men -> NOPE
        } else {
            if (movingMan < 0) return false; // if the BLACK player is trying to move any of the WHITE men -> NOPE
        }

        if (movingMan == 2 || movingMan == -2) return true; // if it's a King -> YEP

        // if they are trying to move backwards -> NOPE
        if (movingMan == -1 && newRow > originalRow) return false;
        if (movingMan == 1 && newRow < originalRow) return false;

        // everything else:
        return true;
    }

    public int getOriginalRow() {
        return originalRow;
    }

    public int getOriginalColumn() {
        return originalColumn;
    }

    public int getMan() {
        return movingMan;
    }

    public int getNewRow() {
        return newRow;
    }

    public int getNewColumn() {
        return newColumn;
    }

    @Override
    public String toString() {
        return "" + Columns.values()[originalColumn] + Utils.convertRowForToString(originalRow) + " -> " +
                Columns.values()[newColumn] + Utils.convertRowForToString(newRow) +
                " (" + player.getName() + ", " + Utils.whichMan(movingMan) + ")";
    }
}
