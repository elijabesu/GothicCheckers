import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class History {
    private final List<Move> history;

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
        s.append("~~ HISTORY ~~");

        for (Move move: history) {
            s.append(System.lineSeparator());
            s.append(move.toString());
        }

        return s.toString();
    }

    public void save() throws IOException {
        // TODO saving mechanism
        Files.write(Paths.get(System.getProperty("user.dir") + "\\saves\\" +
                        String.valueOf(java.time.LocalDateTime.now()).split(".")[0]
                                .replaceAll(":", ".") + ".txt"),
                history.stream().map(move -> move.toString()).collect(Collectors.toList()));
    }

    public void load(String fileName) {
        // TODO loading mechanism
    }
}
