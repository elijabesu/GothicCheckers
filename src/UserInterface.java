import java.util.Scanner;

public class UserInterface {
    private final Game game;
    private final Scanner scanner;

    private Player player1; // white == o
    private Player player2; // black == x

    private final String moveRegex;
    private final String hintRegex;

    private final int depth;

    public UserInterface() {
        game = new Game();
        scanner = new Scanner(System.in);

        moveRegex = "[A-H][1-8] -> [A-H][1-8]";
        hintRegex = "[A-H][1-8] ->";

        depth = 3;
    }

    public void startGame() {
        getPlayerNames();
        printBoard();

        while (true) {
            Player currentPlayer = null;
            Player nextPlayer = null;

            if (game.getPlayerBool()) {
                currentPlayer = player1;
                nextPlayer = player2;
            }
            else {
                currentPlayer = player2;
                nextPlayer = player1;
            }

            System.out.println("\n\t\t" + currentPlayer.getName() + "'s turn:");

            if (currentPlayer.isComputer()) computerMove(currentPlayer, nextPlayer);
            else if (!readCommand()) break;

            if (game.shouldEnd(player1, player2)) break;

            if (currentPlayer.isComputer() && nextPlayer.isComputer()) {
                System.out.print("Intervene? (y/n) ");
                if (scanner.nextLine().trim().equals("y")) if (!readCommand()) break;
            }
        }
        endGame();
    }

    private void movement(String movement) {
        int[] origCoords = Utils.getCoordinate(movement, 1, 0);

        Pieces man = game.getManByPosition(origCoords[0], origCoords[1]);

        if (man == Pieces.EMPTY) printInvalidMove();
        else {
            int[] coordinates = Utils.getCoordinates(movement, whichPlayer().isWhite());
            if (man.isKing() || Utils.containsMinus(coordinates)) move(man, coordinates);
            else jump(man, coordinates);
        }
    }

    private void move(Pieces man, int[] coordinates) {
        if (man.isKing()) {
            Move gameKingMove = game.moveKing(whichPlayer(), man, coordinates);
            if (gameKingMove != null) afterMove();
            else {
                Jump gameKingJump = game.jumpKing(whichPlayer(), man, coordinates);
                if (gameKingJump != null) afterJump(whichPlayer(), man, gameKingJump);
                else printInvalidMove();
            }
        }
        else {
            Move gameMove = game.move(whichPlayer(), man, coordinates);
            if (gameMove != null) afterMove();
            else printInvalidMove();
        }
    }

    private void jump(Pieces man, int[] coordinates) {
        Jump gameJump = game.jump(whichPlayer(), man, coordinates);
        if (gameJump != null) afterJump(whichPlayer(), man, gameJump);
        else printInvalidMove();
    }

    private void printBoard() {
        System.out.print(game.displayBoard());
    }

    private static void printInvalidMove() {
        System.out.println("\tInvalid move.");
    }

    private void getPlayerNames() {
        System.out.print("Enter first player's name (white): ");
        String player1Name = scanner.nextLine();
        if (player1Name.contains("ai")) player1 = new Player(player1Name, true, true);
        else player1 = new Player(player1Name, true);

        System.out.print("Enter second player's name (black): ");
        String player2Name = scanner.nextLine();
        if (player2Name.contains("ai")) player2 = new Player(player2Name, false, true);
        else player2 = new Player(player2Name, false);
    }

    private Player whichPlayer() {
        if (game.getPlayerBool()) return player1;
        return player2;
    }

    private void printScore() {
        System.out.println(player1);
        System.out.println(player2);
    }

    private void hint(String command) {
        int[] coordinate = Utils.getCoordinate(command, 1, 0);
        Pieces man = game.getManByPosition(coordinate[0], coordinate[1]);
        if (man == Pieces.EMPTY) printInvalidMove();
        else {
            //String hint = game.hint(whichPlayer(), man, coordinate[0], coordinate[1]);
            Player otherPlayer = whichPlayer();
            if (whichPlayer().equals(player1)) otherPlayer = player2;
            else otherPlayer = player1;
            String hint = game.hint(whichPlayer(), otherPlayer, man, coordinate[0], coordinate[1], depth);
            if (hint.length() == 0) printInvalidMove();
            else {
                System.out.println("Possible moves: " + hint);
            }
        }
    }

    private void saveGame() {
        if (game.save()) System.out.println("Successfully saved the game.");
    }

    private void loadGame() {
        System.out.print("Filename? ");
        String fileName = scanner.nextLine();
        if (game.load(game, player1, player2, fileName)) {
            System.out.println("Successfully loaded the game.");
            printBoard();
        }
    }

    private void endGame() {
        if (player1.getPoints() > player2.getPoints()) System.out.println(player1.getName() + " won!");
        else if (player1.getPoints() < player2.getPoints()) System.out.println(player2.getName() + " won!");
        else System.out.println("It's a draw!");
    }

    private void afterMove() {
        printBoard();
        game.switchPlayers();
    }

    private void afterJump(Player player, Pieces movingMan, Jump jump) {
        if (game.possibleAnotherJump(player, movingMan, jump)) askForAnotherJump();
        else afterMove();
    }

    private void askForAnotherJump() {
        printBoard();
        System.out.print("\n\tAnother jump is possible.");
        readCommand();
    }

    private boolean readCommand() {
        System.out.print("\tCommand? (B2 -> C3, B2 ->, history, score, display, change, save, load, quit)\n\t\t");
        String command = scanner.nextLine().trim();

        if (command.equals("quit")) return false;
        if (command.equals("history")) System.out.println(game.getHistory());
        if (command.matches(moveRegex)) movement(command);
        if (command.matches(hintRegex)) hint(command);
        if (command.equals("score")) printScore();
        if (command.equals("display")) printBoard();
        if (command.equals("save")) saveGame();
        if (command.equals("load")) loadGame();
        if (command.equals("change")) changePlayers();

        return true;
    }

    private void computerMove(Player currentPlayer, Player nextPlayer) {
        game.computerMove(currentPlayer, nextPlayer, depth);
        afterMove();
    }

    private void changePlayers() {
        System.out.println("Which player? (white, black)");
        String whichPlayer = scanner.nextLine().trim();
        System.out.print("Enter player's new name: ");
        String playerName = scanner.nextLine().trim();
        if (whichPlayer.equals("white")) {
            player1.changePlayer(playerName, playerName.contains("ai"));
        } else if (whichPlayer.equals("black")) {
            player2.changePlayer(playerName, playerName.contains("ai"));
        }
    }
}
