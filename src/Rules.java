import java.util.ArrayList;
import java.util.List;

public class Rules {

    public static List<Move> generateMoves(Player player, Man movingMan, Board board) {
        if (player.isWhite() != movingMan.isWhite()) return null;

        List<Move> possibleMoves = new ArrayList<>();
        List<Jump> possibleJumps = new ArrayList<>();
        List<Move> possibilities = new ArrayList<>();

        int[] rows = Utils.generateArrayOfThree(movingMan.getRow());
        int[] columns = Utils.generateArrayOfThree(movingMan.getColumn());

        for (int row: rows) {
            if (row < 0 || row > 7) continue;
            for (int column: columns) {
                if (column < 0 || column > 7) continue;
                if (board.isOccupied(row, column)) possibleJumps.add(generateJump(player, movingMan, board, row, column));
                else {
                    Move maybeMove = new Move(player, movingMan, row, column);
                    if (isValidMove(player, maybeMove, false)) possibleMoves.add(maybeMove);
                }
            }
        }

        for (Jump jump: possibleJumps) {
            if (jump == null) continue;
            possibilities.add(jump);
        }
        for (Move move: possibleMoves) {
            if (move == null) continue;
            possibilities.add(move);
        }

        return possibilities;
    }

    private static Jump generateJump(Player player, Man movingMan, Board board, int row, int column) {
        if (board.getCoordinate(row, column) == movingMan.getValue().getValue()) return null;

        int nextRow = row - (movingMan.getRow() - row);
        int nextColumn = column - (movingMan.getColumn() - column);

        if (board.isOccupied(nextRow, nextColumn)) return null;
        Jump maybeJump = new Jump(player, movingMan,
                row, column, board.getCoordinate(row, column), // jumpedRow, jumpedColumn, jumpedValue
                nextRow, nextColumn);
        if (isValidJump(player, maybeJump, board.isOccupied(nextRow, nextColumn))) return maybeJump;

        return null;
    }

    public static boolean isValidMove(Player player, Move move, boolean newPositionOccupied) { //TODO doesn't work for Kings
        if (!(basicValidation(player, move, newPositionOccupied))) return false;

        if (move.getMan().isKing()) isValidMoveForKing(move);

        int rowDifference = Math.abs(move.getOriginalRow() - move.getNewRow());
        int columnDifference = Math.abs(move.getOriginalColumn() - move.getNewColumn());

        if (rowDifference == 0 && columnDifference == 1) return true; // left and right
        if (rowDifference == 1 && (columnDifference == 0 | columnDifference == 1)) return true; // rest

        return false;
    }

    public static boolean isValidJump(Player player, Jump jump, boolean newPositionOccupied) {
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

    private static boolean basicValidation(Player player, Move move, boolean newPositionOccupied) {
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

    public static boolean needsPromotion(Man movingMan, int newRow) {
        if (movingMan.getValue() == Pieces.BLACK && newRow == 7) return true;
        if (movingMan.getValue() == Pieces.WHITE && newRow == 0) return true;
        return false;
    }

    public static boolean isValid(Player player, Man movingMan, Move move, Board board) {
        List<Move> possibilities = generateMoves(player, movingMan, board);
        if (possibilities == null || possibilities.isEmpty()) return false;
        return possibilities.contains(move);
    }

    public static boolean isPossibleAnotherJump() {
        return false;
    }

    private static boolean isValidMoveForKing(Move move) {
        return false;
    }
}
