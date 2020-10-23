import java.util.Scanner;

public class UserInterface {
    private Game game;
    private Scanner scanner;
    private String player1; // white == o
    private String player2; // black == x
    private boolean playerBool; // true == player1, false == player2

    public UserInterface() {
        game = new Game();
        scanner = new Scanner(System.in);

        game.generateMen();
        game.placeAllMenOnBoard();
    }

    public void startGame() {
        getPlayerNames();
        printBoard();
        while (true) {
            if (playerBool) System.out.println("\n\t\t" + player1 + "'s turn:");
            else System.out.println("\n\t\t" + player2 + "'s turn:");
            System.out.print("\tCommand? (move, history, quit, display)\n\t\t");
            String command = scanner.nextLine();

            if (command.equals("quit")) break;
            if (command.equals("history")) System.out.println(game.getHistory());
            if (command.equals("move")) move();
            if (command.equals("display")) printBoard();
            else continue;
        }
    }

    private void move() {
        System.out.print("\tNext move? (format: B2 -> C3)\n\t\t");

        String movement = scanner.nextLine();
        String regex = "[A-H][1-8] -> [A-H][1-8]";

        if (!movement.matches(regex)) printInvalidMove();
        else {
            int originalColumn = getColumn(movement.charAt(0)); // A - H
            int originalRow = getRow(movement.charAt(1)); // 1 - 8

            int nextColumn = getColumn(movement.charAt(6)); // A - H
            int nextRow = getRow(movement.charAt(7)); // 1 - 8

            Man move = game.getManByPosition(originalRow, originalColumn);
            if (move == null) printInvalidMove();
            else {
                if (game.move(playerBool, move, nextRow, nextColumn)) {
                    printBoard();
                    switchPlayers();
                }
                else printInvalidMove();
            }
        }
    }

    private void printBoard() {
        System.out.print(game.displayBoard());
    }

    private static void printInvalidMove() {
        System.out.println("\tInvalid move.");
    }

    private void getPlayerNames() {
        System.out.print("Enter first player's name (white): ");
        player1 = scanner.nextLine();

        System.out.print("Enter second player's name (black): ");
        player2 = scanner.nextLine();

        playerBool = true;
    }

    private void switchPlayers() {
        if (playerBool) playerBool = false;
        else playerBool = true;
    }

    private static int getColumn(char column) {
        return Columns.valueOf(String.valueOf(column)).ordinal();
    }

    private static int getRow(char row) {
        return Integer.valueOf(String.valueOf(row)) - 1;
    }
}
