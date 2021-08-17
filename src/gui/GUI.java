package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;

public class GUI extends JFrame {

    private JLabel difficultyBar;

    public GUI() {
        this.setTitle("Gothic Checkers");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createMenuBar();

        BoardPanel boardPanel = new BoardPanel(8);
        this.add(boardPanel);

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

        fileMenu.add(newMenu);
        fileMenu.add(load);
        fileMenu.add(save);

        return fileMenu;
    }

    private JMenu createDifficultyMenu() {
        JMenu difMenu = new JMenu("Difficulty");
        difMenu.setMnemonic(KeyEvent.VK_D);
        ButtonGroup difGroup = new ButtonGroup();

        JRadioButtonMenuItem easy = new JRadioButtonMenuItem("Easy");
        easy.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) { difficultyBar.setText("Difficulty: Easy");}
        });

        JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium");
        medium.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) { difficultyBar.setText("Difficulty: Medium");}
        });

        JRadioButtonMenuItem hard = new JRadioButtonMenuItem("Hard");
        hard.setSelected(true);
        hard.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) { difficultyBar.setText("Difficulty: Hard");}
        });

        difMenu.add(easy);
        difMenu.add(medium);
        difMenu.add(hard);

        difGroup.add(easy);
        difGroup.add(medium);
        difGroup.add(hard);

        return difMenu;
    }
}
