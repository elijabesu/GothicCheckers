package gui;

import gui.brain.Game;
import shared.Player;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class GUI extends JFrame {

    private final Player[] players;
    private Game game;

    private JLabel difficultyBar;
    private JLabel playerLabel;
    private BoardPanel boardPanel;
    private RightPanel rightPanel;

    private final int boardSize = 8;
    private final int depth = 3;

    public GUI() {
        this.setTitle("Gothic Checkers");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        players = new Player[] {
                new Player("P1", true, false),
                new Player("P2", false, false)
        };
        startNewGui();
        game = new Game(boardPanel);

        setSize(new Dimension(600, 500));
        setResizable(false);
    }

    private void startNewGui() {
        // BOTTOM BAR
        difficultyBar = new JLabel("Difficulty: Hard");
        difficultyBar.setBorder(BorderFactory.createEtchedBorder());
        getContentPane().add(difficultyBar, BorderLayout.SOUTH);

        // TOP BAR
        createMenuBar();

        // CENTER
        boardPanel = new BoardPanel(this);
        getContentPane().add(boardPanel, BorderLayout.CENTER);

        // RIGHT
        rightPanel = new RightPanel(this);
        getContentPane().add(rightPanel, BorderLayout.EAST);
    }

    private void clearGui() {
        getContentPane().removeAll();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File
        JMenu fileMenu = new JMenu("File");
        JMenuItem newGame = new JMenuItem("New");
        JMenuItem saveGame = new JMenuItem("Save");
        JMenuItem loadGame = new JMenuItem("Load");

        fileMenu.setMnemonic(KeyEvent.VK_F);
        newGame.setMnemonic(KeyEvent.VK_N);
        saveGame.setMnemonic(KeyEvent.VK_S);
        loadGame.setMnemonic(KeyEvent.VK_L);

        newGame.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Start a new game?",
                    "New game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                newGame();
            }
        });
//        saveGame.addActionListener(); TODO save game
//        loadGame.addActionListener(); TODO load game

        fileMenu.add(newGame);
        fileMenu.add(saveGame);
        fileMenu.add(loadGame);

        menuBar.add(fileMenu);

        // Difficulty
        JMenu difficultyMenu = new JMenu("Difficulty");
        difficultyMenu.setMnemonic(KeyEvent.VK_D);
        ButtonGroup difficultyGroup = new ButtonGroup();
        String[] difficulties = {"Easy", "Normal", "Hard"};
        for (String d: difficulties) {
            JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem(d);
            if (d.equals("Hard")) radioButtonMenuItem.setSelected(true);
            radioButtonMenuItem.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    difficultyBar.setText("Difficulty: " + d);
                    game.setDifficulty(Arrays.asList(difficulties).indexOf(d));
                }
            });

            difficultyMenu.add(radioButtonMenuItem);
            difficultyGroup.add(radioButtonMenuItem);
        }

        menuBar.add(difficultyMenu);

        // Help
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JDialog helpDialog = createHelpDialog();
                helpDialog.setVisible(true);
            }

            @Override
            public void menuDeselected(MenuEvent e) {}

            @Override
            public void menuCanceled(MenuEvent e) {}
        });

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JDialog createHelpDialog() {
        JDialog helpDialog = new JDialog(this, "How to play");
        String helpText = "<html><b>Goal:</b><br>Capture all of opponent's stones.<br><br>" +
                "<b>Start:</b><br>" +
                "At the beginning of the game, there are stones in the two outer ones rows.<br><br>" +
                "<b>Rules:</b><br>" +
                "- Players take turns.<br>" +
                "- During a player's turn, they may move one of their stones one square to an adjacent free" +
                " square that is in front of, beside or diagonally in front of it.<br>" +
                "- A stone can also capture an opponent's stone by jumping over it. The opponent's stone must" +
                "be on an adjacent square and immediately behind it must be a free square.<br>" +
                "- During the jump, the stone is moved to this free square and the opponent stone is removed " +
                "from the board. Stones may only jump forward, diagonally forward, or sideways.<br>" +
                "- Multiple jumps are allowed.<br>" +
                "- If a stone ends its turn in the last row on the opposite side of the board, it is promoted " +
                "to a Queen.<br>" +
                "- The Queen may move in all directions (even backwards) by any number of free squares. The " +
                "Queen can take an opponent's stone by jumping over it, and can skip any number of free squares " +
                "in front of the stone, the stone of interest, and any number of free squares behind this stone." +
                "<br>" +
                "- Capturing opponent's stones is mandatory and the player must capture as many stones as " +
                "possible if there are more jumps available.<br>" +
                "- If a player cannot make a move, his opponent continues to play.<br><br>" +
                "<b>End:</b><br>" +
                "The player who captures all the opponent's stones wins.<br>" +
                "If no stone is captured for 30 moves, the player with more stones wins."
                ;

        JTextPane helpPane = new JTextPane();
        helpPane.setPreferredSize(new Dimension(400,300));
        helpPane.setContentType("text/html");
        helpPane.setText(helpText);
        helpPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(helpPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        helpDialog.add(scrollPane);
        helpDialog.pack();

        int x = getX() + getWidth() / 2 - helpDialog.getWidth() / 2;
        int y = getY() + getHeight() / 2 - helpDialog.getHeight() / 2;
        helpDialog.setLocation(x, y);
        helpDialog.setModal(true);
        return helpDialog;
    }

    public void startGame() { this.setVisible(true); }

    public void endGame() {
        String text = "It's a draw!";
        int index = -1;
        if (players[0].getPoints() > players[1].getPoints())
            index = 0;
        else if (players[0].getPoints() < players[1].getPoints())
            index = 1;
        if (index != -1)
            text = players[index].getName() + " won with " + players[index].getPoints() + " point(s)!";

        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        String[] buttons = {"New game"};

        int result = JOptionPane.showOptionDialog(
                this,
                label,
                "Game ended",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                buttons, buttons[0]
        );
        if (result == 0) newGame();
    }

    private void newGame() {
        clearGui();
        startNewGui();
        game = new Game(boardPanel);
        validate();
        getContentPane().repaint();
    }

    // getters for BoardPanel and RightPanel
    public Player[] getPlayers() { return players; }

    public Game getGame() { return game; }

    public int getBoardSize() { return boardSize; }

    public RightPanel getRightPanel() { return rightPanel; }

    public JLabel getPlayerLabel() {
        return playerLabel;
    }

    public void setPlayerLabel(JLabel playerLabel) {
        this.playerLabel = playerLabel;
    }
}
