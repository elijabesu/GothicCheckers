public class Player {
    private int points;
    private final String name;
    private final boolean white; // true == white, false == black

    public Player(String name, boolean white) {
        this.name = name;
        this.points = 0;
        this.white = white;
    }

    public int getPoints() {
        return points;
    }

    public void addPoint() {
        ++points;
    }

    public boolean isWhite() {
        return white;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + points + " point(s)";
    }
}
