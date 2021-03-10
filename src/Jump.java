import java.util.Objects;

public class Jump extends Move {
    /*
    Extension to the Move class.    TODO maybe remove this class?
     */

    public Jump(Player player, Pieces movingMan, Coordinate originalCoordinate,
                Coordinate jumpedCoordinate, Pieces jumpedMan, Coordinate newCoordinate) {
        super(player, movingMan, originalCoordinate,
                jumpedCoordinate, jumpedMan,
                newCoordinate, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Jump jump = (Jump) o;
        return jumpedCoordinate == jump.jumpedCoordinate &&
                jumpedMan == jump.jumpedMan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), jumpedCoordinate, jumpedMan);
    }
}
