public enum Symbols {
    EMPTY("     "),
    WHITE("  o  "),
    BLACK("  x  "),
    WHITE_KING("  O  "),
    BLACK_KING("  X  "),
    LINE("\n      -------------------------------------------------\n"),
    VERTICAL("|");

    private String symbol;

    Symbols(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
