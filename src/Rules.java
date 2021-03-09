import java.util.*;

public class Rules {
    /*
    Generating and validating all possible moves.
     */
    private final Board board;

    public Rules(Board board) {
        this.board = board;
    }

    public boolean isValid(Player player, Pieces movingMan, Move move) {
        List<List<? extends Move>> movesAndJumps = generateMoves(player, movingMan, move.getOriginal());
        List<Move> possibilities = addIntoPossibilities((List<Move>) movesAndJumps.get(0),
                (List<Jump>) movesAndJumps.get(1));
        if (possibilities.isEmpty()) return false;
        return possibilities.contains(move);
    }

    public List<List<? extends Move>> generateMoves(Player player, Pieces movingMan, Coordinate coordinate) {
        if (player.isWhite() != movingMan.isWhite()) return null;

        List<Move> possibleMoves = new ArrayList<>();
        List<Jump> possibleJumps = new ArrayList<>();
        List<List<? extends Move>> possibilities = new ArrayList<>();

        if (movingMan.isKing()) {
            return generateKingMoves(player, movingMan, coordinate,
                    possibilities, possibleMoves, possibleJumps);
        }

        List<Integer> rows = Utils.generateListOfAvailable(coordinate.getRow(), 0);
        List<Integer> columns = Utils.generateListOfAvailable(coordinate.getColumn(), 0);

        for (int row: rows) {
            for (int column: columns) {
                Coordinate newCoordinate = new Coordinate(row, column);
                if (board.isOccupied(newCoordinate)) generateJump(player, movingMan,
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

    private void generateJump(Player player, Pieces movingMan, Coordinate originalCoordinate,
                              Coordinate coordinate, List<Jump> possibleJumps) {
        if (movingMan.isSameColourAs(board.getCoordinate(coordinate))) return;

        int nextRow = coordinate.getRow() - (originalCoordinate.getRow() - coordinate.getRow());
        int nextColumn = coordinate.getColumn() - (originalCoordinate.getColumn() - coordinate.getColumn());
        Coordinate nexCoordinate = new Coordinate(nextRow, nextColumn);

        if (nextRow < 0 || nextRow > 7 || nextColumn < 0 || nextColumn > 7) return;

        if (board.isOccupied(nexCoordinate)) return;
        Move maybeJump = new Move(player, movingMan, originalCoordinate,
                coordinate, board.getCoordinate(coordinate),
                nexCoordinate);
        if (isValidJump(player, Utils.convertMoveIntoJump(maybeJump), board.isOccupied(nexCoordinate)))
            possibleJumps.add(Utils.convertMoveIntoJump(maybeJump));
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

        Pieces jumpedMan = jump.getJumpedMan();

        if (jumpedMan == Pieces.EMPTY) return false;

        int rowDifference = Math.abs(jump.getOriginal().getRow() - jump.getNew().getRow());
        int columnDifference = Math.abs(jump.getOriginal().getColumn() - jump.getNew().getColumn());

        if (player.isWhite() == jumpedMan.isWhite()) return false;

        return (rowDifference == 0 && columnDifference == 2) ||
                (rowDifference == 2 && (columnDifference == 0 || columnDifference == 2));
    }

    private boolean basicValidation(Player player, Move move, boolean newPositionOccupied) {
        Pieces movingMan = move.getMan();

        if (newPositionOccupied) return false; // if the position is occupied -> NOPE

        if (player.isWhite() != movingMan.isWhite()) return false;

        if (movingMan.isKing()) return true; // if it's a King -> YEP
        if (move.getNew().getRow() == move.getOriginal().getRow()) return true; // same row

        return (movingMan.isWhite() && move.getNew().getRow() < move.getOriginal().getRow()) ||
                (!movingMan.isWhite() && move.getNew().getRow() > move.getOriginal().getRow());
    }

    public boolean possibleAnotherJump(Player player, Pieces movingMan, Jump jump) {
        List<Jump> maybeMoreJumps = getPossibleJumps(player, movingMan, jump.getNew());
        return !(maybeMoreJumps == null || maybeMoreJumps.isEmpty());
    }

    private List<List<? extends Move>> generateKingMoves(Player player, Pieces movingMan,
                                                         Coordinate coordinate,
                                                         List<List<? extends Move>> possibilities,
                                                         List<Move> possibleMoves, List<Jump> possibleJumps) {
        List<Coordinate> skipPositions = new ArrayList<>();

        for (int i = 0; i < board.getSize(); i++) {
            for (int row: Utils.generateListOfAvailable(coordinate.getRow(), i)) {
                for (int column: Utils.generateListOfAvailable(coordinate.getColumn(), i)) {
                    if (Utils.listOfArraysContains(skipPositions, new Coordinate(row, column))) continue;
                    Coordinate newCoordinate = new Coordinate(row, column);
                    if (board.isOccupied(newCoordinate))
                        generateKingJump(player, movingMan, coordinate, newCoordinate, skipPositions, possibleJumps);
                    else possibleMoves.add(new Move(player, movingMan, coordinate, newCoordinate));
                }
            }
        }
        possibilities.add(possibleMoves);
        possibilities.add(possibleJumps);

        return possibilities;
    }

    private void generateKingJump(Player player, Pieces movingMan, Coordinate originalCoordinate,
                                  Coordinate coordinate, List<Coordinate> skipPositions, List<Jump> possibleJumps) {
        if (movingMan.isSameColourAs(board.getCoordinate(coordinate))) return;// if they are the same colour

        for (int i = 1; i < board.getSize(); i++) {
            int nextRow = coordinate.getRow();
            if (originalCoordinate.getRow() - nextRow < 0) nextRow += i;
            else if (originalCoordinate.getRow() - nextRow > 0) nextRow = nextRow - i;

            int nextColumn = coordinate.getColumn();
            if (originalCoordinate.getColumn() - nextColumn < 0) nextColumn += i;
            else if (originalCoordinate.getColumn() - nextColumn > 0) nextColumn -= i;

            if (nextRow < 0 || nextRow > 7 || nextColumn < 0 || nextColumn > 7) return;

            Coordinate nextCoordinate = new Coordinate(nextRow, nextColumn);

            if (board.isOccupied(nextCoordinate)) {
                Utils.ignorePositions(coordinate, nextCoordinate, skipPositions);
                return;
            }

            possibleJumps.add(new Jump(player, movingMan, originalCoordinate,
                    coordinate, board.getCoordinate(coordinate),
                    nextCoordinate));

            skipPositions.add(new Coordinate(nextRow, nextColumn));
        }
    }

    public boolean needsPromotion(Pieces movingMan, int newRow) {
        return (!movingMan.isWhite() && newRow == 7) || (movingMan.isWhite() && newRow == 0);
    }

    private List<Move> addIntoPossibilities(List<Move> possibleMoves, List<Jump> possibleJumps) {
        List<Move> possibilities = new ArrayList<>();

        if (possibleJumps != null) possibilities.addAll(possibleJumps);
        if (possibleMoves != null) possibilities.addAll(possibleMoves);

        return possibilities;
    }

    public List<Move> getPossibleMoves(Player player, Pieces movingMan, Coordinate coordinate) {
        List<List<? extends Move>> possibilities = generateMoves(player, movingMan, coordinate);
        if (possibilities == null || possibilities.isEmpty()) return null;
        return (List<Move>) possibilities.get(0);
    }

    public List<Jump> getPossibleJumps(Player player, Pieces movingMan, Coordinate coordinate) {
        List<List<? extends Move>> possibilities = generateMoves(player, movingMan, coordinate);
        if (possibilities == null || possibilities.isEmpty()) return null;
        return (List<Jump>) possibilities.get(1);
    }

    public List<Move> getAllPossibilities(Player player, Pieces movingMan, Coordinate coordinate) {
        return addIntoPossibilities(getPossibleMoves(player, movingMan, coordinate),
                getPossibleJumps(player, movingMan, coordinate));
    }

    public boolean isJumpingPossible(Player player, Pieces movingMan, Coordinate coordinate) {
        List <Jump> possibleJumps = getPossibleJumps(player, movingMan, coordinate);
        if (possibleJumps == null) return false;
        return possibleJumps.size() != 0;
    }
}
