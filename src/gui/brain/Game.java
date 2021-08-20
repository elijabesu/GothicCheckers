package gui.brain;

import shared.*;
import ui.Jump;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageFilter;

public class Game {
//    private final BoardPanel board;
//    private final Rules rules;
    private final History history;
    private int movesWithoutJump;
    private int difficulty;
    private boolean playerBool;

    private ImageIcon[] pieces;

//    public Game(BoardPanel boardPanel) {
//        board = boardPanel;
//        rules = new Rules(board);
    public Game() {
        history = new History();
        movesWithoutJump = 0;
        playerBool = true;
        difficulty = 2; // default being hard

//        generateMen();
    }

    public void setPieces(ImageIcon[] pieces) { this.pieces = pieces; }

    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public boolean getPlayerBool() {
        return playerBool;
    }

    public void switchPlayers() {
        playerBool = !playerBool;
    }

//    public Pieces getManByPosition(Coordinate coordinate) {
//        return board.getCoordinate(coordinate);
//    }

    public boolean shouldEnd(Player player1, Player player2) {
        return movesWithoutJump == 30 || player1.getPoints() == 16 || player2.getPoints() == 16;
    }

    // MOVING // TODO
//    public Move move(Player player, Pieces movingMan, List<Coordinate> coordinates) {
//        if (rules.isJumpingPossible(player, movingMan, coordinates.get(0))) return null;
//        Move move = new Move(player, movingMan, coordinates.get(0), // the man we are currently moving
//                coordinates.get(1)); // the position where we want to move
//
//        if (!(rules.isValid(player, movingMan, move))) return null;
//        afterMove(move);
//        return move;
//    }
//
//    public Move moveKing(Player player, Pieces movingMan, List<Coordinate> coordinates) {
//        if (rules.isJumpingPossible(player, movingMan, coordinates.get(0))) return null;
//        Move maybeMove = rules.getPossibleMoves(player, movingMan, coordinates.get(0)).stream()
//                .filter(move -> move.getNew().getRow() == coordinates.get(1).getRow() &&
//                        move.getNew().getColumn() == coordinates.get(1).getColumn())
//                .findFirst().orElse(null);
//        if (maybeMove != null) {
//            afterMove(maybeMove);
//            return maybeMove;
//        }
//        return null;
//    }

    // JUMPING // TODO
//    public Jump jump(Player player, Pieces movingMan, List<Coordinate> coordinates) {
//        Pieces jumpedMan = getManByPosition(coordinates.get(2));
//        if (jumpedMan == null) return null;
//
//        Jump jump = new Jump(player, movingMan, coordinates.get(0), // the man we are currently moving
//                coordinates.get(2), jumpedMan,
//                coordinates.get(1)); // the position where we want to move
//
//        if (!(rules.isValid(player, movingMan, jump))) return null;
//        afterJump(player, jump);
//        return jump;
//    }

//    public Jump jumpKing(Player player, Pieces movingMan, List<Coordinate> coordinates) {
//        Jump maybeJump = rules.getPossibleJumps(player, movingMan, coordinates.get(0)).stream()
//                .filter(jump -> jump.getNew().getRow() == coordinates.get(1).getRow() &&
//                        jump.getNew().getColumn() == coordinates.get(1).getColumn())
//                .findFirst().orElse(null);
//        if (maybeJump != null) {
//            afterJump(player, maybeJump);
//            return maybeJump;
//        }
//        return null;
//    }

    // TODO more jumps check
//    public boolean possibleAnotherJump(Player player, Pieces movingMan, Jump jump) {
//        return rules.possibleAnotherJump(player, movingMan, jump);
//    }

    // AFTER MOVEMENT
//    private void after(Move move) {
//        board.moveOrJump(move);

        // TODO promotions
//        if (rules.needsPromotion(move.getMan(), move.getNew().getRow()))
//            board.promote(move.getMan(), move.getNew());

//        history.add(move);
//    }

    // TODO hinting
//    public String hint(Player currentPlayer, Player nextPlayer, Pieces movingMan, Coordinate coordinate, int depth) {
//        Move move = Minimax.bestMove(rules, board, difficulty, currentPlayer, nextPlayer, movingMan, coordinate, depth);
//        if (move == null) return "";
//        return move.toStringWithoutPlayer();
//    }

    // HISTORY
    public boolean save(String fileName) {
        try {
            history.save(fileName);
            return true;
        } catch (Exception e) {
            System.out.println("Error saving a file: " + e);
            return false;
        }
    }

    public boolean load(Game game, Player[] players, String fileName) {
        try {
            history.load(game, players, fileName);
            return true;
        } catch (Exception e) {
            System.out.println("Error loading a file: " + e);
            return false;
        }
    }

    public boolean canPlayerMoveThis(ImageIcon piece, Player player) {
        return player.isWhite() == (piece == pieces[0] || piece == pieces[1]);
    }

    public boolean canPlayerReplaceThis(ImageIcon original, ImageIcon moving) {
        if (moving == original) return false;
        return true;
    }

    public boolean checkValidityAndMove(ImageIcon originalPiece, ImageIcon movingPiece,
                                 Point originalPosition, Point newPosition, Player player) {
        if (!canPlayerReplaceThis(originalPiece, movingPiece)) return false;
        if (!canPlayerMoveThis(movingPiece, player)) return false;

        Move move = new Move(player, movingPiece, originalPosition, newPosition);
        history.add(move);
        if (move.isJump()) {
            player.addPoint();
            movesWithoutJump = 0;
        }
        else ++movesWithoutJump;
        return true;
    }
}
