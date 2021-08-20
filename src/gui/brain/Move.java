package gui.brain;

import shared.Player;
import shared.Utils;
import ui.Pieces;

import javax.swing.*;
import java.awt.*;

public class Move {
    private final Player player;
    private final ImageIcon movingMan;
    private final Point originalCoordinate;
    private final Point jumpedCoordinate;
    private final ImageIcon jumpedMan;
    private final Point newCoordinate;
    private final boolean isJump;

    public Move(Player player, ImageIcon movingMan, Point originalCoordinate,
                Point newCoordinate) {
        this(player, movingMan, originalCoordinate, null, null, newCoordinate, false);
    }

    public Move(Player player, ImageIcon movingMan, Point originalCoordinate,
                Point jumpedCoord, ImageIcon jumpedMan,
                Point newCoordinate) {
        this(player, movingMan, originalCoordinate,
                jumpedCoord, jumpedMan,
                newCoordinate, true);
    }
    public Move(Player player, ImageIcon movingMan, Point originalCoordinate,
                Point jumpedCoordinate, ImageIcon jumpedMan,
                Point newCoordinate, boolean isJump) {
        this.player = player;
        this.movingMan = movingMan;
        this.originalCoordinate = originalCoordinate;
        this.jumpedCoordinate = jumpedCoordinate;
        this.jumpedMan = jumpedMan;
        this.newCoordinate = newCoordinate;
        this.isJump = isJump;
    }

    public Point getOriginal() { return originalCoordinate; }

    public ImageIcon getMan() { return movingMan; }

    public Point getNew() { return newCoordinate; }

    public boolean isJump() { return this.isJump; }

    public Point getJumped() { return jumpedCoordinate; }

    public ImageIcon getJumpedMan() { return jumpedMan; }

    public Player getPlayer() { return this.player; }

    @Override
    public String toString() {
        return toStringWithoutPlayer() + " (" + player.getName() + ")";
    }

    public String toStringWithoutPlayer() {
        return "" + originalCoordinate.toString() + " -> " + newCoordinate.toString();
    }
}
