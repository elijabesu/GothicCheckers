public class Jump extends Move {
    private final int jumpedRow;
    private final int jumpedColumn;
    private final int jumpedMan;

    public Jump(int originalRow, int originalColumn, int movingMan,
                int jumpedRow, int jumpedColumn, int jumpedMan,
                int newRow, int newColumn, int newMan) {
        super(originalRow, originalColumn, movingMan, newRow, newColumn, newMan);
        this.jumpedRow = jumpedRow;
        this.jumpedColumn = jumpedColumn;
        this.jumpedMan = jumpedMan;
    }

    @Override
    public boolean isValid(boolean player) {
        if (!basicValidation(player)) return false;

        int rowDifference = Utils.getDifference(originalRow, newRow);
        int columnDifference = Utils.getDifference(originalColumn, newColumn);

        if (player) {
            if (jumpedMan < 0) return false; // if the WHITE player is trying to jump over another WHITE man -> NOPE
        } else {
            if (jumpedMan > 0) return false; // if the BLACK player is trying to jump over another BLACK man -> NOPE
        }

        if (rowDifference == 0 && columnDifference == 2) return true; // left and right
        if (rowDifference == 2 && (columnDifference == 0 || columnDifference == 2)) return true; // rest

        return false;
    }

    public int getJumpedRow() {
        return jumpedRow;
    }

    public int getJumpedColumn() {
        return jumpedColumn;
    }

}
