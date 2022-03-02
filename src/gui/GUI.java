package gui;

import gui.brain.Game;
import gui.brain.Piece;
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
    private BoardPanel boardPanel;
    private RightPanel rightPanel;

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
        startNewGui();

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
        boardPanel = new BoardPanel();
        getContentPane().add(boardPanel, BorderLayout.CENTER);

        // RIGHT
        rightPanel = new RightPanel();
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
                // TODO new game
                game = new Game();
                clearGui();
                startNewGui();
                getContentPane().repaint();
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
        helpMenu.addMenuListener(new CustomMenuListener());

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    class CustomMenuListener implements MenuListener {
        @Override
        public void menuSelected(MenuEvent e) {
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

    class HelpDialog extends JDialog {
        public HelpDialog(GUI parent) {
            super(parent, "How to play");
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
            add(scrollPane);
            pack();

            int x = parent.getX() + parent.getWidth() / 2 - getWidth() / 2;
            int y = parent.getY() + parent.getHeight() / 2 - getHeight() / 2;
            setLocation(x, y);
            setModal(true);
        }
    }

    class BoardPanel extends JLayeredPane implements MouseListener, MouseMotionListener {
        private final JPanel mainPanel;

        private final Piece white;
        private final Piece whiteKing;
        private final Piece black;
        private final Piece blackKing;

        private Player currentPlayer;
        private JLabel currentPiece;
        private Point currentPieceOriginalPosition;
        private int x;
        private int y;

        private Point originalCoordinate;
        private Point newCoordinate;

        public BoardPanel() {
            currentPlayer = players[0];

            setPreferredSize(new Dimension(boardSize * 50, boardSize * 50));
            addMouseListener(this);
            addMouseMotionListener(this);

            mainPanel = new JPanel();
            add(mainPanel, JLayeredPane.DEFAULT_LAYER);
            mainPanel.setLayout(new GridLayout(boardSize, boardSize));
            mainPanel.setPreferredSize(getPreferredSize());
            mainPanel.setBounds(10,10, getPreferredSize().width, getPreferredSize().height);

            // Pieces
            Image resizedWhite = new ImageIcon(getClass().getResource("resources/white.png"))
                    .getImage().getScaledInstance(45,45,Image.SCALE_SMOOTH);
            Image resizedWhiteKing = new ImageIcon(getClass().getResource("resources/whiteKing.png"))
                    .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            Image resizedBlack = new ImageIcon(getClass().getResource("resources/black.png"))
                    .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            Image resizedBlackKing = new ImageIcon(getClass().getResource("resources/blackKing.png"))
                    .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);

            white = new Piece(resizedWhite, true, false);
            whiteKing = new Piece(resizedWhiteKing, true, true);
            black = new Piece(resizedBlack, false, false);
            blackKing = new Piece(resizedBlackKing, false, true);
            game.setPieces(new Piece[] {white, whiteKing, black, blackKing});

            // generate board tiles
            for (int i = 0; i < boardSize * boardSize; i++) {
                JPanel tile = new JPanel(new BorderLayout());
                mainPanel.add(tile);

                int row = (i / boardSize) % 2;
                if (row == 0) tile.setBackground(i % 2 == 0 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                else tile.setBackground(i % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            }

            // place men
            for (int row = 0; row < boardSize; row++) {
                for (int column = 0; column < boardSize; column++) {
                    JPanel tile = (JPanel) mainPanel.getComponent(row * 8 + column);
                    JLabel piece = null;
                    if (row < 2) {
                        piece = new JLabel(black);
                    } else if (row > 5) {
                        piece = new JLabel(white);
                    }
                    if (piece != null) tile.add(piece);
                }
            }
        }

        private void switchPlayers() {
            if (currentPlayer.equals(players[0])) currentPlayer = players[1];
            else currentPlayer = players[0];
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("Click for hint."); // TODO delete
        }

        @Override
        public void mousePressed(MouseEvent e) {
            currentPiece = null;

            // get the component that was clicked on
            Component componentAt = mainPanel.findComponentAt(e.getX(), e.getY());
            if (componentAt == null) return;
            if (componentAt instanceof JPanel) return; // we only work with JLabels

            if (!game.canPlayerMoveThis(
                    (Piece) ((JLabel) componentAt).getIcon(),
                    currentPlayer
            )) return;

            currentPieceOriginalPosition = componentAt.getParent().getLocation();
            x = currentPieceOriginalPosition.x - e.getX();
            y = currentPieceOriginalPosition.y - e.getY();
            originalCoordinate = new Point(currentPieceOriginalPosition.x / 50, currentPieceOriginalPosition.y / 50);

            currentPiece = (JLabel) componentAt;
            currentPiece.setLocation(currentPieceOriginalPosition);
            currentPiece.setSize(currentPiece.getWidth(), currentPiece.getHeight());

            // make it draggable
            add(currentPiece, JLayeredPane.DRAG_LAYER);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (currentPiece == null) return;

            Component componentAt = mainPanel.findComponentAt(e.getX(), e.getY());
            if (componentAt == null) return;
            if (componentAt.getLocation().equals(currentPieceOriginalPosition)) return;
            if (componentAt instanceof JLabel) return;

            currentPiece.setVisible(false);
            newCoordinate = new Point(componentAt.getX() / 50, componentAt.getY() / 50);

            JPanel originalTile = (JPanel) mainPanel.getComponent(
                    originalCoordinate.y * 8 + originalCoordinate.x
            );
            originalTile.remove(currentPiece);

            JPanel newTile = (JPanel) mainPanel.getComponent(
                    newCoordinate.y * 8 + newCoordinate.x
            );
            boolean valid = false;

            if (game.checkValidityAndMove(
                    (Piece) currentPiece.getIcon(),
                    originalCoordinate,
                    newCoordinate,
                    currentPlayer,
                    rightPanel.getHistoryDlm()
            )) {
                valid = true;
                newTile.add(currentPiece);
            } else {
                originalTile.add(currentPiece);
            }

            currentPiece.setVisible(true);
            if (valid) switchPlayers();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (currentPiece == null) return;
            currentPiece.setLocation(e.getX() + x, e.getY() + y);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    class RightPanel extends JPanel {
        private final DefaultListModel<String> historyDlm;
        public RightPanel() {
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
            pausePanel.setPreferredSize(new Dimension(130,50));

            JLabel stateLabel = new JLabel("Game in progress.");
            pausePanel.add(stateLabel);

            JButton pauseButton = new JButton("Pause");
            pauseButton.setPreferredSize(new Dimension(130,20));
            pauseButton.addActionListener(e -> {
                if (pauseButton.getText().equals("Pause")) {
                    stateLabel.setText("Game paused.");
                    pauseButton.setText("Resume");
                } else {
                    stateLabel.setText("Game in progress.");
                    pauseButton.setText("Pause");
                }
            });
            pausePanel.add(pauseButton);

            add(pausePanel);

            // History
            JPanel historyPanel = new JPanel();
            historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
            historyPanel.setPreferredSize(new Dimension(130,200));
            historyPanel.add(new JLabel("History:"));

            historyDlm = new DefaultListModel<>();
            JScrollPane scrollPane = new JScrollPane(new JList(historyDlm));
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            historyPanel.add(scrollPane);

            add(historyPanel);

            setFocusable(true);
        }

        public DefaultListModel<String> getHistoryDlm() {
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

    public void startGame() { this.setVisible(true); }

}
