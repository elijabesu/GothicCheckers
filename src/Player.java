public class Player {
    private int points;
    private String name;
    private final boolean white; // true == white, false == black
    private boolean computer;

    public Player(String name, boolean white) {
        this(name, white, false);
    }

    public Player(String name, boolean white, boolean computer) {
        this.white = white;
        this.computer = computer;
        this.points = 0;
        if (name.isEmpty()) name = "Player";
        this.name = name;
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

    public boolean isComputer() { return computer; }

    public String getName() {
        return name;
    }

    public void changePlayer(String name, boolean computer) {
        this.computer = computer;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ": " + points + " point(s)";
    }
}
