package ui;

import shared.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class History {
    /*
    Displaying, storing, and loading the history.
     */
    private final List<Move> history;

    public History() {
        history = new ArrayList<>();
    }

    public void add(Move move) {
        history.add(move);
    }

    public void save() throws IOException {
        Files.write(Paths.get(System.getProperty("user.dir") + "/saves/" +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss"))
                        + ".txt"),
                history.stream().map(String::valueOf).collect(Collectors.toList()));
    }

    public void load(Game game, Player player1, Player player2, String fileName) throws IOException {
        Files.lines(Paths.get(System.getProperty("user.dir") + "/saves/" + fileName))
                .map(row -> row.split(" "))
                .filter(parts -> parts.length >= 5)
                .forEach(parts -> {
                    String movement = parts[0] + " " + parts[1] + " " + parts[2];
                    Player player = player1;
                    boolean which = true;
                    if (parts[4].toLowerCase().contains("x")) {
                        player = player2;
                        which = false;
                    }
                    List<Coordinate> coordinates = Utils.getCoordinates(movement, which);
                    Pieces man = game.getManByPosition(coordinates.get(0));
                    if (man.isKing() || coordinates.size() < 3) game.move(player, man, coordinates);
                    else game.jump(player, man, coordinates);
                    game.switchPlayers();
                });
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
}
