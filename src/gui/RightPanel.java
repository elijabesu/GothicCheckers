package gui;

import gui.brain.Move;
import shared.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class RightPanel extends JPanel {
    private final GUI guiParent;

    private final DefaultListModel<Move> historyDlm;

    public RightPanel(GUI parent) {
        guiParent = parent;
        Player[] players = parent.getPlayers();

        setPreferredSize(new Dimension(150,450));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Players
        JPanel playersPanel = new JPanel();
        playersPanel.setPreferredSize(new Dimension(130,120));
        playersPanel.add(new JLabel("Player 1:"));
        playersPanel.add(createPlayerPanel(players[0]));
        playersPanel.add(new JLabel("Player 2:"));
        playersPanel.add(createPlayerPanel(players[1]));
        add(playersPanel);

        // Pause
        JPanel pausePanel = new JPanel();
        pausePanel.setPreferredSize(new Dimension(130,70));

        guiParent.setPlayerLabel(new JLabel("Player 1's turn."));
        JLabel stateLabel = new JLabel("Game in progress.");

        JButton pauseButton = new JButton("Pause");
        pauseButton.setPreferredSize(new Dimension(130,20));
        pauseButton.addActionListener(e -> {
            if (pauseButton.getText().equals("Pause")) {
                stateLabel.setText("Game paused.");
                pauseButton.setText("Resume");
                guiParent.getGame().setStatus(0);
            } else {
                stateLabel.setText("Game in progress.");
                pauseButton.setText("Pause");
                guiParent.getGame().setStatus(1);
            }
        });

        pausePanel.add(guiParent.getPlayerLabel());
        pausePanel.add(stateLabel);
        pausePanel.add(pauseButton);

        add(pausePanel);

        // History
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setPreferredSize(new Dimension(130,200));

        historyDlm = new DefaultListModel<>();
        JList<Move> historyJList = new JList<>(historyDlm);
        historyJList.addListSelectionListener(e -> {
            int index = historyJList.getSelectedIndex();
            if (index > -1) {
                for (int i = historyDlm.size() - 1; i > index; i--) {
                    historyDlm.remove(i);
                    // UNDO in BoardPanel and Game
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyJList);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        historyPanel.add(new JLabel("History:"));
        historyPanel.add(scrollPane);

        add(historyPanel);

        setFocusable(true);
    }

    public DefaultListModel<Move> getHistoryDlm() {
        return historyDlm;
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
            radioButton.addItemListener(e -> {
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
