package gui;

import gui.brain.Piece;
import shared.Coordinate;
import shared.Player;
import shared.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Objects;

public class BoardPanel extends JLayeredPane implements MouseListener, MouseMotionListener {
    private final GUI guiParent;
    private final Player[] players;
    private final int boardSize;

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

    private Coordinate originalCoordinate;
    private Coordinate newCoordinate;

    public BoardPanel(GUI parent) {
        guiParent = parent;
        players = guiParent.getPlayers();
        boardSize = guiParent.getBoardSize();

        currentPlayer = players[0];

        setPreferredSize(new Dimension(boardSize * 50, boardSize * 50));
        addMouseListener(this);
        addMouseMotionListener(this);

        mainPanel = new JPanel();
        add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        mainPanel.setLayout(new GridLayout(boardSize, boardSize));
        mainPanel.setPreferredSize(getPreferredSize());
        mainPanel.setBounds(10, 10, getPreferredSize().width, getPreferredSize().height);

        // Pieces
        Image resizedWhite = new ImageIcon(Objects.requireNonNull(getClass().getResource("resources/white.png")))
                .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        Image resizedWhiteKing = new ImageIcon(Objects.requireNonNull(getClass().getResource("resources/whiteKing.png")))
                .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        Image resizedBlack = new ImageIcon(Objects.requireNonNull(getClass().getResource("resources/black.png")))
                .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        Image resizedBlackKing = new ImageIcon(Objects.requireNonNull(getClass().getResource("resources/blackKing.png")))
                .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);

        white = new Piece(resizedWhite, true, false);
        whiteKing = new Piece(resizedWhiteKing, true, true);
        black = new Piece(resizedBlack, false, false);
        blackKing = new Piece(resizedBlackKing, false, true);

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
        if (currentPlayer.equals(players[0])) {
            currentPlayer = players[1];
            guiParent.getPlayerLabel().setText("Player 2's turn.");
        } else {
            currentPlayer = players[0];
            guiParent.getPlayerLabel().setText("Player 1's turn.");
        }
        guiParent.getGame().switchPlayers();
    }

    public boolean isOccupied(Coordinate coordinate) {
        return (getCoordinate(coordinate) != null);
    }

    public Piece getCoordinate(Coordinate coordinate) {
        JPanel tile = (JPanel) mainPanel.getComponent(
                coordinate.getRow() * 8 + coordinate.getColumn()
        );
        Component[] components = tile.getComponents();
        for (Component c : components) {
            if (c instanceof JLabel) return (Piece) ((JLabel) c).getIcon();
        }
        return null;
    }

    public int getBoardSize() {
        return boardSize;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO implement hinting on clicking
        System.out.println("Click for hint."); // TODO delete
    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentPiece = null;

        // get the component that was clicked on
        Component componentAt = mainPanel.findComponentAt(e.getX(), e.getY());
        if (componentAt == null) return;
        if (componentAt instanceof JPanel) return; // we only work with JLabels

        if (!guiParent.getGame().canPlayerMoveThis(
                currentPlayer,
                (Piece) ((JLabel) componentAt).getIcon()
        )) return;

        currentPieceOriginalPosition = componentAt.getParent().getLocation();
        x = currentPieceOriginalPosition.x - e.getX();
        y = currentPieceOriginalPosition.y - e.getY();
        originalCoordinate = new Coordinate(
                currentPieceOriginalPosition.y / 50, currentPieceOriginalPosition.x / 50
        );

        currentPiece = (JLabel) componentAt;
        currentPiece.setLocation(currentPieceOriginalPosition);
        currentPiece.setSize(currentPiece.getWidth(), currentPiece.getHeight());

        // make it draggable
        add(currentPiece, JLayeredPane.DRAG_LAYER);
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentPiece == null) return;

        Component componentAt = mainPanel.findComponentAt(e.getX(), e.getY());
        if (componentAt == null) return;
        if (componentAt.getLocation().equals(currentPieceOriginalPosition)) return;
        if (componentAt instanceof JLabel) return;

        newCoordinate = new Coordinate(
                componentAt.getY() / 50, componentAt.getX() / 50
        );
        currentPiece.setVisible(false);

        JPanel originalTile = (JPanel) mainPanel.getComponent(
                originalCoordinate.getRow() * 8 + originalCoordinate.getColumn()
        );
        originalTile.remove(currentPiece);

        JPanel newTile = (JPanel) mainPanel.getComponent(
                newCoordinate.getRow() * 8 + newCoordinate.getColumn()
        );
        boolean valid = false;

        Coordinate jumpedCoordinate = Utils.getJumpedCoordinate(
                currentPlayer.isWhite(), originalCoordinate, newCoordinate
        );

        Piece jumpedMan = null;
        if (jumpedCoordinate != null)
            jumpedMan = getCoordinate(jumpedCoordinate);
        System.out.println("Jumped coord: " + jumpedCoordinate); // TODO delete
        System.out.println("Jumped man: " + jumpedMan); // TODO delete

        if (guiParent.getGame().checkValidityAndMove(
                currentPlayer,
                (Piece) currentPiece.getIcon(),
                jumpedMan,
                originalCoordinate,
                jumpedCoordinate,
                newCoordinate,
                guiParent.getRightPanel().getHistoryDlm()
        )) {
            valid = true;
            if (guiParent.getGame().needsPromotion((Piece) currentPiece.getIcon(), newCoordinate)) {
                if (((Piece) currentPiece.getIcon()).isWhite())
                    currentPiece = new JLabel(whiteKing);
                else currentPiece = new JLabel(blackKing);
            }
            newTile.add(currentPiece);
            if (jumpedCoordinate != null && jumpedMan != null) {
                JPanel jumpedTile = (JPanel) mainPanel.getComponent(
                        jumpedCoordinate.getRow() * 8 + jumpedCoordinate.getColumn()
                );
                Component[] components = jumpedTile.getComponents();
                for (Component c: components) {
                    if (c instanceof JLabel)
                        jumpedTile.remove(c);
                }
            }
        } else {
            originalTile.add(currentPiece);
        }

        currentPiece.setVisible(true);
        if (valid) switchPlayers();

        validate();
        repaint();

        if (guiParent.getGame().shouldEnd(players)) guiParent.endGame();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentPiece == null) return;
        currentPiece.setLocation(e.getX() + x, e.getY() + y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}
