package brain;

import java.util.Objects;

public class Move {
    /*
    Storing information about the move.
     */
    protected final Player player;
    protected final Pieces movingMan;
    protected final Coordinate originalCoordinate;
    protected final Coordinate jumpedCoordinate;
    protected final Pieces jumpedMan;
    protected final Coordinate newCoordinate;
    protected final boolean isJump;

    public Move(Player player, Pieces movingMan, Coordinate originalCoordinate,
                Coordinate newCoordinate) {
        this(player, movingMan, originalCoordinate, null, null, newCoordinate, false);
    }

    public Move(Player player, Pieces movingMan, Coordinate originalCoordinate,
                Coordinate jumpedCoord, Pieces jumpedMan,
                Coordinate newCoordinate) {
        this(player, movingMan, originalCoordinate,
                jumpedCoord, jumpedMan,
                newCoordinate, true);
    }
    public Move(Player player, Pieces movingMan, Coordinate originalCoordinate,
                Coordinate jumpedCoordinate, Pieces jumpedMan,
                Coordinate newCoordinate, boolean isJump) {
        this.player = player;
        this.movingMan = movingMan;
        this.originalCoordinate = originalCoordinate;
        this.jumpedCoordinate = jumpedCoordinate;
        this.jumpedMan = jumpedMan;
        this.newCoordinate = newCoordinate;
        this.isJump = isJump;
    }

    public Coordinate getOriginal() { return originalCoordinate; }


    public Pieces getMan() { return movingMan; }

    public Coordinate getNew() { return newCoordinate; }

    public boolean isJump() { return this.isJump; }

    public Coordinate getJumped() { return jumpedCoordinate; }

    public Pieces getJumpedMan() { return jumpedMan; }

    public Player getPlayer() { return this.player; }

    @Override
    public String toString() {
        return toStringWithoutPlayer() + " (" + player.getName() + ", " + Utils.whichMan(movingMan) + ")";
    }

    public String toStringWithoutPlayer() {
        return "" + originalCoordinate.toString() + " -> " + newCoordinate.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return originalCoordinate.equals(move.originalCoordinate) &&
                newCoordinate.equals(move.newCoordinate) &&
                isJump == move.isJump &&
                Objects.equals(player, move.player) &&
                movingMan == move.movingMan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, movingMan, originalCoordinate, newCoordinate, isJump);
    }
}
