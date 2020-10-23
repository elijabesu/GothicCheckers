public class Move {
    protected int originalRow;
    protected int originalColumn;
    protected int movingMan;
    protected int newRow;
    protected int newColumn;
    protected int newPositionOriginalMan;

    public Move(int originalRow, int originalColumn, int movingMan,
                int newRow, int newColumn, int newMan) {
        this.originalRow = originalRow;
        this.originalColumn = originalColumn;
        this.movingMan = movingMan;
        this.newRow = newRow;
        this.newColumn = newColumn;
        this.newPositionOriginalMan = newMan;
    }

    public boolean isValid(boolean player) {
        if (!basicValidation(player)) return false;

        int rowDifference = Utils.getDifference(originalRow, newRow);
        int columnDifference = Utils.getDifference(originalColumn, newColumn);

        if (rowDifference == 0 && columnDifference == 1) return true; // left and right
        if (rowDifference == 1 && (columnDifference == 0 | columnDifference == 1)) return true; // rest

        return false;
    }

    protected boolean basicValidation(boolean player) {
        if (newPositionOriginalMan != 0) return false; // if the position is occupied -> NOPE

        if (player) {
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
        StringBuilder s = new StringBuilder();

        s.append(Columns.values()[originalColumn]);
        s.append(originalRow);

        s.append(" -> ");

        s.append(Columns.values()[newColumn]);
        s.append(newRow);

        return s.toString();
    }
}
