import java.util.ArrayList;
import java.util.List;

public class History {
    private List<Move> history;

    public History() {
        history = new ArrayList<>();
    }

    public void add(Move move) {
        history.add(move);
    }

    @Override
    public String toString() {
        if (history.size() == 0) return "~~ HISTORY ~~\nNo records.";
        StringBuilder s = new StringBuilder();
        String lineBreak = "\n";
        s.append("~~ HISTORY ~~");

        for (Move move: history) {
            s.append(lineBreak);
            s.append(move.toString());
        }

        return s.toString();
    }
}
