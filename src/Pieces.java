public enum Pieces {
    EMPTY(0),
    WHITE(-1),
    BLACK(1),
    WHITE_KING(-2),
    BLACK_KING(2);

    private final int value;

    Pieces(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
