package gui;

import gui.brain.Game;
import gui.brain.Piece;
import shared.Player;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

public class old_GUI extends JFrame {

    private final JLabel difficultyBar;
    private Game game;
    private BoardPanel boardPanel;
    private RightPanel rightPanel;

    private final Player[] players;

    private final int boardSize = 8;
    private final int depth = 3;

    public old_GUI() {
        this.setTitle("Gothic Checkers");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        players = new Player[] {
                new Player("White", true, false),
                new Player("Black", false, false)
        };

        game = new Game();
        boardPanel = new BoardPanel();
        this.getContentPane().add(boardPanel, BorderLayout.CENTER);

        System.out.println("Game started."); // TODO delete

        createMenuBar();

        rightPanel = new RightPanel();
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
                boardPanel = new BoardPanel();
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
            HelpDialog helpDialog = new HelpDialog(old_GUI.this);
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
        String str;
        String title;

        if (save) {
            fileChooser.setDialogTitle("Saving current game");
            selection = fileChooser.showSaveDialog(this);
            if (selection == JFileChooser.APPROVE_OPTION) {
//                if (game.save(Utils.checkExtension(fileChooser.getSelectedFile())))
//                    str = "Successfully saved the game.";
//                else {
                    str = "Couldn't save the game.";
//                }
            } else str = "Saving cancelled";
            title = "Saving";
        } else {
            fileChooser.setDialogTitle("Loading saved game");
            selection = fileChooser.showOpenDialog(this);
            if (selection == JFileChooser.APPROVE_OPTION) {
//                if (game.load(game, players, Utils.checkExtension(fileChooser.getSelectedFile())))
//                    str = "Successfully loaded the game.";
//                else {
                    str = "Couldn't load the game.";
//                }
            } else str = "Loading cancelled";
            title = "Loading";
        }

        JOptionPane.showConfirmDialog(
                this, str, title, JOptionPane.DEFAULT_OPTION
        );
    }

    class BoardPanel extends JLayeredPane implements MouseListener, MouseMotionListener {
        private final JPanel boardPanel;

        private Player currentPlayer;
        private JLabel currentPiece;
        private Point currentPieceOriginalPosition;
        private int x;
        private int y;

        private Piece white;
        private Piece whiteKing;
        private Piece black;
        private Piece blackKing;

        public BoardPanel() {
            prepareImages();
            game.setPieces(new Piece[] {white, whiteKing, black, blackKing});

            Dimension boardDimension = new Dimension(400, 400);
            setPreferredSize(boardDimension);
            addMouseListener(this);
            addMouseMotionListener(this);

            boardPanel = new JPanel();
            this.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
            boardPanel.setLayout(new GridLayout(boardSize, boardSize));
            boardPanel.setPreferredSize(boardDimension);
            boardPanel.setBounds(0, 0, boardDimension.width, boardDimension.height);

            generateTiles();
            placeMen();

            currentPlayer = players[0];
        }

        private void prepareImages() {
            Image resizedWhite = new ImageIcon(getClass().getResource("resources/white.png"))
                    .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            Image resizedBlack = new ImageIcon(getClass().getResource("resources/black.png"))
                    .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            Image resizedWhiteKing = new ImageIcon(getClass().getResource("resources/whiteKing.png"))
                    .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            Image resizedBlackKing = new ImageIcon(getClass().getResource("resources/blackKing.png"))
                    .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);

            white = new Piece(resizedWhite, true, false);
            black = new Piece(resizedBlack, false, false);
            whiteKing = new Piece(resizedWhiteKing, true, true);
            blackKing = new Piece(resizedBlackKing, false, true);
        }

