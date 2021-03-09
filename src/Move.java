import java.util.Objects;

public class Move {
    /*
    Storing information about the move.
     */
    protected final Player player;
    protected final Pieces movingMan;
    protected final Coordinate originalCoord;
    protected final Coordinate jumpedCoord;
    protected final Pieces jumpedMan;
    protected final Coordinate newCoord;
    protected double evaluation; // TODO remove
    protected final boolean isJump;

    public Move(Player player, Pieces movingMan, Coordinate originalCoord,
                Coordinate newCoord) {
        this(player, movingMan, originalCoord, null, null, newCoord, 0, false);
    }

    public Move(Player player, Pieces movingMan, Coordinate originalCoord,
                Coordinate jumpedCoord, Pieces jumpedMan,
                Coordinate newCoord) {
        this(player, movingMan, originalCoord,
                jumpedCoord, jumpedMan,
                newCoord, 0, true);
    }
    public Move(Player player, Pieces movingMan, Coordinate originalCoord,
                Coordinate jumpedCoord, Pieces jumpedMan,
                Coordinate newCoord, double evaluation, boolean isJump) {
        this.player = player;
        this.movingMan = movingMan;
        this.originalCoord = originalCoord;
        this.jumpedCoord = jumpedCoord;
        this.jumpedMan = jumpedMan;
        this.newCoord = newCoord;
        this.evaluation = evaluation;
        this.isJump = isJump;
    }

    public Coordinate getOriginal() { return originalCoord; }


    public Pieces getMan() { return movingMan; }

    public Coordinate getNew() { return newCoord; }

    public void setEvaluation(double evaluation) { this.evaluation = evaluation; }

    public double getEvaluation() { return evaluation; }

    public boolean isJump() { return this.isJump; }

    public Coordinate getJumped() { return jumpedCoord; }

    public Pieces getJumpedMan() { return jumpedMan; }

    public Player getPlayer() { return this.player; }

    @Override
    public String toString() {
        return toStringWithoutPlayer() + " (" + player.getName() + ", " + Utils.whichMan(movingMan) + ")";
    }

    public String toStringWithoutPlayer() {
        return "" + originalCoord.toString() + " -> " + newCoord.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return originalCoord.equals(move.originalCoord) &&
                newCoord.equals(move.newCoord) &&
                Double.compare(move.evaluation, evaluation) == 0 &&
                isJump == move.isJump &&
                Objects.equals(player, move.player) &&
                movingMan == move.movingMan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, movingMan, originalCoord, newCoord, evaluation, isJump);
    }
}
