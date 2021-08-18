package ui;

import shared.*;

import java.util.List;

public class Game {
    /*
    Core of the game. Interacting with other classes.
     */
    private final Board board;
    private final Rules rules;
    private final History history;
    private int movesWithoutJump;
    private int difficulty;
    private boolean playerBool; // true == player1, false == player2

    public Game() {
        board = new Board(8);
        rules = new Rules(board);
        history = new History();
        movesWithoutJump = 0;
        playerBool = true;
        difficulty = 2; // default being hard

        generateMen();
    }

    public String displayBoard() {
        return board.displayBoard();
    }

    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public boolean getPlayerBool() {
        return playerBool;
    }

    public void switchPlayers() {
        playerBool = !playerBool;
    }

    public Pieces getManByPosition(Coordinate coordinate) {
        return board.getCoordinate(coordinate);
    }

    public boolean shouldEnd(Player player1, Player player2) {
        return movesWithoutJump == 30 || player1.getPoints() == 16 || player2.getPoints() == 16;
    }

    public void generateMen() {
        // black
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < board.getSize(); j++) {
                board.placeMan(Pieces.BLACK, new Coordinate(i, j));
            }
        }

        // white
        for (int i = board.getSize() - 2; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                board.placeMan(Pieces.WHITE, new Coordinate(i, j));
            }
        }
    }

    // MOVING
    public Move move(Player player, Pieces movingMan, List<Coordinate> coordinates) {
        if (rules.isJumpingPossible(player, movingMan, coordinates.get(0))) return null;
        Move move = new Move(player, movingMan, coordinates.get(0), // the man we are currently moving
                coordinates.get(1)); // the position where we want to move

        if (!(rules.isValid(player, movingMan, move))) return null;
        afterMove(move);
        return move;
    }

    public Move moveKing(Player player, Pieces movingMan, List<Coordinate> coordinates) {
        if (rules.isJumpingPossible(player, movingMan, coordinates.get(0))) return null;
        Move maybeMove = rules.getPossibleMoves(player, movingMan, coordinates.get(0)).stream()
                .filter(move -> move.getNew().getRow() == coordinates.get(1).getRow() &&
                        move.getNew().getColumn() == coordinates.get(1).getColumn())
                .findFirst().orElse(null);
        if (maybeMove != null) {
            afterMove(maybeMove);
            return maybeMove;
        }
        return null;
    }

    // JUMPING
    public Jump jump(Player player, Pieces movingMan, List<Coordinate> coordinates) {
        Pieces jumpedMan = getManByPosition(coordinates.get(2));
        if (jumpedMan == null) return null;

        Jump jump = new Jump(player, movingMan, coordinates.get(0), // the man we are currently moving
                coordinates.get(2), jumpedMan,
                coordinates.get(1)); // the position where we want to move

        if (!(rules.isValid(player, movingMan, jump))) return null;
        afterJump(player, jump);
        return jump;
    }

    public Jump jumpKing(Player player, Pieces movingMan, List<Coordinate> coordinates) {
        Jump maybeJump = rules.getPossibleJumps(player, movingMan, coordinates.get(0)).stream()
                .filter(jump -> jump.getNew().getRow() == coordinates.get(1).getRow() &&
                        jump.getNew().getColumn() == coordinates.get(1).getColumn())
                .findFirst().orElse(null);
        if (maybeJump != null) {
            afterJump(player, maybeJump);
            return maybeJump;
        }
        return null;
    }

    public boolean possibleAnotherJump(Player player, Pieces movingMan, Jump jump) {
        return rules.possibleAnotherJump(player, movingMan, jump);
    }

    // AFTER MOVEMENT
    private void after(Move move) {
        board.moveOrJump(move);

        if (rules.needsPromotion(move.getMan(), move.getNew().getRow()))
            board.promote(move.getMan(), move.getNew());

        history.add(move);
    }

    private void afterMove(Move move) {
        after(move);
        ++movesWithoutJump;
    }

    private void afterJump(Player player, Jump jump) {
        after(jump);
        player.addPoint();
        movesWithoutJump = 0;
    }

    public String hint(Player currentPlayer, Player nextPlayer, Pieces movingMan, Coordinate coordinate, int depth) {
        Move move = Minimax.bestMove(rules, board, difficulty, currentPlayer, nextPlayer, movingMan, coordinate, depth);
        if (move == null) return "";
        return move.toStringWithoutPlayer();
    }

    // HISTORY
    public String getHistory() {
        return history.toString();
    }

    public boolean save() {
        try {
            history.save();
            return true;
        } catch (Exception e) {
            System.out.println("Error saving a file: " + e);
            return false;
        }
    }

    public boolean load(Game game, Player player1, Player player2, String fileName) {
        try {
            history.load(game, player1, player2, fileName);
            return true;
        } catch (Exception e) {
            System.out.println("Error loading a file: " + e);
            return false;
        }
    }

    // AI MOVEMENT
    public void computerMove(Player currentPlayer, Player nextPlayer, int depth) {
        Jump bestJump = null;
        Move bestMove = null;

        Jump tempJump;
        Move tempMove;

        for (Coordinate positionCoordinate : board.getCoordinatesList(currentPlayer)) {
            Pieces movingMan = board.getCoordinate(positionCoordinate);
            tempMove = Minimax.bestMove(rules, board, difficulty, currentPlayer, nextPlayer, movingMan, positionCoordinate, depth);
            if (tempMove == null) continue;
            if (tempMove.isJump()) {
                tempJump = Utils.convertMoveIntoJump(tempMove);
                if (bestJump == null || getMoveEvaluation(currentPlayer, nextPlayer, depth, bestJump)
                        < getMoveEvaluation(currentPlayer, nextPlayer, depth, tempJump))
                    bestJump = tempJump;
            } else {
                if (bestMove == null || getMoveEvaluation(currentPlayer, nextPlayer, depth, bestMove)
                        < getMoveEvaluation(currentPlayer, nextPlayer, depth, tempMove))
                    bestMove = tempMove;
            }
        }
        if (bestJump != null) {
            afterJump(currentPlayer, bestJump);
            if (possibleAnotherJump(currentPlayer, bestJump.getMan(), bestJump))
                computerMove(currentPlayer, nextPlayer, depth);
        } else afterMove(bestMove);
    }

    private double getMoveEvaluation(Player currentPlayer, Player nextPlayer, int depth, Move move) {
        return Minimax.getMoveEvaluation(rules, board, difficulty, move,
                nextPlayer, currentPlayer, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
}
