package gui.brain;
import shared.*;

import javax.swing.*;
import java.awt.*;

public class Game {
//    private final BoardPanel board;
//    private final Rules rules;
//    private final History history;
    private int movesWithoutJump;
    private int difficulty;
    private boolean playerBool;

    private Piece[] pieces;

//    public Game(BoardPanel boardPanel) {
//        board = boardPanel;
//        rules = new Rules(board);
    public Game() {
//        history = new History();
        movesWithoutJump = 0;
        playerBool = true;
        difficulty = 2; // default being hard

        System.out.println("Game started."); // TODO delete

//        generateMen();
    }

    public void setPieces(Piece[] pieces) { this.pieces = pieces; }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        System.out.println("Difficulty set to " + difficulty); // TODO delete
    }

    public boolean getPlayerBool() {
        return playerBool;
    }

    public void switchPlayers() {
        playerBool = !playerBool;
    }

    public boolean shouldEnd(Player player1, Player player2) {
        return movesWithoutJump == 30 || player1.getPoints() == 16 || player2.getPoints() == 16;
    }

    public boolean canPlayerMoveThis(Piece piece, Player player) {
        return player.isWhite() == piece.isWhite();
    }

    public boolean canPlayerReplaceThis(Piece original, Piece moving) {
        return moving.isWhite() != original.isWhite();
    }

    public boolean checkValidityAndMove(Piece originalPiece, Piece movingPiece,
                                        Point originalPosition, Point newPosition, Player player,
                                        DefaultListModel historyDlm) {
        if (!canPlayerReplaceThis(originalPiece, movingPiece)) return false;
        return checkValidityAndMove(movingPiece, originalPosition, newPosition, player, historyDlm);
    }

    public boolean checkValidityAndMove(Piece movingPiece,
                                        Point originalPosition, Point newPosition, Player player,
                                        DefaultListModel historyDlm) {
        if (!canPlayerMoveThis(movingPiece, player)) return false;

        Move move = new Move(player, movingPiece, originalPosition, newPosition);
        System.out.println("Moved: " + move.toString()); // TODO delete
        historyDlm.addElement(move.toStringWithoutPlayer());
        if (move.isJump()) {
            player.addPoint();
            movesWithoutJump = 0;
        }
        else ++movesWithoutJump;
        return true;
    }
}
