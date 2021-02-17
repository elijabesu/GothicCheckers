public enum Pieces {
    WHITE(),
    WHITE_KING(),
    EMPTY(),
    BLACK(),
    BLACK_KING();

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
