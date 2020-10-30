import java.util.List;

public class Hint {
    private final List<Move> possibilities;

    public Hint(Player player, Pieces movingMan, int originalRow, int originalColumn, Rules rules) {
        this.possibilities = rules.getPossibilities(player, movingMan, originalRow, originalColumn);
    }

    @Override
    public String toString() {
        if (possibilities == null || possibilities.size() == 0) return "No possible moves.";

        StringBuilder str = new StringBuilder();
        str.append("Possible moves:");

        for (Move move: possibilities) {
            str.append(System.lineSeparator());
            str.append(move.toStringWithoutPlayer());
        }

        return str.toString();
    }
}
