package gui;

import javax.swing.*;
import java.awt.*;

public class HelpDialog extends JDialog {
    public HelpDialog(GUI parent) {
        super(parent, "How to play");

        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        String helpText = "<html>Pieces can only be moved in diagonal directions." +
                        " They may jump over an opponents piece by moving" +
                        " to the space behind the opponent, if your piece," +
                        " the opponents piece, and the empty square are all" +
                        " in a diagonal line.  You may also make multiple" +
                        " jumps with the same piece in a single turn if the" +
                        " game permits you to. To move a piece select the" +
                        " space on the board that the piece is in. Then Select" +
                        " the space you would like to move this piece to. If" +
                        " it is a valid move, the board will update. If not," +
                        " then you will have to select another piece to move." +
                        " At the end of your turn (when you have moved the" +
                        " piece you want), click the Next Move button to tell" +
                        " your opponent to move.  When you reach the row" +
                        " farthest from your starting row, the piece that" +
                        " lands there will be kinged. This means that this" +
                        " piece can now move backwards. To win, capture all" +
                        " of your opponents pieces by jumping over them to the" +
                        " space behind them.";

        JLabel helpLabel = new JLabel(helpText) {
            public Dimension getPreferredSize() {
                return new Dimension(500, 200);
            }
        };
//        helpLabel.setVerticalAlignment(SwingConstants.CENTER);
        helpLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.add(helpLabel);

        this.add(rightPanel);

//        this.add(new JLabel("How to play"));
//        this.setSize(300, 400);

//        JTextArea textArea = new JTextArea();
//        textArea.setLineWrap(true);
//        textArea.setEditable(false);
//
//        textArea.setText(helpText);
//
////        textArea.setFont(new Font("Arial",Font.BOLD,12));
//
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//
//        this.add(scrollPane);
        this.pack();

        int x = parent.getX() + parent.getWidth() / 2 - getWidth() / 2;
        int y = parent.getY() + parent.getHeight() / 2 - getHeight() / 2;
        this.setLocation(x, y);

    }
}
