import java.util.Scanner;

public class UserInterface {
    private Game game;
    private Scanner scanner;

    private Player player1; // white == o
    private Player player2; // black == x
    private boolean playerBool; // true == player1, false == player2

    private String moveRegex;

    public UserInterface() {
        game = new Game();
        scanner = new Scanner(System.in);

        game.generateMen();
        game.placeAllMenOnBoard();

        moveRegex = "[A-H][1-8] -> [A-H][1-8]";
    }

    public void startGame() {
        getPlayerNames();
        printBoard();
        while (true) {
            if (playerBool) System.out.println("\n\t\t" + player1.getName() + "'s turn:");
            else System.out.println("\n\t\t" + player2.getName() + "'s turn:");

            System.out.print("\tCommand? (move, jump, history, score, display, quit)\n\t\t");
            String command = scanner.nextLine();

            if (command.equals("quit")) break;
            if (command.equals("history")) System.out.println(game.getHistory());
            if (command.equals("move")) move();
            if (command.equals("jump")) jump();
            if (command.equals("score")) printScore();
            if (command.equals("display")) printBoard();
            else continue;
        }
    }

    private void move() {
        System.out.print("\tNext move? (format: B2 -> C3)\n\t\t");
        String movement = scanner.nextLine();
        if (!movement.matches(moveRegex)) printInvalidMove();

        else {
            int originalColumn = getColumn(movement.charAt(0)); // A - H
            int originalRow = getRow(movement.charAt(1)); // 1 - 8

            int nextColumn = getColumn(movement.charAt(6)); // A - H
            int nextRow = getRow(movement.charAt(7)); // 1 - 8

            Man man = game.getManByPosition(originalRow, originalColumn);
            if (man == null) printInvalidMove();
            else {
                if (game.move(playerBool, man, nextRow, nextColumn)) {
                    printBoard();
                    switchPlayers();
                } else printInvalidMove();
            }
        }
    }

    private void jump() {
        System.out.print("\tNext move? (format: B2 -> C3)\n\t\t");
        String movement = scanner.nextLine();
        if (!movement.matches(moveRegex)) printInvalidMove();

        else {
            int originalColumn = getColumn(movement.charAt(0)); // A - H
            int originalRow = getRow(movement.charAt(1)); // 1 - 8

            int nextColumn = getColumn(movement.charAt(6)); // A - H
            int nextRow = getRow(movement.charAt(7)); // 1 - 8

            int jumpedColumn = getJumpedColumn(originalColumn, nextColumn);
            int jumpedRow = getJumpedRow(originalRow, nextRow);

            Man man = game.getManByPosition(originalRow, originalColumn);
            if (man == null) printInvalidMove();
            else {
                if (game.jump(playerBool, man, jumpedRow, jumpedColumn, nextRow, nextColumn)) {
                    successfulJump();
                    printBoard();
                    switchPlayers();
                } else printInvalidMove();
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
        player1 = new Player(scanner.nextLine(), true);

        System.out.print("Enter second player's name (black): ");
        player2 = new Player(scanner.nextLine(), false);

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

    private int getJumpedRow(int original, int next) {
        int difference = getDifference(original, next);
        if (difference == 0) return 0;
        if (difference != 2) return -1;
        if (playerBool) return original - 1;
        return original + 1;
    }

    private int getJumpedColumn(int original, int next) {
        int difference = getDifference(original, next);
        if (difference == 0) return 0;
        if (difference != 2) return -1;
        return original + 1;
    }

    private int getDifference(int x, int y) {
        return Math.abs(x - y);
    }

    private void successfulJump() {
        Player player = whichPlayer();
        player.addPoint();
        System.out.println("Well done, " + player.getName() + "! You now have " + player.getPoints() + " point(s).");
    }

    public Player whichPlayer() {
        if (playerBool) return player1;
        return player2;
    }

    public void printScore() {
        System.out.println(player1);
        System.out.println(player2);
    }
}