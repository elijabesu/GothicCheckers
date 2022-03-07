package gui.brain;

import shared.Coordinate;
import shared.Player;
import shared.Utils;

import java.util.Objects;

public class Move {
    protected final Player player;
    protected final Piece movingMan;
    protected final Coordinate originalCoordinate;
    protected final Coordinate jumpedCoordinate;
    protected final Piece jumpedMan;
    protected final Coordinate newCoordinate;
    protected final boolean isJump;

    public Move(Player player, Piece movingMan, Coordinate originalCoordinate,
                Coordinate newCoordinate) {
        this(player, movingMan, originalCoordinate, null, null, newCoordinate, false);
    }

    public Move(Player player, Piece movingMan, Coordinate originalCoordinate,
                Coordinate jumpedCoord, Piece jumpedMan,
                Coordinate newCoordinate) {
        this(player, movingMan, originalCoordinate,
                jumpedCoord, jumpedMan,
                newCoordinate, true);
    }
    public Move(Player player, Piece movingMan, Coordinate originalCoordinate,
                Coordinate jumpedCoordinate, Piece jumpedMan,
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

    public Piece getMan() { return movingMan; }

    public Coordinate getNew() { return newCoordinate; }

    public boolean isJump() { return this.isJump; }

    public Coordinate getJumped() { return jumpedCoordinate; }

    public Piece getJumpedMan() { return jumpedMan; }

    public Player getPlayer() { return this.player; }

    @Override
    public String toString() {
        return toStringWithoutPlayer() + " (" + player.getName() + ")";
    }

    public String toStringWithoutPlayer() {
        return
                Utils.convertColumnToString(originalCoordinate.getColumn()) +
                        Utils.convertRowToString(originalCoordinate.getRow()) +
                        " -> " +
                        Utils.convertColumnToString(newCoordinate.getColumn()) +
                        Utils.convertRowToString(newCoordinate.getRow());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return isJump == move.isJump && player.equals(move.player) && movingMan.equals(move.movingMan) && originalCoordinate.equals(move.originalCoordinate) && newCoordinate.equals(move.newCoordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, movingMan, originalCoordinate, newCoordinate, isJump);
    }
}
