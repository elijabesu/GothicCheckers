import java.util.ArrayList;
import java.util.List;

public class Hint {
    private List<Move> possibilities;
    private Man movingMan;

    public Hint(Player player, Man movingMan) {
        this.possibilities = new ArrayList<>();
        this.movingMan = movingMan;
    }

    private void generateMoves() {
        // TODO
    }

    private void generateJumps() {
        // TODO
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Move move: possibilities) {
            str.append(move.toString());
        }
        return str.toString();
    }
}
