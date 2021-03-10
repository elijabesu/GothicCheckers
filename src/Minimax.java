import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Minimax {

    private static double getHeuristicValue(Board board, Player currentPlayer, int difficulty) {
        double kingWeight = 1.5;
        double random = new Random().nextDouble();

        double result = Utils.getValueDependingOnColour(currentPlayer.isWhite(),
                board.getNumberOf(true, true), board.getNumberOf(true, false),
                board.getNumberOf(false, true), board.getNumberOf(false, false), kingWeight);

        if (difficulty == 1) result = result * 0.75 + random * 0.25;
        if (difficulty == 0) result = result * 0.5 + random * 0.5;
        return result;
    }

    private static double minimax(Rules rules, Board board, int difficulty, Player currentPlayer, Player nextPlayer, Pieces movingMan,
                           Coordinate coordinate, int depth, double alpha, double beta) {
        if (depth == 0) return getHeuristicValue(board, currentPlayer, difficulty);

        List<Move> possibleMoves = rules.getAllPossibilities(currentPlayer, movingMan, coordinate);
        double initialValue;

        if (currentPlayer.isWhite()) initialValue = Double.POSITIVE_INFINITY;
        else initialValue = Double.NEGATIVE_INFINITY;

        for (Move possibleMove : possibleMoves) {
            board.moveOrJump(possibleMove);
            for (Coordinate enemyCoordinate : board.getCoordinatesList(nextPlayer)) {
                double result = minimax(rules, board, difficulty, nextPlayer, currentPlayer,
                        board.getCoordinate(enemyCoordinate),
                        enemyCoordinate, depth - 1, alpha, beta);

                initialValue = Utils.getValueDependingOnColour(currentPlayer.isWhite(), result, initialValue);
                alpha = Utils.getValueDependingOnColour(currentPlayer.isWhite(), alpha, initialValue);

                if (alpha >= beta) break;
            }
            board.undoMoveOrJump(possibleMove);
            if (alpha >= beta) break;
        }
        return initialValue;
    }

    public static Move bestMove(Rules rules, Board board, int difficulty, Player currentPlayer, Player nextPlayer, Pieces movingMan,
                         Coordinate coordinate, int depth) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        List<Move> possibleMoves = rules.getAllPossibilities(currentPlayer, movingMan, coordinate);
        if (possibleMoves.isEmpty()) return null;

        List<Double> heuristics = new ArrayList<>();

        for (Move possibleMove : possibleMoves) {
            heuristics.add(getMoveEvaluation(rules, board, difficulty, possibleMove,
                    nextPlayer, currentPlayer, depth, alpha, beta));
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

    public static double getMoveEvaluation(Rules rules, Board board, int difficulty, Move move,
                                           Player currentPlayer, Player nextPlayer,
                                           int depth, double alpha, double beta) {
        List<Double> heuristics = new ArrayList<>();
        board.moveOrJump(move);
        for (Coordinate enemyCoordinate : board.getCoordinatesList(nextPlayer)) {
            heuristics.add(minimax(rules, board, difficulty, nextPlayer, currentPlayer,
                    board.getCoordinate(enemyCoordinate),
                    enemyCoordinate, depth - 1, alpha, beta));
        }
        board.undoMoveOrJump(move);
        return Utils.getValueDependingOnColour(currentPlayer.isWhite(), heuristics);
    }
}
