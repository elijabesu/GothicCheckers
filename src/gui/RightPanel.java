package gui;

import shared.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class RightPanel extends JPanel {
    private JTextArea historyArea;
    private final JButton pauseButton;
    private final JLabel stateLabel;
    private final Player[] players;

    public RightPanel(Player[] players) {
        this.players = players;

        stateLabel = new JLabel("Game in progress");
        this.setPreferredSize(new Dimension(150, 450));
//        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener((e) -> {
            if (pauseButton.getText().equals( "Pause")) {
                stateLabel.setText("Game paused");
                pauseButton.setText("Resume");
            } else {
                stateLabel.setText("Game in progress");
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
        panel.add(createPlayerPanel(players[0]));

        panel.add(new JLabel("Player 2:"));
        panel.add(createPlayerPanel(players[1]));

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
        panel.add(stateLabel);
        panel.add(pauseButton);

//        panel.setSize(150, 50);

        return panel;
    }

    private JPanel createPlayerPanel(Player player) {
        JPanel playerPanel = new JPanel();
        ButtonGroup playerGroup = new ButtonGroup();

        for (int i = 0; i < 2; i++) {
            String buttonText;
            boolean isAI;

            if (i == 0) {
                buttonText = "AI";
                isAI = true;
            } else {
                buttonText = "Human";
                isAI = false;
            }

            JRadioButton radioButton = new JRadioButton(buttonText);
            if (player.isComputer() == isAI) radioButton.setSelected(true);
            radioButton.addItemListener((e) -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    player.changePlayer(player.getName(), isAI);
                }
            });

            playerGroup.add(radioButton);
            playerPanel.add(radioButton);
        }

        return playerPanel;
    }
}
