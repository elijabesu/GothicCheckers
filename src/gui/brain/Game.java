package gui.brain;
import gui.BoardPanel;
import shared.*;

import javax.swing.*;

public class Game {
    private final Rules rules;
    private int movesWithoutJump;
    private int difficulty;
    private boolean playerBool;

    public Game(BoardPanel boardPanel) {
        rules = new Rules(boardPanel);
        movesWithoutJump = 0;
        playerBool = true;
        difficulty = 2; // default being hard

        System.out.println("Game started."); // TODO delete
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        System.out.println("Difficulty set to " + difficulty + "."); // TODO delete
    }

    public boolean getPlayerBool() {
        return playerBool;
    }

    public void switchPlayers() {
        playerBool = !playerBool;
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
                                        DefaultListModel<String> historyDlm) {
        if (!canPlayerMoveThis(player, movingMan)) {
            System.out.println("Player cannot move this."); // TODO delete
            return false;
        }
        System.out.println("Player can move this."); // TODO delete
        if (jumpedCoordinate != null && jumpedMan != null) {
            if (jump(player, movingMan, jumpedMan,
                    originalCoordinate, jumpedCoordinate, newCoordinate,
                    historyDlm) == null) return false;
            return true;
        }

        if (move(player, movingMan, originalCoordinate, newCoordinate, historyDlm) == null) return false;

        System.out.println(movesWithoutJump); // TODO delete
        return true;
    }

    private Move move(Player player, Piece movingMan,
                     Coordinate originalCoordinate, Coordinate newCoordinate,
                     DefaultListModel<String> historyDlm) {
        Move move = new Move(player, movingMan, originalCoordinate, newCoordinate);
        System.out.println("Generated move: " + move); // TODO delete

        if (!(rules.isValid(player, movingMan, move))) {
            System.out.println("Move is not valid."); // TODO delete
            return null;
        }

        afterMove(move, historyDlm);
        ++movesWithoutJump;

        System.out.println("Moved " + move + "."); // TODO delete
        return move;
    }

    private Jump jump(Player player, Piece movingMan, Piece jumpedMan,
                      Coordinate originalCoordinate, Coordinate jumpedCoordinate, Coordinate newCoordinate,
                      DefaultListModel<String> historyDlm) {
        Jump jump = new Jump(player, movingMan, jumpedMan,
                originalCoordinate, jumpedCoordinate, newCoordinate);
        System.out.println("Generated jump: " + jump); // TODO delete

        if (!(rules.isValid(player, movingMan, jump))) {
            System.out.println("Jump is not valid."); // TODO delete
            return null;
        }

        afterMove(jump, historyDlm);
        player.addPoint();
        System.out.println(player); // TODO delete
        movesWithoutJump = 0;

        System.out.println("Jumped " + jump + "."); // TODO delete
        return jump;
    }

    private void afterMove(Move move, DefaultListModel<String> historyDlm) {
        rules.maybePromote(move.getMan(), move.getNew());
        historyDlm.addElement(move.toString());
    }
}
