import java.util.ArrayList;
import java.util.List;

public class Rules {
    Board board;
    List<Move> possibleMoves;
    List<Jump> possibleJumps;
    List<Move> possibilities;

    public Rules(Board board) {
        this.board = board;
        this.possibleMoves = new ArrayList<>();
        this.possibleJumps = new ArrayList<>();
        this.possibilities = new ArrayList<>();
    }

    public boolean isValid(Player player, Man movingMan, Move move) {
        clearAll();
        generateMoves(player, movingMan);
        addIntoPossibilities();
        if (possibilities == null || possibilities.isEmpty()) return false;
        return possibilities.contains(move);
    }

    public void generateMoves(Player player, Man movingMan) {
        if (player.isWhite() != movingMan.isWhite()) return;

        if (movingMan.isKing()) {
            generateKingMoves(player, movingMan);
            return;
        }

        int[] rows = Utils.generateArrayOfThree(movingMan.getRow());
        int[] columns = Utils.generateArrayOfThree(movingMan.getColumn());

        for (int row: rows) {
            if (row < 0 || row > 7) continue;
            for (int column: columns) {
                if (column < 0 || column > 7) continue;
                if (board.isOccupied(row, column)) generateJump(player, movingMan, row, column);
                else {
                    Move maybeMove = new Move(player, movingMan, row, column);
                    if (isValidMove(player, maybeMove)) possibleMoves.add(maybeMove);
                }
            }
        }
    }

    private void generateJump(Player player, Man movingMan, int row, int column) {
        if (board.getCoordinate(row, column) == movingMan.getValue().getNumberValue()) return;

        int nextRow = row - (movingMan.getRow() - row);
        int nextColumn = column - (movingMan.getColumn() - column);

        if (nextRow < 0 || nextRow > 7 || nextColumn < 0 || nextColumn > 7) return;

        if (board.isOccupied(nextRow, nextColumn)) return;
        Jump maybeJump = new Jump(player, movingMan,
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

        int jumpedMan = jump.getJumpedMan();

        if (jumpedMan == 0) return false;

        int rowDifference = Math.abs(jump.getOriginalRow() - jump.getNewRow());
        int columnDifference = Math.abs(jump.getOriginalColumn() - jump.getNewColumn());

        if (player.isWhite()) {
            if (jumpedMan < 0) return false; // if the WHITE player is trying to jump over another WHITE man -> NOPE
        } else {
            if (jumpedMan > 0) return false; // if the BLACK player is trying to jump over another BLACK man -> NOPE
        }

        if (rowDifference == 0 && columnDifference == 2) return true; // left and right
        if (rowDifference == 2 && (columnDifference == 0 || columnDifference == 2)) return true; // rest

        return false;
    }

    private boolean basicValidation(Player player, Move move, boolean newPositionOccupied) {
        Man movingMan = move.getMan();

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

    public boolean possibleAnotherJump() {
        // TODO
        return false;
    }

    private void generateKingMoves(Player player, Man movingMan) {
        List<Integer> skipRows = new ArrayList<>();
        List<Integer> skipColumns = new ArrayList<>();

        int sameRow = movingMan.getRow();
        int sameColumn = movingMan.getColumn();

        for (int i = 0; i < board.getSize(); i++) {
            for (int row: new int[] {sameRow, sameRow - (i + 1), sameRow + (i + 1)}) {
                if (row < 0 || row > 7) continue;
                for (int column: new int[] {sameColumn, sameColumn - (i + 1), sameColumn + (i + 1)}) {
                    if (column < 0 || column > 7) continue;
                    if (skipRows.contains(row) && skipColumns.contains(column)) continue;
                    if (board.isOccupied(row, column)) generateKingJump(player, movingMan, row, column,
                            skipRows, skipColumns);
                    else possibleMoves.add(new Move(player, movingMan, row, column));
                }
            }
        }
    }

    private void generateKingJump(Player player, Man movingMan, int row, int column,
                                  List<Integer> skipRows, List<Integer> skipColumns) {
        // FIXME jumps don't work when jumping over multiple empty spots
        // TODO check so that Kings cannot jump over multiple enemy men
        if (movingMan.getValue().isSameColourAs(board.getCoordinate(row, column))) return;// if they are the same colour

        int nextRow = row;
        if (movingMan.getRow() - nextRow < 0) nextRow = row + 1;
        else if (movingMan.getRow() - nextRow > 0) nextRow = row - 1;

        int nextColumn = column;
        if (movingMan.getColumn() - nextColumn < 0) nextColumn = column + 1;
        else if (movingMan.getColumn() - nextColumn > 0) nextColumn = column - 1;

        if (nextRow < 0 || nextRow > 7 || nextColumn < 0 || nextColumn > 7) return;

        if (board.isOccupied(nextRow, nextColumn)) return;

        possibleJumps.add(new Jump(player, movingMan,
                row, column, board.getCoordinate(row, column), // jumpedRow, jumpedColumn, jumpedValue
                nextRow, nextColumn));

        skipRows.add(nextRow);
        skipColumns.add(nextColumn);
    }

    public boolean needsPromotion(Man movingMan, int newRow) {
        if (movingMan.getValue() == Pieces.BLACK && newRow == 7) return true;
        if (movingMan.getValue() == Pieces.WHITE && newRow == 0) return true;
        return false;
    }

    private void clearAll() {
        possibleMoves.clear();
        possibleJumps.clear();
        possibilities.clear();
    }

    private void addIntoPossibilities() {
        for (Jump jump: possibleJumps) {
            possibilities.add(jump);
        }
        for (Move move: possibleMoves) {
            possibilities.add(move);
        }
    }

    public List<Move> getPossibleMoves(Player player, Man movingMan) {
        clearAll();
        generateMoves(player, movingMan);
        return possibleMoves;
    }

    public List<Jump> getPossibleJumps(Player player, Man movingMan) {
        clearAll();
        generateMoves(player, movingMan);
        return possibleJumps;
    }

    public List<Move> getPossibilities(Player player, Man movingMan) {
        clearAll();
        generateMoves(player, movingMan);
        addIntoPossibilities();
        return possibilities;
    }
}
