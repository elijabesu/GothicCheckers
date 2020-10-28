public enum Pieces {
    WHITE(-1),
    BLACK(1),
    WHITE_KING(-2),
    BLACK_KING(2);

    private final int numberValue;

    Pieces(int numberValue) {
        this.numberValue = numberValue;
    }

    public int getNumberValue() {
        return numberValue;
    }

    public boolean isSameColourAs(int value) {
        if (isWhite(value) && isWhite(this.numberValue)) return true;
        if (!isWhite(value) && !isWhite(this.numberValue)) return true;
        return false;
    }

    private boolean isWhite(int value) {
        return (value == WHITE.numberValue || value == WHITE_KING.numberValue);
    }
}
