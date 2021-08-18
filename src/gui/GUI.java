package gui;

import shared.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

public class GUI extends JFrame {

    private JLabel difficultyBar;
    private Game game;
    private BoardPanel boardPanel;

    private Player player1;
    private Player player2;

    private final int boardSize = 8;
    private final int depth = 3;

    public GUI() {
        this.setTitle("Gothic Checkers");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        player1 = new Player("White", true, false);
        player2 = new Player("Black", false, true);

        boardPanel = new BoardPanel(boardSize);
        this.add(boardPanel, BorderLayout.CENTER);

        game = new Game(boardPanel);
        System.out.println("Game started."); // TODO delete

        createMenuBar();

        RightPanel rightPanel = new RightPanel();
        this.add(rightPanel, BorderLayout.EAST);

        this.add(new JPanel(), BorderLayout.WEST);

        difficultyBar = new JLabel("Difficulty: Hard");
        difficultyBar.setBorder(BorderFactory.createEtchedBorder());
        this.add(difficultyBar, BorderLayout.SOUTH);

        this.setSize(new Dimension(600, 500));
//        this.setResizable(false);
    }

    public void startGame() {
        this.setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
//        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(createDifficultyMenu());

        setJMenuBar(menuBar);
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newMenu = new JMenuItem("New");
        JMenuItem load = new JMenuItem("Load");
        JMenuItem save = new JMenuItem("Save");

        newMenu.setMnemonic(KeyEvent.VK_N);
        load.setMnemonic(KeyEvent.VK_L);
        save.setMnemonic(KeyEvent.VK_S);

        newMenu.addActionListener((e) -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure?",
                    "New game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                boardPanel = new BoardPanel(boardSize);
                game = new Game(boardPanel);
                System.out.println("New game started."); // TODO delete
            }
        });
        load.addActionListener((e) -> loadGame());
        save.addActionListener((e) -> saveGame());

        fileMenu.add(newMenu);
        fileMenu.add(load);
        fileMenu.add(save);

        return fileMenu;
    }

    private JMenu createDifficultyMenu() {
        JMenu difMenu = new JMenu("Difficulty");
        difMenu.setMnemonic(KeyEvent.VK_D);
        ButtonGroup difGroup = new ButtonGroup();
        String[] difficulties = {"Easy", "Normal", "Hard"};

        for (String d : difficulties) {
            JRadioButtonMenuItem radio = new JRadioButtonMenuItem(d);
            if (d.equals("Hard")) radio.setSelected(true);
            radio.addItemListener((e) -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    difficultyBar.setText("Difficulty: " + d);
                    game.setDifficulty(Arrays.asList(difficulties).indexOf(d));
                }
            });

            difMenu.add(radio);
            difGroup.add(radio);
        }

        return difMenu;
    }

    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Loading saved game");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        int selection = fileChooser.showOpenDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            if (!game.load(game, player1, player2, fileChooser.getSelectedFile().getAbsolutePath())) {
                JOptionPane.showConfirmDialog(
                        this,
                        "Couldn't load the game.",
                        "Saving",
                        JOptionPane.DEFAULT_OPTION);
            }
        }
    }

    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Saving current game");

        int selection = fileChooser.showSaveDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            String str;
            if (game.save(fileChooser.getSelectedFile().getAbsolutePath()))
                str = "Successfully saved the game.";
            else str = "Couldn't save the game.";

            JOptionPane.showConfirmDialog(
                    this, str, "Saving", JOptionPane.DEFAULT_OPTION
            );
        }
    }
}
