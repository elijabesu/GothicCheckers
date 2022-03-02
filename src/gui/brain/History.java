package gui.brain;

import shared.*;

import javax.swing.*;
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

    public String getMostRecentMove() {
        return history.get(history.size() - 1).toString();
    }

    public void save(String fileName) throws IOException {
        Files.write(Paths.get(fileName),
                history.stream().map(String::valueOf).collect(Collectors.toList()));
    }

    public void load(Game game, Player[] players, String fileName) throws IOException {
        System.out.println("Loaded"); // TODO history loading
//        Files.lines(Paths.get(System.getProperty("user.dir") + "/saves/" + fileName))
//                .map(row -> row.split(" "))
//                .filter(parts -> parts.length >= 5)
//                .forEach(parts -> {
//                    String movement = parts[0] + " " + parts[1] + " " + parts[2];
//                    Player player = player1;
//                    boolean which = true;
//                    if (parts[4].toLowerCase().contains("x")) {
//                        player = player2;
//                        which = false;
//                    }
//                    List<Coordinate> coordinates = Utils.getCoordinates(movement, which);
//                    Pieces man = game.getManByPosition(coordinates.get(0));
//                    if (man.isKing() || coordinates.size() < 3) game.move(player, man, coordinates);
//                    else game.jump(player, man, coordinates);
//                    game.switchPlayers();
//                });
    }
}

