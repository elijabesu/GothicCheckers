import java.util.ArrayList;
import java.util.List;

public class Rules {
    Board board;

    public Rules(Board board) {
        this.board = board;
    }

    public boolean isValid(Player player, Pieces movingMan, Move move) {
        List<List> movesAndJumps = generateMoves(player, movingMan, move.getOriginalRow(), move.getOriginalColumn());
        List<Move> possibilities = addIntoPossibilities(movesAndJumps.get(0), movesAndJumps.get(1));
        if (possibilities == null || possibilities.isEmpty()) return false;
        return possibilities.contains(move);
    }

    public List<List> generateMoves(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        if (player.isWhite() != movingMan.isWhite()) return null;

        List<Move> possibleMoves = new ArrayList<>();
        List<Jump> possibleJumps = new ArrayList<>();
        List<List> possibilities = new ArrayList<>();

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

        if (rowDifference == 0 && columnDifference == 1) return true; // left and right
        if (rowDifference == 1 && (columnDifference == 0 | columnDifference == 1)) return true; // rest

        return false;
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

        if (rowDifference == 0 && columnDifference == 2) return true; // left and right
        if (rowDifference == 2 && (columnDifference == 0 || columnDifference == 2)) return true; // rest

        return false;
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
        if (maybeMoreJumps == null || maybeMoreJumps.size() == 0) return false;
        return true;
    }

    private List<List> generateKingMoves(Player player, Pieces movingMan, int originalRow, int originalColumn,
                                         List<List> possibilities, List<Move> possibleMoves, List<Jump> possibleJumps) {
        List<int[]> skipPositions = new ArrayList<>();

        int sameRow = originalRow;
        int sameColumn = originalColumn;

        for (int i = 0; i < board.getSize(); i++) {
            for (int row: new int[] {sameRow, sameRow - (i + 1), sameRow + (i + 1)}) {
                if (row < 0 || row > 7) continue;
                for (int column: new int[] {sameColumn, sameColumn - (i + 1), sameColumn + (i + 1)}) {
                    if (column < 0 || column > 7) continue;
                    if (Utils.listOfArraysContains(skipPositions, row, column)) continue;
                    if (board.isOccupied(row, column)) generateKingJump(player, movingMan, originalRow, originalColumn,
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
        // TODO check so that Kings cannot jump over multiple enemy men
        if (movingMan.isSameColourAs(board.getCoordinate(row, column))) return;// if they are the same colour

        for (int i = 1; i < board.getSize(); i++) {
            int nextRow = row;
            if (originalRow - nextRow < 0) nextRow = row + i;
            else if (originalRow - nextRow > 0) nextRow = row - i;

            int nextColumn = column;
            if (originalColumn - nextColumn < 0) nextColumn = column + i;
            else if (originalColumn - nextColumn > 0) nextColumn = column - i;

            if (nextRow < 0 || nextRow > 7 || nextColumn < 0 || nextColumn > 7) return;

            if (board.isOccupied(nextRow, nextColumn)) return;

            possibleJumps.add(new Jump(player, movingMan, originalRow, originalColumn,
                    row, column, board.getCoordinate(row, column), // jumpedRow, jumpedColumn, jumpedValue
                    nextRow, nextColumn));

            skipPositions.add(new int[]{nextRow, nextColumn});
        }
    }

    public boolean needsPromotion(Pieces movingMan, int newRow) {
        if (!movingMan.isWhite() && newRow == 7) return true;
        if (movingMan.isWhite() && newRow == 0) return true;
        return false;
    }

    private List<Move> addIntoPossibilities(List<Move> possibleMoves, List<Jump> possibleJumps) {
        List<Move> possibilities = new ArrayList<>();

        for (Jump jump: possibleJumps) {
            possibilities.add(jump);
        }
        for (Move move: possibleMoves) {
            possibilities.add(move);
        }
        return possibilities;
    }

    public List<Move> getPossibleMoves(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        return generateMoves(player, movingMan, originalRow, originalColumn).get(0);
    }

    public List<Jump> getPossibleJumps(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        return generateMoves(player, movingMan, originalRow, originalColumn).get(1);
    }

    public List<Move> getPossibilities(Player player, Pieces movingMan, int originalRow, int originalColumn) {
        List<List> possibilities = generateMoves(player, movingMan, originalRow, originalColumn);
        if (possibilities == null) return null;
        return addIntoPossibilities(possibilities.get(0), possibilities.get(1));
    }
}
