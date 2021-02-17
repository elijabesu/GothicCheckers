import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Rules {
    Board board;

    public Rules(Board board) {
        this.board = board;
    }

    public boolean isValid(Player player, Pieces movingMan, Move move) {
        List<List<? extends Move>> movesAndJumps = generateMoves(player, movingMan,
                move.getOriginalRow(), move.getOriginalColumn());
        List<Move> possibilities = addIntoPossibilities((List<Move>) movesAndJumps.get(0),
                (List<Jump>) movesAndJumps.get(1));
        if (possibilities.isEmpty()) return false;
        return possibilities.contains(move);
    }

    public List<List<? extends Move>> generateMoves(Player player, Pieces movingMan,
                                                    int originalRow, int originalColumn) {
        if (player.isWhite() != movingMan.isWhite()) return null;

        List<Move> possibleMoves = new ArrayList<>();
        List<Jump> possibleJumps = new ArrayList<>();
        List<List<? extends Move>> possibilities = new ArrayList<>();

        if (movingMan.isKing()) {
            return generateKingMoves(player, movingMan, originalRow, originalColumn,
                    possibilities, possibleMoves, possibleJumps);
        }

        int[] rows = Utils.generateArrayOfThree(originalRow);
        int[] columns = Utils.generateArrayOfThree(originalColumn);

        for (int row: rows) {
            if (row < 0 || row > 7) continue;
            for (int column: columns) {
                if (column < 0 || column > 7) continue;
                if (board.isOccupied(row, column)) generateJump(player, movingMan,
                        originalRow, originalColumn, row, column, possibleJumps);
                else {
                    Move maybeMove = new Move(player, movingMan, originalRow, originalColumn, row, column);
                    if (isValidMove(player, maybeMove)) possibleMoves.add(maybeMove);
                }
            }
        }

        possibilities.add(possibleMoves);
        possibilities.add(possibleJumps);

        return possibilities;
    }

    private void generateJump(Player player, Pieces movingMan, int originalRow, int originalColumn,
                              int row, int column, List<Jump> possibleJumps) {
        if (movingMan.isSameColourAs(board.getCoordinate(row, column))) return;

        int nextRow = row - (originalRow - row);
        int nextColumn = column - (originalColumn - column);

        if (nextRow < 0 || nextRow > 7 || nextColumn < 0 || nextColumn > 7) return;

        if (board.isOccupied(nextRow, nextColumn)) return;
        Move maybeJump = new Move(player, movingMan, originalRow, originalColumn,
                row, column, board.getCoordinate(row, column), // jumpedRow, jumpedColumn, jumpedValue
                nextRow, nextColumn);
        if (isValidJump(player, convertMoveIntoJump(maybeJump), board.isOccupied(nextRow, nextColumn))) possibleJumps.add(convertMoveIntoJump(maybeJump));
    }

    private boolean isValidMove(Player player, Move move) {
        if (!(basicValidation(player, move, false))) return false;

        int rowDifference = Math.abs(move.getOriginalRow() - move.getNewRow());
        int columnDifference = Math.abs(move.getOriginalColumn() - move.getNewColumn());

        return (rowDifference == 0 && columnDifference == 1) ||
                (rowDifference == 1 && (columnDifference == 0 || columnDifference == 1));
    }

    private boolean isValidJump(Player player, Jump jump, boolean newPositionOccupied) {
        if (!(basicValidation(player, jump, newPositionOccupied))) return false;

        Pieces jumpedMan = jump.getJumpedMan();

        if (jumpedMan == Pieces.EMPTY) return false;

        int rowDifference = Math.abs(jump.getOriginalRow() - jump.getNewRow());
        int columnDifference = Math.abs(jump.getOriginalColumn() - jump.getNewColumn());

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
        if (movingMan.isWhite() && move.getNewRow() > move.getOriginalRow()) return false;
        if (!(movingMan.isWhite()) && move.getNewRow() < move.getOriginalRow()) return false;

        // everything else:
        return true;
    }

    public boolean possibleAnotherJump(Player player, Pieces movingMan, Jump jump) {
        List<Jump> maybeMoreJumps = getPossibleJumps(player, movingMan, jump.getNewRow(), jump.getNewColumn());
        return !(maybeMoreJumps == null || maybeMoreJumps.isEmpty());
    }

    private List<List<? extends Move>> generateKingMoves(Player player, Pieces movingMan,
                                                         int originalRow, int originalColumn,
                                                         List<List<? extends Move>> possibilities,
                                                         List<Move> possibleMoves, List<Jump> possibleJumps) {
        List<int[]> skipPositions = new ArrayList<>();

        for (int i = 0; i < board.getSize(); i++) {
            for (int row: new int[] {originalRow, originalRow - (i + 1), originalRow + (i + 1)}) {
                if (row < 0 || row > 7) continue;
                for (int column: new int[] {originalColumn, originalColumn - (i + 1), originalColumn + (i + 1)}) {
                    if (column < 0 || column > 7) continue;
                    if (Utils.listOfArraysContains(skipPositions, row, column)) continue;
                    if (board.isOccupied(row, column))generateKingJump(player, movingMan, originalRow, originalColumn,
                            row, column, skipPositions, possibleJumps);
                    else possibleMoves.add(new Move(player, movingMan, originalRow, originalColumn, row, column));
                }
            }
        }
        possibilities.add(possibleMoves);
        possibilities.add(possibleJumps);

        return possibilities;
    }

    private void generateKingJump(Player player, Pieces movingMan, int originalRow, int originalColumn,
                                  int row, int column, List<int[]> skipPositions, List<Jump> possibleJumps) {
        if (movingMan.isSameColourAs(board.getCoordinate(row, column))) return;// if they are the same colour

        for (int i = 1; i < board.getSize(); i++) {
            int nextRow = row;
            if (originalRow - nextRow < 0) nextRow = row + i;
            else if (originalRow - nextRow > 0) nextRow = row - i;

            int nextColumn = column;
            if (originalColumn - nextColumn < 0) nextColumn = column + i;
            else if (originalColumn - nextColumn > 0) nextColumn = column - i;

            if (nextRow < 0 || nextRow > 7 || nextColumn < 0 || nextColumn > 7) return;

            if (board.isOccupied(nextRow, nextColumn)) {
                Utils.ignorePositions(row, column, nextRow, nextColumn, skipPositions);
                return;
            }

            possibleJumps.add(new Jump(player, movingMan, originalRow, originalColumn,
                    row, column, board.getCoordinate(row, column), // jumpedRow, jumpedColumn, jumpedValue
                    nextRow, nextColumn));

            skipPositions.add(new int[]{nextRow, nextColumn});
        }
    }

    public boolean needsPromotion(Pieces movingMan, int newRow) {
        return (!movingMan.isWhite() && newRow == 7) || (movingMan.isWhite() && newRow == 0);
    }

    public Jump convertMoveIntoJump(Move move) {
        return new Jump(move.getPlayer(), move.getMan(),
                move.getOriginalRow(), move.getOriginalColumn(),
                move.getJumpedRow(), move.getJumpedColumn(), move.getJumpedMan(),
                move.getNewRow(), move.getNewColumn(),
                move.getEvaluation());
    }

    private List<Move> addIntoPossibilities(List<Move> possibleMoves, List<Jump> possibleJumps) {
        List<Move> possibilities = new ArrayList<>();

        possibilities.addAll(possibleJumps);
        possibilities.addAll(possibleMoves);

        return possibilities;
    }

    public List<Move> getPossibleMoves(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        List<List<? extends Move>> possibilities = generateMoves(player, movingMan, originalRow, originalColumn);
        if (possibilities == null || possibilities.isEmpty()) return null;
        return (List<Move>) possibilities.get(0);
    }

    public List<Jump> getPossibleJumps(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        List<List<? extends Move>> possibilities = generateMoves(player, movingMan, originalRow, originalColumn);
        if (possibilities == null || possibilities.isEmpty()) return null;
        return (List<Jump>) possibilities.get(1);
    }

    public List<Move> getAllPossibilities(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        return addIntoPossibilities(getPossibleMoves(player, movingMan, originalRow, originalColumn),
                getPossibleJumps(player, movingMan, originalRow, originalColumn));
    }

    public boolean isJumpingPossible(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        List <Jump> possibleJumps = getPossibleJumps(player, movingMan, originalRow, originalColumn);
        if (possibleJumps == null) return false;
        return possibleJumps.size() != 0;
    }

    private double getHeuristicValue(Board board, Player currentPlayer) {
        double kingWeight = 1.5;
        double result;

        if (currentPlayer.isWhite()) result = board.getNumberOfWhiteKings() * kingWeight +
                board.getNumberOfWhiteMen() - board.getNumberOfBlackKings() * kingWeight -
                board.getNumberOfBlackMen();
        else result = board.getNumberOfBlackKings() * kingWeight +
                board.getNumberOfBlackMen() - board.getNumberOfWhiteKings() * kingWeight -
                board.getNumberOfWhiteMen();

        return result;
    }

    private double minimax(Board board, Player currentPlayer, Player nextPlayer, Pieces movingMan,
                           int originalRow, int originalColumn, int depth, double alpha, double beta) {
        if (depth == 0) return getHeuristicValue(board, currentPlayer);

        List<Move> possibleMoves = getAllPossibilities(currentPlayer, movingMan, originalRow, originalColumn);
        double initialValue;
        Board tempBoard;

        if (currentPlayer.isWhite()) {
            initialValue = Double.POSITIVE_INFINITY;
            for (Move possibleMove : possibleMoves) {
                tempBoard = board.clone();
                tempBoard.moved(possibleMove);
                for (int[] positionOfEnemy : tempBoard.getCoordinatesList(nextPlayer)) {
                    double result = minimax(tempBoard, nextPlayer, currentPlayer,
                            tempBoard.getCoordinate(positionOfEnemy[0], positionOfEnemy[1]),
                            positionOfEnemy[0], positionOfEnemy[1], depth - 1, alpha, beta);

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
                for (int[] positionOfEnemy : tempBoard.getCoordinatesList(nextPlayer)) {
                    double result = minimax(tempBoard, nextPlayer, currentPlayer,
                            tempBoard.getCoordinate(positionOfEnemy[0], positionOfEnemy[1]),
                            positionOfEnemy[0], positionOfEnemy[1], depth - 1, alpha, beta);

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
                         int originalRow, int originalColumn, int depth) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        List<Move> possibleMoves = getAllPossibilities(currentPlayer, movingMan, originalRow, originalColumn);
        if (possibleMoves.isEmpty()) return null;

        List<Double> heuristics = new ArrayList<>();
        List<Double> tempHeuristics = new ArrayList<>();
        Board tempBoard;

        for (Move possibleMove : possibleMoves) {
            tempBoard = board.clone();
            tempBoard.moved(possibleMove);
            for (int[] positionOfEnemy : tempBoard.getCoordinatesList(nextPlayer)) {
                possibleMove.setEvaluation(minimax(tempBoard, nextPlayer, currentPlayer,
                        tempBoard.getCoordinate(positionOfEnemy[0], positionOfEnemy[1]),
                        positionOfEnemy[0], positionOfEnemy[1], depth - 1, alpha, beta));
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
