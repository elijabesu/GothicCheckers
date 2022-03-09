package gui.brain;

import gui.BoardPanel;
import shared.Coordinate;
import shared.Player;
import shared.Utils;

import java.util.ArrayList;
import java.util.List;

public class Rules {
    /*
    Generating and validating all possible moves.
     */
    private final BoardPanel boardPanel;

    public Rules (BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    // VALIDATION
    public boolean canPlayerMoveThis(Player player, Piece piece) {
        return player.isWhite() == piece.isWhite();
    }

    private boolean basicValidation(Player player, Move move, boolean newPositionOccupied) {
        Piece movingMan = move.getMan();

        if (newPositionOccupied) return false; // if the position is occupied -> NOPE

        if (player.isWhite() != movingMan.isWhite()) return false;

        if (movingMan.isKing()) return true; // if it's a King -> YEP
        if (move.getNew().getRow() == move.getOriginal().getRow()) return true; // same row

        return (movingMan.isWhite() && move.getNew().getRow() < move.getOriginal().getRow()) ||
                (!movingMan.isWhite() && move.getNew().getRow() > move.getOriginal().getRow());
    }

    private boolean isValidMove(Player player, Move move) {
        if (!(basicValidation(player, move, false))) return false;

        int rowDifference = Math.abs(move.getOriginal().getRow() - move.getNew().getRow());
        int columnDifference = Math.abs(move.getOriginal().getColumn() - move.getNew().getColumn());

        return (rowDifference == 0 && columnDifference == 1) ||
                (rowDifference == 1 && (columnDifference == 0 || columnDifference == 1));
    }

    private boolean isValidJump(Player player, Jump jump, boolean newPositionOccupied) {
        if (!(basicValidation(player, jump, newPositionOccupied))) return false;

        Piece jumpedMan = jump.getJumpedMan();

        if (jumpedMan == null) return false;

        int rowDifference = Math.abs(jump.getOriginal().getRow() - jump.getNew().getRow());
        int columnDifference = Math.abs(jump.getOriginal().getColumn() - jump.getNew().getColumn());

        if (player.isWhite() == jumpedMan.isWhite()) return false;

        return (rowDifference == 0 && columnDifference == 2) ||
                (rowDifference == 2 && (columnDifference == 0 || columnDifference == 2));
    }

    public boolean isValid(Player player, Piece movingMan, Move move) {
        List<Move> possibilities = getAllPossibilities(player, movingMan, move.getOriginal());
        if (possibilities.isEmpty()) {
            System.out.println("No possible moves."); // TODO delete
            return false;
        }

        if (move.isJump()) {
            Jump jump = Utils.convertMoveIntoJump(move);
            return possibilities.contains(jump);
        }
        return possibilities.contains(move);
    }

    public boolean needsPromotion(Piece movingMan, Coordinate coordinate) {
        return (!movingMan.isWhite() && coordinate.getRow() == 7) || (movingMan.isWhite() && coordinate.getRow() == 0);
    }

    // MOVING
    private List<List<? extends Move>> generateMoves(Player player, Piece movingMan, Coordinate coordinate) {
        if (player.isWhite() != movingMan.isWhite()) return null;

        List<Move> possibleMoves = new ArrayList<>();
        List<Jump> possibleJumps = new ArrayList<>();
        List<List<? extends Move>> possibilities = new ArrayList<>();

        if (movingMan.isKing()) {
            return generateKingMoves(player, movingMan, coordinate, possibilities, possibleMoves, possibleJumps);
        }

        List<Integer> rows = Utils.generateListOfAvailable(coordinate.getRow(), 0);
        List<Integer> columns = Utils.generateListOfAvailable(coordinate.getColumn(), 0);

        for (int row: rows) {
            for (int column: columns) {
                Coordinate newCoordinate = new Coordinate(row, column);
                if (boardPanel.isOccupied(newCoordinate)) generateJumps(player, movingMan,
                        coordinate, newCoordinate, possibleJumps);
                else {
                    Move maybeMove = new Move(player, movingMan, coordinate, newCoordinate);
                    if (isValidMove(player, maybeMove)) possibleMoves.add(maybeMove);
                }
            }
        }

        possibilities.add(possibleMoves);
        possibilities.add(possibleJumps);

        return possibilities;
    }

    private List<List<? extends Move>> generateKingMoves(Player player, Piece movingMan, Coordinate coordinate,
                                                         List<List<? extends Move>> possibilities,
                                                         List<Move> possibleMoves, List<Jump> possibleJumps) {
        List<Coordinate> skipPositions = new ArrayList<>();

        for (int i = 0; i < boardPanel.getBoardSize(); i++) {
            for (int row: Utils.generateListOfAvailable(coordinate.getRow(), i)) {
                for (int column: Utils.generateListOfAvailable(coordinate.getColumn(), i)) {
                    if (Utils.listOfArraysContains(skipPositions, new Coordinate(row, column))) continue;
                    Coordinate newCoordinate = new Coordinate(row, column);
                    if (boardPanel.isOccupied(newCoordinate))
                        generateKingJumps(player, movingMan, coordinate, newCoordinate, skipPositions, possibleJumps);
                    else {
                        Move move = new Move(player, movingMan, coordinate, newCoordinate);
                        possibleMoves.add(move);
                        System.out.println("Possible King move: " + move); // TODO delete
                    }
                }
            }
        }

        possibilities.add(possibleMoves);
        possibilities.add(possibleJumps);

        return possibilities;
    }

    // JUMPING
    public boolean isJumpingPossible(Player player, Piece movingMan, Coordinate coordinate) {
        List<Jump> possibleJumps = getPossibleJumps(player, movingMan, coordinate);
        if (possibleJumps == null) return false;
        return possibleJumps.size() != 0;
    }

    private void generateJumps(Player player, Piece movingMan, Coordinate originalCoordinate,
                              Coordinate coordinate, List<Jump> possibleJumps) {
        if (movingMan.isWhite() == boardPanel.getCoordinate(coordinate).isWhite()) return;

        int nextRow = coordinate.getRow() - (originalCoordinate.getRow() - coordinate.getRow());
        int nextColumn = coordinate.getColumn() - (originalCoordinate.getColumn() - coordinate.getColumn());
        Coordinate nextCoordinate = new Coordinate(nextRow, nextColumn);

        if (nextRow < 0 || nextRow > 7 || nextColumn < 0 || nextColumn > 7) return;

        if (boardPanel.isOccupied(nextCoordinate)) return;
        Move maybeJump = new Move(player, movingMan, originalCoordinate,
                coordinate, boardPanel.getCoordinate(coordinate),
                nextCoordinate);
        if (isValidJump(player, Utils.convertMoveIntoJump(maybeJump), boardPanel.isOccupied(nextCoordinate)))
            possibleJumps.add(Utils.convertMoveIntoJump(maybeJump));
    }

    private void generateKingJumps(Player player, Piece movingMan, Coordinate originalCoordinate,
                                   Coordinate coordinate, List<Coordinate> skipPositions, List<Jump> possibleJumps) {
        if (movingMan.isWhite() == boardPanel.getCoordinate(coordinate).isWhite()) return;

        for (int i = 1; i < boardPanel.getBoardSize(); i++) {
            int nextRow = coordinate.getRow();
            if (originalCoordinate.getRow() - nextRow < 0) nextRow += i;
            else if (originalCoordinate.getRow() - nextRow > 0) nextRow -= i;

            int nextColumn = coordinate.getColumn();
            if (originalCoordinate.getColumn() - nextColumn < 0) nextColumn += i;
            else if (originalCoordinate.getColumn() - nextColumn > 0) nextColumn -= i;

            if (nextRow < 0 || nextRow > 7 || nextColumn < 0 || nextColumn > 7) return;

            Coordinate nextCoordinate = new Coordinate(nextRow, nextColumn);

            if (boardPanel.isOccupied(nextCoordinate)) {
                Utils.ignorePositions(coordinate, nextCoordinate, skipPositions);
                return;
            }

            Jump jump = new Jump(player, movingMan, boardPanel.getCoordinate(coordinate),
                    originalCoordinate, coordinate, nextCoordinate);
            possibleJumps.add(jump);
            System.out.println("Possible King jump: " + jump); // TODO delete

            skipPositions.add(nextCoordinate);
        }
    }

    private boolean anotherJumpPossible(Player player, Piece movingMan, Jump jump) {
        List<Jump> maybeMoreJumps = getPossibleJumps(player, movingMan, jump.getNew());
        return !(maybeMoreJumps == null || maybeMoreJumps.isEmpty());
    }

    // GETTING POSSIBLE MOVES
    private List<Move> getAllPossibilities(Player player, Piece movingMan, Coordinate coordinate) {
        return addIntoPossibilities(getPossibleMoves(player, movingMan, coordinate),
                getPossibleJumps(player, movingMan, coordinate));
    }

    @SuppressWarnings("unchecked")
    private List<Move> getPossibleMoves(Player player, Piece movingMan, Coordinate coordinate) {
        List<List<? extends Move>> possibilities = generateMoves(player, movingMan, coordinate);
        if (possibilities == null || possibilities.isEmpty()) return null;
        return (List<Move>) possibilities.get(0);
    }

    @SuppressWarnings("unchecked")
    private List<Jump> getPossibleJumps(Player player, Piece movingMan, Coordinate coordinate) {
        List<List<? extends Move>> possibilities = generateMoves(player, movingMan, coordinate);
        if (possibilities == null || possibilities.isEmpty()) return null;
        return (List<Jump>) possibilities.get(1);
    }

    private List<Move> addIntoPossibilities(List<Move> possibleMoves, List<Jump> possibleJumps) {
        List<Move> possibilities = new ArrayList<>();

        if (possibleJumps != null) possibilities.addAll(possibleJumps);
        if (possibleMoves != null) possibilities.addAll(possibleMoves);

        return possibilities;
    }
}