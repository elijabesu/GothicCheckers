package gui;

import shared.Player;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

public class GUI extends JFrame {

    private final JLabel difficultyBar;
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
        load.addActionListener((e) -> saveOrLoad(false));
        save.addActionListener((e) -> saveOrLoad(true));

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

    private void saveOrLoad(boolean save) {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "Text Documents (*.txt)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".txt");
                }
            }
        });

        int selection;
        String str = "";
        String title;

        if (save) {
            fileChooser.setDialogTitle("Saving current game");
            selection = fileChooser.showSaveDialog(this);
            if (selection == JFileChooser.APPROVE_OPTION) {
                if (game.save(checkExtension(fileChooser.getSelectedFile())))
                    str = "Successfully saved the game.";
                else str = "Couldn't save the game.";
            }
            title = "Saving";
        } else {
            fileChooser.setDialogTitle("Loading saved game");
            selection = fileChooser.showOpenDialog(this);
            if (selection == JFileChooser.APPROVE_OPTION) {
                if (game.load(game, player1, player2, checkExtension(fileChooser.getSelectedFile())))
                    str = "Successfully loaded the game.";
                else str = "Couldn't load the game.";
            }
            title = "Loading";
        }

        JOptionPane.showConfirmDialog(
                this, str, title, JOptionPane.DEFAULT_OPTION
        );
    }

    private String checkExtension(File file) {
        String path = file.getAbsolutePath();
        if (!path.endsWith(".txt")) path += ".txt";
        return path;
    }
}
