import java.util.List;

public class Hint {
    private final List<Move> possibilities;

    public Hint(Player player, Man movingMan, Rules rules) {
        this.possibilities = rules.getPossibilities(player, movingMan);
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