        private void generateTiles() {
            for (int i = 0; i < boardSize * boardSize; i++) {
                JPanel tile = new JPanel(new BorderLayout());
                boardPanel.add(tile);

                int row = (i / boardSize) % 2;
//            int col = ((i / boardSize) % 2) * 10;
//            if (col > 0) col -= 10;
                if (row == 0) tile.setBackground(i % 2 == 0 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                else tile.setBackground(i % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            }
        }

        private void placeMen() {
            for (int row = 0; row < boardSize; row++) {
                for (int column = 0; column < boardSize; column++) {
                    JPanel tile = (JPanel) boardPanel.getComponent(row * 8 + column);
                    JLabel piece = null;
                    if (row < 2) {
//                    coordinates[row][column] = Pieces.WHITE;
                        piece = new JLabel(black);
                    } else if (row > 5) {
//                    coordinates[row][column] = Pieces.BLACK;
                        piece = new JLabel(white);
                    }
//                } else coordinates[row][column] = Pieces.EMPTY;
                    if (piece != null) tile.add(piece);
                }
            }
        }

        private void switchPlayers() {
            if (currentPlayer == players[0]) currentPlayer = players[1];
            else currentPlayer = players[0];
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            currentPiece = null;

            // get the component where mouse was pressed
            Component componentAt = boardPanel.findComponentAt(e.getX(), e.getY());

            // we only need JLabels
            if (componentAt instanceof JPanel) return;
            if (!game.canPlayerMoveThis(
                    (Piece) ((JLabel) componentAt).getIcon(),
                    currentPlayer)
            ) return;

            Point parentLocation = componentAt.getParent().getLocation();
            x = parentLocation.x - e.getX();
            y = parentLocation.y - e.getY();

            // from Component object to JLabel
            currentPiece = (JLabel) componentAt;
            currentPieceOriginalPosition = parentLocation;
            currentPiece.setLocation(e.getX() + x, e.getY() + y);
            currentPiece.setSize(currentPiece.getWidth(), currentPiece.getHeight());

            // allow JLabel to be dragged
            this.add(currentPiece, JLayeredPane.DRAG_LAYER);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (currentPiece == null) return;

            Component componentAt = boardPanel.findComponentAt(e.getX(), e.getY());
            if (componentAt.getLocation().equals(currentPieceOriginalPosition)) return;

            currentPiece.setVisible(false);

            Container parent;
            boolean valid = true;

//            Piece pc = (Piece) currentPiece.getIcon();
            if (componentAt instanceof JLabel) {
//                if (!game.checkValidityAndMove(
//                        (Piece) ((JLabel) componentAt).getIcon(),
//                        (Piece) currentPiece.getIcon(),
//                        currentPieceOriginalPosition,
//                        componentAt.getLocation(),
//                        currentPlayer, rightPanel
//                )) {
                if (valid) {
                    valid = false;
                    parent = (Container) boardPanel.findComponentAt(
                            currentPieceOriginalPosition.x, currentPieceOriginalPosition.y
                    );
                } else {
                    parent = componentAt.getParent();
                    parent.remove(0);
                }
            } else {
                parent = (Container) componentAt;
            }

            parent.add(currentPiece);

            currentPiece.setVisible(true);
            if (valid) switchPlayers();
//            rightPanel.appendToHistory(pc.toString() + " moved.\n");
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

    class HelpDialog extends JDialog {
        public HelpDialog(old_GUI parent) {
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
            helpLabel.setHorizontalAlignment(SwingConstants.LEFT);

            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
            rightPanel.add(helpLabel);

            this.add(rightPanel);
            this.pack();

            int x = parent.getX() + parent.getWidth() / 2 - getWidth() / 2;
            int y = parent.getY() + parent.getHeight() / 2 - getHeight() / 2;
            this.setLocation(x, y);

        }
    }

    public class RightPanel extends JPanel {
        private JTextArea historyArea;
        private final JButton pauseButton;
        private final JLabel stateLabel;

        public RightPanel() {
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
            historyArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(historyArea);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            panel.add(scrollPane);

            return panel;
        }

        public void appendToHistory(String text) {
            historyArea.append(text);
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

}
