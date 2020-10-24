import java.util.Scanner;

public class UserInterface {
    private final Game game;
    private final Scanner scanner;

    private Player player1; // white == o
    private Player player2; // black == x
    private boolean playerBool; // true == player1, false == player2

    private final String moveRegex;
    private final String hintRegex;

    public UserInterface() {
        game = new Game();
        scanner = new Scanner(System.in);

        game.generateMen();
        game.placeAllMenOnBoard();

        moveRegex = "[A-H][1-8] -> [A-H][1-8]";
        hintRegex = "[A-H][1-8] -> ?";
    }

    public void startGame() {
        getPlayerNames();
        printBoard();
        while (true) {
            if (playerBool) System.out.println("\n\t\t" + player1.getName() + "'s turn:");
            else System.out.println("\n\t\t" + player2.getName() + "'s turn:");

            System.out.print("\tCommand? (B2 -> C3, B2 -> ?, history, score, display, save, load, quit)\n\t\t");
            String command = scanner.nextLine();

            if (command.equals("quit")) break;
            if (command.equals("history")) System.out.println(game.getHistory());
            if (command.matches(moveRegex)) movement(command);
            if (command.matches(hintRegex)) hint(command);
            if (command.equals("score")) printScore();
            if (command.equals("display")) printBoard();
            if (command.equals("save")) saveGame();
            if (command.equals("load")) loadGame();
        }
    }

    private void movement(String movement) {
        int[] coordinates = Utils.getCoordinates(movement, whichPlayer().isWhite());

        Man man = game.getManByPosition(coordinates[0], coordinates[1]);
        if (man == null) printInvalidMove();
        else {
            if (Utils.containsMinus(coordinates)) move(man, coordinates);
            else jump(man, coordinates);
        }
    }

    private void move(Man man, int[] coordinates) {
        if (game.move(whichPlayer(), man, coordinates[2], coordinates[3])) {
            printBoard();
            switchPlayers();
        } else printInvalidMove();
    }

    private void jump(Man man, int[] coordinates) {
        if (game.jump(whichPlayer(), man,
                coordinates[4], coordinates[5], coordinates[2], coordinates[3])) {
            printBoard();
            switchPlayers();
        } else printInvalidMove();
    }

    private void printBoard() {
        System.out.print(game.displayBoard());
    }

    private static void printInvalidMove() {
        System.out.println("\tInvalid move.");
    }

    private void getPlayerNames() {
        System.out.print("Enter first player's name (white): ");
        player1 = new Player(scanner.nextLine(), true);

        System.out.print("Enter second player's name (black): ");
        player2 = new Player(scanner.nextLine(), false);

        playerBool = true;
    }

    private void switchPlayers() {
        if (playerBool) playerBool = false;
        else playerBool = true;
    }

    public Player whichPlayer() {
        if (playerBool) return player1;
        return player2;
    }

    public void printScore() {
        System.out.println(player1);
        System.out.println(player2);
    }

    private void hint(String command) {
        // TODO implement hinting mechanism, aka generating all possible moves
    }

    private void saveGame() {
        // TODO saving mechanism
    }

    private void loadGame() {
        // TODO loading mechanism
    }
}
