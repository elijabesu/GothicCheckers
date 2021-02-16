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
        List<List<? extends Move>> movesAndJumps = generateMoves(player, movingMan, move.getOriginalRow(), move.getOriginalColumn());
        List<Move> possibilities = addIntoPossibilities((List<Move>) movesAndJumps.get(0), (List<Jump>) movesAndJumps.get(1));
        if (possibilities.isEmpty()) return false;
        return possibilities.contains(move);
    }

    public List<List<? extends Move>> generateMoves(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        if (player.isWhite() != movingMan.isWhite()) return null;

        List<Move> possibleMoves = new ArrayList<>();
        List<Jump> possibleJumps = new ArrayList<>();
        List<List<? extends Move>> possibilities = new ArrayList<>();

        if (movingMan.isKing()) {
            return generateKingMoves(player, movingMan, originalRow, originalColumn, possibilities, possibleMoves, possibleJumps);
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
        Jump maybeJump = new Jump(player, movingMan, originalRow, originalColumn,
                row, column, board.getCoordinate(row, column), // jumpedRow, jumpedColumn, jumpedValue
                nextRow, nextColumn);
        if (isValidJump(player, maybeJump, board.isOccupied(nextRow, nextColumn))) possibleJumps.add(maybeJump);
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

        /*return !(movingMan.isWhite() && move.getNewRow() > move.getOriginalRow()) ||
                !(!(movingMan.isWhite()) && move.getNewRow() < move.getOriginalRow());*/
    }

    public boolean possibleAnotherJump(Player player, Pieces movingMan, Jump jump) {
        List<Jump> maybeMoreJumps = getPossibleJumps(player, movingMan, jump.getNewRow(), jump.getNewColumn());
        return !(maybeMoreJumps == null || maybeMoreJumps.isEmpty());
    }

    private List<List<? extends Move>> generateKingMoves(Player player, Pieces movingMan, int originalRow, int originalColumn,
                                         List<List<? extends Move>> possibilities, List<Move> possibleMoves, List<Jump> possibleJumps) {
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

    public boolean endRow(Pieces movingMan, int newRow) {
        return (!movingMan.isWhite() && newRow == 0) || (movingMan.isWhite() && newRow == 7);
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

    public boolean isJumpingPossible(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        List <Jump> possibleJumps = getPossibleJumps(player, movingMan, originalRow, originalColumn);
        if (possibleJumps == null) return false;
        return possibleJumps.size() != 0;
    }

//    private int minimax(Player currentPlayer, Player nextPlayer, Pieces movingMan, int originalRow, int originalColumn, int depth) {
//        if (depth == 0 || endRow(movingMan, originalRow)) return board.getBoardValue();
//        int evaluation = 0;
//        if (isJumpingPossible(currentPlayer, movingMan, originalRow, originalColumn)) {
//            List<Jump> possibilities = getPossibleJumps(currentPlayer, movingMan, originalRow, originalColumn);
//            if (possibilities == null) return evaluation;
//            for (Jump possibleJump : possibilities) {
//                //System.out.println("minimax: " + possibleJump.toString()); // TODO delete
//                int newRow = possibleJump.getNewRow();
//                int newColumn = possibleJump.getNewColumn();
//                evaluation = Integer.max(evaluation,
//                        -minimax(currentPlayer, nextPlayer, board.getCoordinate(newRow, newColumn), newRow, newColumn, depth-1));
//            }
//        } else {
//            List<Move> possibilities = getPossibleMoves(currentPlayer, movingMan, originalRow, originalColumn);
//            if (possibilities == null) return evaluation;
//            for (Move possibleMove : possibilities) {
//                //System.out.println("minimax: " + possibleMove.toString()); // TODO delete
//                List<int[]> positionsOfTheNextPlayer = board.getCoordinatesList(nextPlayer);
//                for (int[] positionCoordinate : positionsOfTheNextPlayer) {
//                    evaluation = Integer.max(evaluation,
//                            -minimax(nextPlayer, currentPlayer,
//                                    board.getCoordinate(positionCoordinate[0], positionCoordinate[1]),
//                                    positionCoordinate[0], positionCoordinate[1], depth-1));
//                }
//            }
//        }
//        return evaluation;
//    }
//
//    public Move bestMove(Player currentPlayer, Player nextPlayer, Pieces movingMan,
//                         int originalRow, int originalColumn, int depth, int bestEvaluation) {
//        Move apparentlyBestMove = null;
//        if (isJumpingPossible(currentPlayer, movingMan, originalRow, originalColumn)) {
//            apparentlyBestMove = bestJump(currentPlayer, nextPlayer, movingMan, originalRow, originalColumn, depth);
//        } else {
//            List<Move> possibilities = getPossibleMoves(currentPlayer, movingMan, originalRow, originalColumn);
//            if (possibilities == null) return null;
//            for (Move possibleMove : possibilities) {
//                //System.out.println("bestMove: " + possibleMove.toString()); // TODO delete
//                List<int[]> positionsOfTheNextPlayer = board.getCoordinatesList(nextPlayer);
//                for (int[] positionCoordinate : positionsOfTheNextPlayer) {
//                    int evaluation = -minimax(nextPlayer, currentPlayer,
//                                    board.getCoordinate(positionCoordinate[0], positionCoordinate[1]),
//                                    positionCoordinate[0], positionCoordinate[1], depth-1);
//                    if (currentPlayer.isWhite()) {
//                        if (apparentlyBestMove == null || evaluation < bestEvaluation) {
//                            bestEvaluation = evaluation;
//                            apparentlyBestMove = possibleMove;
//                            apparentlyBestMove.setEvaluation(bestEvaluation);
//                        }
//                    } else {
//                        if (apparentlyBestMove == null || evaluation > bestEvaluation) {
//                            bestEvaluation = evaluation;
//                            apparentlyBestMove = possibleMove;
//                            apparentlyBestMove.setEvaluation(bestEvaluation);
//                        }
//                    }
//                }
//            }
//        }
//        if (apparentlyBestMove != null) System.out.println("bestMove: " + apparentlyBestMove.toString() +
//                "\nevaluation: " + apparentlyBestMove.getEvaluation()); // TODO delete
//        return apparentlyBestMove;
//    }
//
//    public Jump bestJump(Player currentPlayer, Player nextPlayer, Pieces movingMan,
//                         int originalRow, int originalColumn, int depth, int bestEvaluation) {
//        Jump apparentlyBestJump = null;
//        List<Jump> possibilities = getPossibleJumps(currentPlayer, movingMan, originalRow, originalColumn);
//        if (possibilities == null) return null;
//        for (Jump possibleJump : possibilities) {
//            //System.out.println("bestJump: " + possibleJump.toString()); // TODO delete
//            int newRow = possibleJump.getNewRow();
//            int newColumn = possibleJump.getNewColumn();
//            int evaluation = -minimax(currentPlayer, nextPlayer, board.getCoordinate(newRow, newColumn), newRow, newColumn, depth-1);
//            if (currentPlayer.isWhite()) {
//                if (apparentlyBestJump == null || evaluation < bestEvaluation) {
//                    bestEvaluation = evaluation;
//                    apparentlyBestJump = possibleJump;
//                    apparentlyBestJump.setEvaluation(bestEvaluation);
//                }
//            } else {
//                if (apparentlyBestJump == null || evaluation > bestEvaluation) {
//                    bestEvaluation = evaluation;
//                    apparentlyBestJump = possibleJump;
//                    apparentlyBestJump.setEvaluation(bestEvaluation);
//                }
//            }
//        }
//        if (apparentlyBestJump != null) System.out.println("bestMove: " + apparentlyBestJump.toString() +
//                "\nevaluation: " + apparentlyBestJump.getEvaluation()); // TODO delete
//        return apparentlyBestJump;
//    }
//
//    public Move bestMove(Player currentPlayer, Player nextPlayer, Pieces movingMan,
//                         int originalRow, int originalColumn, int depth) {
//        return bestMove(currentPlayer, nextPlayer, movingMan, originalRow, originalColumn, depth, 0);
//    }
//    public Jump bestJump(Player currentPlayer, Player nextPlayer, Pieces movingMan,
//                         int originalRow, int originalColumn, int depth) {
//        return bestJump(currentPlayer, nextPlayer, movingMan, originalRow, originalColumn, depth, 0);
//    }

    private double getHeuristicValue(Board board, Player currentPlayer) {
        double kingWeight = 1.5;
        double result = 0;

        if (currentPlayer.isWhite()) result = board.getNumberOfWhiteKings() * kingWeight +
                board.getNumberOfWhiteMen() - board.getNumberOfBlackKings() * kingWeight -
                board.getNumberOfBlackMen();
        else result = board.getNumberOfBlackKings() * kingWeight +
                board.getNumberOfBlackMen() - board.getNumberOfWhiteKings() * kingWeight -
                board.getNumberOfWhiteMen();

        return result;
    }

    public Move bestMove(Player currentPlayer, Player nextPlayer, Pieces movingMan, int originalRow, int originalColumn, int depth) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        List<Move> possibilities = getPossibleMoves(currentPlayer, movingMan, originalRow, originalColumn);
        List<Double> heuristics = new ArrayList<>();
        List<Double> tempHeuristics = new ArrayList<>();

        if (possibilities == null) return null;
        Board tempBoard;

        for (Move possibleMove : possibilities) {
            tempBoard = board.clone();
            tempBoard.moved(possibleMove);
            List<int[]> positionsOfTheNextPlayer = board.getCoordinatesList(nextPlayer);
            for (int[] positionCoordinate : positionsOfTheNextPlayer) {
                tempHeuristics.add(minimax(tempBoard, nextPlayer, currentPlayer,
                        tempBoard.getCoordinate(positionCoordinate[0], positionCoordinate[1]),
                        positionCoordinate[0], positionCoordinate[1], depth, alpha, beta));
            }
            heuristics.add(Collections.max(tempHeuristics));
        }

        double maxHeuristics = Double.NEGATIVE_INFINITY;
        Random random = new Random();
        for (int i = heuristics.size() - 1; i >= 0; i--) {
            if (heuristics.get(i) >= maxHeuristics) {
                maxHeuristics = heuristics.get(i);
            }
        }

        for (int i = 0; i < heuristics.size(); i++) {
            if (heuristics.get(i) < maxHeuristics) {
                heuristics.remove(i);
                possibilities.remove(i);
                i--;
            }
        }
        if (possibilities.isEmpty()) return null;
        return possibilities.get(random.nextInt(possibilities.size()));
    }

    public Jump bestJump(Player currentPlayer, Player nextPlayer, Pieces movingMan, int originalRow, int originalColumn, int depth) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        List<Jump> possibilities = getPossibleJumps(currentPlayer, movingMan, originalRow, originalColumn);

        if (possibilities == null) return null;
        Board tempBoard;

        for (Jump possibleJump : possibilities) {
            tempBoard = board.clone();
            tempBoard.jumped(possibleJump);
            int newRow = possibleJump.getNewRow();
            int newColumn = possibleJump.getNewColumn();
            possibleJump.setEvaluation(minimax(tempBoard, nextPlayer, currentPlayer,
                    tempBoard.getCoordinate(newRow, newColumn),
                    newRow, newColumn, depth, alpha, beta));
        }

        double maxHeuristics = Double.NEGATIVE_INFINITY;
        Random random = new Random();
        for (int i = possibilities.size() - 1; i >= 0; i--) {
            if (possibilities.get(i).getEvaluation() >= maxHeuristics) {
                maxHeuristics = possibilities.get(i).getEvaluation();
            }
        }

        for (int i = 0; i < possibilities.size(); i++) {
            if (possibilities.get(i).getEvaluation() < maxHeuristics) {
                possibilities.remove(i);
                i--;
            }
        }
        if (possibilities.isEmpty()) return null;
        return possibilities.get(random.nextInt(possibilities.size()));
    }

    private double minimax(Board board, Player currentPlayer, Player nextPlayer, Pieces movingMan,
                           int originalRow, int originalColumn, int depth, double alpha, double beta) {
        if (depth == 0) return getHeuristicValue(board, currentPlayer);

        if (isJumpingPossible(currentPlayer, movingMan, originalRow, originalColumn)) {
            List<Jump> possibilities = getPossibleJumps(currentPlayer, movingMan, originalRow, originalColumn);
            if (possibilities == null) return getHeuristicValue(board, currentPlayer);

            double initial;
            Board tempBoard;

            if (currentPlayer.isWhite()) {
                initial = Double.NEGATIVE_INFINITY;
                for (Jump possibleJump : possibilities) {
                    tempBoard = board.clone();
                    tempBoard.jumped(possibleJump);

                    int newRow = possibleJump.getNewRow();
                    int newColumn = possibleJump.getNewColumn();
                    double result = minimax(tempBoard, nextPlayer, currentPlayer, tempBoard.getCoordinate(newRow, newColumn),
                            newRow, newColumn, depth - 1, alpha, beta);

                    initial = Math.max(result, initial);
                    alpha = Math.max(alpha, initial);

                    if (alpha >= beta) break;
                }
            } else {
                initial = Double.POSITIVE_INFINITY;
                for (Jump possibleJump : possibilities) {
                    tempBoard = board.clone();
                    tempBoard.jumped(possibleJump);

                    int newRow = possibleJump.getNewRow();
                    int newColumn = possibleJump.getNewColumn();
                    double result = minimax(tempBoard, nextPlayer, currentPlayer, tempBoard.getCoordinate(newRow, newColumn),
                            newRow, newColumn, depth - 1, alpha, beta);

                    initial = Math.min(result, initial);
                    alpha = Math.min(alpha, initial);

                    if (alpha >= beta) break;
                }
            }
            return initial;
        }
        else {
            List<Move> possibilities = getPossibleMoves(currentPlayer, movingMan, originalRow, originalColumn);
            if (possibilities == null) return getHeuristicValue(board, currentPlayer);

            double initial;
            Board tempBoard;

            if (currentPlayer.isWhite()) {
                initial = Double.NEGATIVE_INFINITY;
                for (Move possibleMove : possibilities) {
                    tempBoard = board.clone();
                    tempBoard.moved(possibleMove);

                    List<int[]> positionsOfTheNextPlayer = board.getCoordinatesList(nextPlayer);
                    for (int[] positionCoordinate : positionsOfTheNextPlayer) {
                        double result = minimax(tempBoard, nextPlayer, currentPlayer,
                                tempBoard.getCoordinate(positionCoordinate[0], positionCoordinate[1]),
                                positionCoordinate[0], positionCoordinate[1], depth - 1, alpha, beta);
                        initial = Math.max(result, initial);
                        alpha = Math.max(alpha, initial);

                        if (alpha >= beta) break;
                    }
                }
            } else {
                initial = Double.POSITIVE_INFINITY;
                for (Move possibleMove : possibilities) {
                    tempBoard = board.clone();
                    tempBoard.moved(possibleMove);

                    List<int[]> positionsOfTheNextPlayer = board.getCoordinatesList(nextPlayer);
                    for (int[] positionCoordinate : positionsOfTheNextPlayer) {
                        double result = minimax(tempBoard, nextPlayer, currentPlayer,
                                tempBoard.getCoordinate(positionCoordinate[0], positionCoordinate[1]),
                                positionCoordinate[0], positionCoordinate[1], depth - 1, alpha, beta);
                        initial = Math.min(result, initial);
                        alpha = Math.min(alpha, initial);

                        if (alpha >= beta) break;
                    }
                }
            }
            return initial;
        }
    }
}
