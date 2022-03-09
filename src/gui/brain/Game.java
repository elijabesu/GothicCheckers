package gui.brain;
import gui.BoardPanel;
import shared.*;

import javax.swing.*;
import java.util.List;

public class Game {
    private final Rules rules;
    private int movesWithoutJump;
    private int difficulty;
    private int status = 1; // 0 paused, 1 in progress

    public Game(BoardPanel boardPanel) {
        rules = new Rules(boardPanel);
        movesWithoutJump = 0;
        difficulty = 2; // default being hard

        System.out.println("Game started."); // TODO delete
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean shouldEnd(Player[] players) {
        return movesWithoutJump == 30 || players[0].getPoints() == 16 || players[1].getPoints() == 16;
    }

    // MOVING
    public boolean canPlayerMoveThis(Player player, Piece movingMan) {
        return rules.canPlayerMoveThis(player, movingMan);
    }

    public boolean checkValidityAndMove(Player player, Piece movingMan, Piece jumpedMan,
                                        Coordinate originalCoordinate, Coordinate jumpedCoordinate, Coordinate newCoordinate,
                                        DefaultListModel<Move> historyDlm) {
        if (!canPlayerMoveThis(player, movingMan)) {
            System.out.println("Player cannot move this."); // TODO delete
            return false;
        }
        if (status == 0) return false;
        System.out.println("Player can move this."); // TODO delete
        if (jumpedCoordinate != null && jumpedMan != null) {
            return jump(player, movingMan, jumpedMan,
                    originalCoordinate, jumpedCoordinate, newCoordinate,
                    historyDlm) != null;
        }

        if (move(player, movingMan, originalCoordinate, newCoordinate, historyDlm) == null) return false;

        System.out.println(movesWithoutJump); // TODO delete
        return true;
    }

    private Move move(Player player, Piece movingMan,
                     Coordinate originalCoordinate, Coordinate newCoordinate,
                     DefaultListModel<Move> historyDlm) {
        Move move = new Move(player, movingMan, originalCoordinate, newCoordinate);
        System.out.println("Generated move: " + move); // TODO delete

        if (!(rules.isValid(player, movingMan, move))) {
            System.out.println("Move is not valid."); // TODO delete
            return null;
        }

        historyDlm.addElement(move);
        ++movesWithoutJump;

        return move;
    }

    private Jump jump(Player player, Piece movingMan, Piece jumpedMan,
                      Coordinate originalCoordinate, Coordinate jumpedCoordinate, Coordinate newCoordinate,
                      DefaultListModel<Move> historyDlm) {
        Jump jump = new Jump(player, movingMan, jumpedMan,
                originalCoordinate, jumpedCoordinate, newCoordinate);
        System.out.println("Generated jump: " + jump); // TODO delete

        if (!(rules.isValid(player, movingMan, jump))) {
            System.out.println("Jump is not valid."); // TODO delete
            return null;
        }

        historyDlm.addElement(jump);
        player.addPoint();
        System.out.println(player); // TODO delete
        movesWithoutJump = 0;

        return jump;
    }

    public boolean needsPromotion(Piece movingMan, Coordinate coordinate) {
        return rules.needsPromotion(movingMan, coordinate);
    }

    public List<Move> hint(Player player, Piece movingMan, Coordinate originalCoordinate) {
        // TODO hinting once minimax is implemented
        return null;
    }
}
