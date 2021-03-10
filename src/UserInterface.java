import java.util.List;
import java.util.Scanner;

public class UserInterface {
    /*
    Interacting with the player(s).
    */
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
        askForDifficulty();
        printBoard();

        while (true) {
            Player currentPlayer;
            Player nextPlayer;

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
                boolean toContinue = true;
                while (toContinue) {
                    System.out.print("Intervene? (y/n) ");
                    if (scanner.nextLine().trim().equals("y")) {
                        if (!readCommand()) {
                            toContinue = false;
                            break;
                        }
                    } else break;
                }
                if (!toContinue) break;
            }
        }
        endGame();
    }

    // INTERACTING WITH THE PLAYER
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

    private void askForDifficulty() {
        System.out.print("Choose a difficulty (easy, normal, hard): ");
        String difficulty = scanner.nextLine().trim();
        if (difficulty.equals("easy")) game.setDifficulty(0);
        else if (difficulty.equals("normal")) game.setDifficulty(1);
        else if (difficulty.equals("hard")) game.setDifficulty(2);
        else askForDifficulty();
    }

    private void printBoard() {
        System.out.print(game.displayBoard());
    }

    private boolean readCommand() {
        System.out.print("\tCommand? (B2 -> C3, B2 ->, history, score, display, change, save, load, quit)\n\t\t");
        String command = scanner.nextLine().trim();

        if (command.equals("quit")) return false;
        if (command.equals("history")) System.out.println(game.getHistory());
        if (command.matches(moveRegex)) playerMove(command);
        if (command.matches(hintRegex)) hint(command);
        if (command.equals("score")) printScore();
        if (command.equals("display")) printBoard();
        if (command.equals("save")) saveGame();
        if (command.equals("load")) loadGame();
        if (command.equals("change")) changePlayers();

        return true;
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

    private Player whichPlayer() {
        if (game.getPlayerBool()) return player1;
        return player2;
    }

    private void printScore() {
        System.out.println(player1);
        System.out.println(player2);
    }

    private void endGame() {
        if (player1.getPoints() > player2.getPoints()) System.out.println(player1.getName() + " won!");
        else if (player1.getPoints() < player2.getPoints()) System.out.println(player2.getName() + " won!");
        else System.out.println("It's a draw!");
    }

    // MOVEMENT
    private void computerMove(Player currentPlayer, Player nextPlayer) {
        game.computerMove(currentPlayer, nextPlayer, depth);
        afterMove();
    }

    private void playerMove(String movement) {
        Coordinate origCoordinate = Utils.getCoordinate(movement, 1, 0);

        Pieces man = game.getManByPosition(origCoordinate);

        if (man == Pieces.EMPTY) printInvalidMove();
        else {
            List<Coordinate> coordinates = Utils.getCoordinates(movement, whichPlayer().isWhite());
            if (man.isKing() || Utils.containsMinusCoordinate(coordinates)) move(man, coordinates);
            else jump(man, coordinates);
        }
    }

    private void move(Pieces man, List<Coordinate> coordinates) {
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

    private void afterMove() {
        printBoard();
        game.switchPlayers();
    }

    private void jump(Pieces man, List<Coordinate> coordinates) {
        Jump gameJump = game.jump(whichPlayer(), man, coordinates);
        if (gameJump != null) afterJump(whichPlayer(), man, gameJump);
        else printInvalidMove();
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

    private static void printInvalidMove() {
        System.out.println("\tInvalid move.");
    }

    private void hint(String command) {
        Coordinate coordinate = Utils.getCoordinate(command, 1, 0);
        Pieces man = game.getManByPosition(coordinate);
        if (man == Pieces.EMPTY) printInvalidMove();
        else {
            Player otherPlayer;
            if (whichPlayer().equals(player1)) otherPlayer = player2;
            else otherPlayer = player1;
            String hint = game.hint(whichPlayer(), otherPlayer, man, coordinate, depth);
            if (hint.length() == 0) printInvalidMove();
            else {
                System.out.println("Possible moves: " + hint);
            }
        }
    }

    // HISTORY
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
}
