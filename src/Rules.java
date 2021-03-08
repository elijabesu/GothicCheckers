import java.util.*;

public class Rules {
    private Board board;
    private int difficulty;

    public Rules(Board board) {
        this.board = board;
        this.difficulty = 2; // default being hard
    }

    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

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

        int[] rows = Utils.generateArrayOfThree(coordinate.getRow());
        int[] columns = Utils.generateArrayOfThree(coordinate.getColumn());

        for (int row: rows) {
            if (row < 0 || row > 7) continue;
            for (int column: columns) {
                if (column < 0 || column > 7) continue;
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
        if (isValidJump(player, convertMoveIntoJump(maybeJump), board.isOccupied(nexCoordinate)))
            possibleJumps.add(convertMoveIntoJump(maybeJump));
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

        if (player.isWhite()) {
            if (jumpedMan.isWhite()) return false; // if the WHITE player is trying to jump over another WHITE man -> NOPE
        } else {
            if (!jumpedMan.isWhite()) return false; // if the BLACK player is trying to jump over another BLACK man -> NOPE
        }

        return (rowDifference == 0 && columnDifference == 2) ||
                (rowDifference == 2 && (columnDifference == 0 || columnDifference == 2));
    }

    private boolean basicValidation(Player player, Move move, boolean newPositionOccupied) {
        Pieces movingMan = move.getMan();

        if (newPositionOccupied) return false; // if the position is occupied -> NOPE

        if (player.isWhite()) {
            if (!(movingMan.isWhite())) return false; // if the WHITE player is trying to move any of the BLACK men -> NOPE
        } else {
            if (movingMan.isWhite()) return false; // if the BLACK player is trying to move any of the WHITE men -> NOPE
        }

        if (movingMan.isKing()) return true; // if it's a King -> YEP

        // if they are trying to move backwards -> NOPE
        if (movingMan.isWhite() && move.getNew().getRow() > move.getOriginal().getRow()) return false;
        if (!(movingMan.isWhite()) && move.getNew().getRow() < move.getOriginal().getRow()) return false;

        // everything else:
        return true;
    }

    public boolean possibleAnotherJump(Player player, Pieces movingMan, Jump jump) {
        List<Jump> maybeMoreJumps = getPossibleJumps(player, movingMan, jump.getNew());
        return !(maybeMoreJumps == null || maybeMoreJumps.isEmpty());
    }

    private List<List<? extends Move>> generateKingMoves(Player player, Pieces movingMan,
                                                         Coordinate coordinate,
                                                         List<List<? extends Move>> possibilities,
                                                         List<Move> possibleMoves, List<Jump> possibleJumps) {
        List<int[]> skipPositions = new ArrayList<>();

        for (int i = 0; i < board.getSize(); i++) {
            for (int row: new int[] {coordinate.getRow(), coordinate.getRow() - (i + 1), coordinate.getRow() + (i + 1)}) {
                if (row < 0 || row > 7) continue;
                for (int column: new int[] {coordinate.getColumn(), coordinate.getColumn() - (i + 1), coordinate.getColumn() + (i + 1)}) {
                    if (column < 0 || column > 7) continue;
                    if (Utils.listOfArraysContains(skipPositions, row, column)) continue;
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
                                  Coordinate coordinate, List<int[]> skipPositions, List<Jump> possibleJumps) {
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

            skipPositions.add(new int[]{nextRow, nextColumn});
        }
    }

    public boolean needsPromotion(Pieces movingMan, int newRow) {
        return (!movingMan.isWhite() && newRow == 7) || (movingMan.isWhite() && newRow == 0);
    }

    public Jump convertMoveIntoJump(Move move) {
        return new Jump(move.getPlayer(), move.getMan(), move.getOriginal(),
                move.getJumped(), move.getJumpedMan(), move.getNew(), move.getEvaluation());
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

    private double getHeuristicValue(Board board, Player currentPlayer) {
        double kingWeight = 1.5;
        double result;
        double random = new Random().nextDouble();

        if (currentPlayer.isWhite()) result = board.getNumberOfWhiteKings() * kingWeight +
                board.getNumberOfWhiteMen() - board.getNumberOfBlackKings() * kingWeight -
                board.getNumberOfBlackMen();
        else result = board.getNumberOfBlackKings() * kingWeight +
                board.getNumberOfBlackMen() - board.getNumberOfWhiteKings() * kingWeight -
                board.getNumberOfWhiteMen();

        if (difficulty == 1) result = result * 0.75 + random * 0.25;
        if (difficulty == 0) result = result * 0.5 + random * 0.5;
        return result;
    }

    private double minimax(Board board, Player currentPlayer, Player nextPlayer, Pieces movingMan,
                           Coordinate coordinate, int depth, double alpha, double beta) {
        if (depth == 0) return getHeuristicValue(board, currentPlayer);

        List<Move> possibleMoves = getAllPossibilities(currentPlayer, movingMan, coordinate);
        double initialValue;
        Board tempBoard;

        if (currentPlayer.isWhite()) {
            initialValue = Double.POSITIVE_INFINITY;
            for (Move possibleMove : possibleMoves) {
                tempBoard = board.clone();
                tempBoard.moved(possibleMove);
                for (Coordinate enemyCoordinate : tempBoard.getCoordinatesList(nextPlayer)) {
                    double result = minimax(tempBoard, nextPlayer, currentPlayer,
                            tempBoard.getCoordinate(enemyCoordinate),
                            enemyCoordinate, depth - 1, alpha, beta);

                    initialValue = Math.min(result, initialValue);
                    alpha = Math.min(alpha, initialValue);

                    if (alpha >= beta) break;
                }
                if (alpha >= beta) break;
            }
        } else {
            initialValue = Double.NEGATIVE_INFINITY;
            for (Move possibleMove : possibleMoves) {
                tempBoard = board.clone();
                tempBoard.moved(possibleMove);
                for (Coordinate enemyCoordinate : tempBoard.getCoordinatesList(nextPlayer)) {
                    double result = minimax(tempBoard, nextPlayer, currentPlayer,
                            tempBoard.getCoordinate(enemyCoordinate),
                            enemyCoordinate, depth - 1, alpha, beta);

                    initialValue = Math.max(result, initialValue);
                    alpha = Math.max(alpha, initialValue);

                    if (alpha >= beta) break;
                }
                if (alpha >= beta) break;
            }
        }
        return initialValue;
    }

    public Move bestMove(Player currentPlayer, Player nextPlayer, Pieces movingMan,
                         Coordinate coordinate, int depth) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        List<Move> possibleMoves = getAllPossibilities(currentPlayer, movingMan, coordinate);
        if (possibleMoves.isEmpty()) return null;

        List<Double> heuristics = new ArrayList<>();
        List<Double> tempHeuristics = new ArrayList<>();
        Board tempBoard;

        for (Move possibleMove : possibleMoves) {
            tempBoard = board.clone();
            tempBoard.moved(possibleMove);
            for (Coordinate enemyCoordinate : tempBoard.getCoordinatesList(nextPlayer)) {
                possibleMove.setEvaluation(minimax(tempBoard, nextPlayer, currentPlayer,
                        tempBoard.getCoordinate(enemyCoordinate),
                        enemyCoordinate, depth - 1, alpha, beta));
                tempHeuristics.add(possibleMove.getEvaluation());
            }
            heuristics.add(Collections.max(tempHeuristics));
            tempHeuristics.clear();
        }

        double maxHeuristics = Double.NEGATIVE_INFINITY;

        Random rand = new Random();
        for(int i = heuristics.size() - 1; i >= 0; i--) {
            if (heuristics.get(i) >= maxHeuristics) {
                maxHeuristics = heuristics.get(i);
            }
        }
        for(int i = 0; i < heuristics.size(); i++)
        {
            if(heuristics.get(i) < maxHeuristics)
            {
                heuristics.remove(i);
                possibleMoves.remove(i);
                i--;
            }
        }
        return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
}
