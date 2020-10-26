public class Player {
    private int points;
    private final String name;
    private final boolean white; // true == white, false == black
    private final boolean computer;

    public Player(String name, boolean white) {
        this(name, white, false);
    }

    public Player(String name, boolean white, boolean computer) {
        this.name = name;
        this.white = white;
        this.computer = computer;
        this.points = 0;
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
