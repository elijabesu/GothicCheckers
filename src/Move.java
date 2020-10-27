public class Move {
    protected Player player;
    protected Man movingMan;
    protected int originalRow;
    protected int originalColumn;
    protected int newRow;
    protected int newColumn;
    protected int newPositionOriginalMan;

    public Move(Player player, Man movingMan,
                int newRow, int newColumn, int newMan) {
        this.player = player;
        this.movingMan = movingMan;
        this.originalRow = movingMan.getRow();
        this.originalColumn = movingMan.getColumn();
        this.newRow = newRow;
        this.newColumn = newColumn;
        this.newPositionOriginalMan = newMan;
    }

    public boolean isValid() { //TODO doesn't work for Kings
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
            if (!movingMan.isWhite()) return false; // if the WHITE player is trying to move any of the BLACK men -> NOPE
        } else {
            if (movingMan.isWhite()) return false; // if the BLACK player is trying to move any of the WHITE men -> NOPE
        }

        if (movingMan.isKing()) return true; // if it's a King -> YEP

        // if they are trying to move backwards -> NOPE
        if (movingMan.isWhite() && newRow > originalRow) return false;
        if (!movingMan.isWhite() && newRow < originalRow) return false;

        // everything else:
        return true;
    }

    public int getOriginalRow() {
        return originalRow;
    }

    public int getOriginalColumn() {
        return originalColumn;
    }

    public Man getMan() {
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
        return toStringWithoutPlayer() + " (" + player.getName() + ", " + Utils.whichMan(movingMan.getValue().getValue()) + ")";
    }

    public boolean needsPromotion() {
        if (movingMan.getValue() == Pieces.BLACK && newRow == 7) return true;
        if (movingMan.getValue() == Pieces.WHITE && newRow == 0) return true;
        return false;
    }

    public String toStringWithoutPlayer() {
        return "" + Columns.values()[originalColumn] + Utils.convertRowForToString(originalRow) + " -> " +
                Columns.values()[newColumn] + Utils.convertRowForToString(newRow);
    }
}
