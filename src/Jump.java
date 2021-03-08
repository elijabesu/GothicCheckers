import java.util.Objects;

public class Jump extends Move {

    public Jump(Player player, Pieces movingMan, Coordinate originalCoord,
                Coordinate jumpedCoord, Pieces jumpedMan,
                Coordinate newCoord) {
        this(player, movingMan, originalCoord, jumpedCoord, jumpedMan, newCoord, 0);
    }

    public Jump(Player player, Pieces movingMan, Coordinate originalCoord,
                Coordinate jumpedCoord, Pieces jumpedMan,
                Coordinate newCoord, double evaluation) {
        super(player, movingMan, originalCoord,
                jumpedCoord, jumpedMan,
                newCoord, evaluation, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Jump jump = (Jump) o;
        return jumpedCoord == jump.jumpedCoord &&
                jumpedMan == jump.jumpedMan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), jumpedCoord, jumpedMan);
    }
}
