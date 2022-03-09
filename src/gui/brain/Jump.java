package gui.brain;

import shared.Coordinate;
import shared.Player;
import shared.Utils;

import java.util.Objects;

public class Jump extends Move {
    /*
    Extension to the brain.Move class.    TODO maybe remove this class?
     */

    public Jump(Player player, Piece movingMan, Piece jumpedMan, Coordinate originalCoordinate,
                Coordinate jumpedCoordinate, Coordinate newCoordinate) {
        super(player, movingMan, originalCoordinate,
                jumpedCoordinate, jumpedMan,
                newCoordinate, true);
    }

    @Override
    public String toString() {
        return toStringWithoutPlayer() + " (" + player.getName() + ")";
    }

    public String toStringWithoutPlayer() {
        return
                Utils.convertColumnToString(originalCoordinate.getColumn()) +
                        Utils.convertRowToString(originalCoordinate.getRow()) +
                        " -> " +
                        Utils.convertColumnToString(jumpedCoordinate.getColumn()) +
                        Utils.convertRowToString(jumpedCoordinate.getRow()) +
                        " (" + jumpedMan.toString() + ") -> " +
                        Utils.convertColumnToString(newCoordinate.getColumn()) +
                        Utils.convertRowToString(newCoordinate.getRow());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jump)) return false;
        Jump jump = (Jump) o;
        return isJump == jump.isJump && player.equals(jump.player) &&
                movingMan.equals(jump.movingMan) && jumpedMan.equals(jump.jumpedMan) &&
                originalCoordinate.equals(jump.originalCoordinate) &&
                jumpedCoordinate.equals(jump.jumpedCoordinate) &&
                newCoordinate.equals(jump.newCoordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, movingMan, jumpedMan, originalCoordinate, jumpedCoordinate, newCoordinate, isJump);
    }
}
