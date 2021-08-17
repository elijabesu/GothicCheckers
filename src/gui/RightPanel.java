package gui;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {
    private JTextArea historyArea;
    private final JButton pauseButton;
    public JLabel stateBar;

    public RightPanel() {
        stateBar = new JLabel("Game in progress");
        this.setPreferredSize(new Dimension(150, 450));
//        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener((e) -> {
            if (pauseButton.getText().equals( "Pause")) {
                stateBar.setText("Game paused");
                pauseButton.setText("Resume");
            } else {
                stateBar.setText("Game in progress");
                pauseButton.setText("Pause");
            }
        });
        pauseButton.setPreferredSize(new Dimension(130, 20));

        this.add(playersPanel());
        this.add(pausePanel());
        this.add(historyPanel());

        this.setFocusable(true);
    }

    private JPanel playersPanel() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(130, 120));
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Player 1:"));
        panel.add(createPlayerPanel(true));

        panel.add(new JLabel("Player 2:"));
        panel.add(createPlayerPanel(false));

        return panel;
    }

    private JPanel historyPanel() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(130, 250));
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("History:"));

        historyArea = new JTextArea(10, 10);
        historyArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane);

        return panel;
    }

    private JPanel pausePanel() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(130, 50));
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(stateBar);
        panel.add(pauseButton);

//        panel.setSize(150, 50);

        return panel;
    }

    private JPanel createPlayerPanel(boolean isHuman) {

        JPanel playerPanel = new JPanel();
        ButtonGroup playerGroup = new ButtonGroup();

        JRadioButton ai = new JRadioButton("AI");
        JRadioButton human = new JRadioButton("Human");

        if (isHuman) human.setSelected(true);
        else ai.setSelected(true);

        playerGroup.add(human);
        playerGroup.add(ai);

        playerPanel.add(human);
        playerPanel.add(ai);

        return playerPanel;
    }
}
