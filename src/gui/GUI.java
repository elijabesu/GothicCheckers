package gui;

import gui.brain.Game;
import shared.Player;
import shared.Utils;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

public class GUI extends JFrame {

    private final JLabel difficultyBar;
    private Game game;
    private BoardPanel boardPanel;

    private Player[] players;

    private final int boardSize = 8;
    private final int depth = 3;

    public GUI() {
        this.setTitle("Gothic Checkers");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        players = new Player[] {
                new Player("White", true, false),
                new Player("Black", false, false)
        };

        game = new Game();
        boardPanel = new BoardPanel(boardSize, game, players);
        this.getContentPane().add(boardPanel, BorderLayout.CENTER);

        System.out.println("Game started."); // TODO delete

        createMenuBar();

        RightPanel rightPanel = new RightPanel(players);
        this.getContentPane().add(rightPanel, BorderLayout.EAST);

        this.getContentPane().add(new JPanel(), BorderLayout.WEST);

        difficultyBar = new JLabel("Difficulty: Hard");
        difficultyBar.setBorder(BorderFactory.createEtchedBorder());
        this.getContentPane().add(difficultyBar, BorderLayout.SOUTH);

        this.setSize(new Dimension(600, 500));
//        this.setResizable(false);
    }

    public void startGame() {
        this.setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createDifficultyMenu());
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(createHelpMenu());

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
                game = new Game();
                boardPanel = new BoardPanel(boardSize, game, players);
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

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        // TODO help menu
//        HelpFrame help = new HelpFrame(this);

        helpMenu.addMenuListener(new CustomMenuListener());

        return helpMenu;
    }

    class CustomMenuListener implements MenuListener {

        @Override
        public void menuSelected(MenuEvent e) {
//            HelpFrame help = new HelpFrame(GUI.this);
//            help.setVisible(true);
            HelpDialog helpDialog = new HelpDialog(GUI.this);
            helpDialog.setVisible(true);
        }

        @Override
        public void menuDeselected(MenuEvent e) {

        }

        @Override
        public void menuCanceled(MenuEvent e) {

        }
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
                if (game.save(Utils.checkExtension(fileChooser.getSelectedFile())))
                    str = "Successfully saved the game.";
                else str = "Couldn't save the game.";
            } else str = "Saving cancelled";
            title = "Saving";
        } else {
            fileChooser.setDialogTitle("Loading saved game");
            selection = fileChooser.showOpenDialog(this);
            if (selection == JFileChooser.APPROVE_OPTION) {
                if (game.load(game, players, Utils.checkExtension(fileChooser.getSelectedFile())))
                    str = "Successfully loaded the game.";
                else str = "Couldn't load the game.";
            } else str = "Loading cancelled";
            title = "Loading";
        }

        JOptionPane.showConfirmDialog(
                this, str, title, JOptionPane.DEFAULT_OPTION
        );
    }
}
