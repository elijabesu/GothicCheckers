public enum Pieces {
    WHITE(),            // ordinal: 0
    WHITE_KING(),       // ordinal: 1
    EMPTY(),            // ordinal: 2
    BLACK(),            // ordinal: 3
    BLACK_KING();       // ordinal: 5

    public boolean isSameColourAs(Pieces value) {
        if (isWhite(value) && isWhite()) return true;
        if (!isWhite(value) && !isWhite()) return true;
        return false;
    }

    public boolean isWhite() {
        return isWhite(this);
    }

    private boolean isWhite(Pieces value) {
        return value == WHITE || value == WHITE_KING;
    }

    public boolean isKing() {
        return this == BLACK_KING || this == WHITE_KING;
    }
}
